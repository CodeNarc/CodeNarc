/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Enforce that all static methods within each visibility level (public, protected, private) are above
 * all instance methods within that same visibility level. In other words, public static must be above
 * public instance methods, protected static must be above protected instance methods and private static
 * must be above private instance methods.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more (comma-separated) instance
 * method names that should be ignored in the visibility level ordering (i.e., that should not cause a rule
 * violation). The name(s) may optionally include wildcard characters ('*' or '?'). A rule violation is still
 * triggered if an ignored instance method appears after the first static method within the visibility level.
 * In other words, all ignored instance methods must appear above all static methods within each visibility level).
 *
 * @author Chris Mair
 * @author Peter Thomas
 */
class StaticMethodsBeforeInstanceMethodsRule extends AbstractAstVisitorRule {

    String name = 'StaticMethodsBeforeInstanceMethods'
    int priority = 3
    Class astVisitorClass = StaticMethodsBeforeInstanceMethodsAstVisitor
    String ignoreMethodNames
}

class StaticMethodsBeforeInstanceMethodsAstVisitor extends AbstractAstVisitor {

    private static enum Visibility {
        PUBLIC('public'), PROTECTED('protected'), PRIVATE('private')

        private final String name

        private Visibility(String name) {
            this.name = name
        }

        String getName() {
            return name
        }
    }

    private final Map<Visibility, Boolean> hasDeclaredInstanceMethod = [:]
    private final Map<Visibility, Boolean> hasDeclaredStaticMethod = [:]

    @Override
    protected void visitMethodComplete(MethodNode methodNode) {
        if (!methodNode.synthetic && isNotGeneratedCode(methodNode)) {
            Visibility visibility = getVisibility(methodNode)
            if (methodNode.static) {
                hasDeclaredStaticMethod[visibility] = true
                if (hasDeclaredInstanceMethod[visibility]) {
                    addMethodViolation(methodNode, visibility)
                }
            } else {
                boolean isNameIgnored = new WildcardPattern(rule.ignoreMethodNames, false).matches(methodNode.name)
                if (!isNameIgnored || hasDeclaredStaticMethod[visibility]) {
                    hasDeclaredInstanceMethod[visibility] = true
                }
            }
        }
        super.visitMethodComplete(methodNode)
    }

    private Visibility getVisibility(MethodNode methodNode) {
        return methodNode.public ? Visibility.PUBLIC : (methodNode.protected ? Visibility.PROTECTED : Visibility.PRIVATE)
    }

    private void addMethodViolation(MethodNode methodNode, Visibility visibility) {
        addViolation(methodNode, "The ${visibility.name} static method $methodNode.name in class ${getCurrentClassName()} is declared after a ${visibility.name} instance method")
    }

}
