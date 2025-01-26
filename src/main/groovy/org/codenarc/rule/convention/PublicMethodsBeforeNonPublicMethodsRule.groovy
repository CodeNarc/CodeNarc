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
 * Enforce that all public methods are above protected and private methods.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more (comma-separated) non-public method
 * names that should be ignored (i.e., that should not cause a rule violation). The name(s) may optionally include
 * wildcard characters ('*' or '?'). A rule violation is still triggered if an ignored non-public method appears after
 * the first public method. In other words, all ignored non-public methods must appear above all public methods).
 *
 * @author Chris Mair
 * @author Peter Thomas
 */
class PublicMethodsBeforeNonPublicMethodsRule extends AbstractAstVisitorRule {

    String name = 'PublicMethodsBeforeNonPublicMethods'
    int priority = 3
    Class astVisitorClass = PublicMethodsBeforeNonPublicMethodsAstVisitor
    String ignoreMethodNames
}

class PublicMethodsBeforeNonPublicMethodsAstVisitor extends AbstractAstVisitor {

    private boolean hasDeclaredNonPublicMethod = false
    private boolean hasDeclaredPublicMethod = false

    @Override
    protected void visitMethodComplete(MethodNode node) {
        if (node.public) {
            hasDeclaredPublicMethod = true
            if (hasDeclaredNonPublicMethod) {
                addViolation(node, "The public method $node.name in class $currentClassName is declared after a non-public method")
            }
        }
        else {
            boolean isNameIgnored = new WildcardPattern(rule.ignoreMethodNames, false).matches(node.name)
            if (!node.synthetic) {
                if (!isNameIgnored || hasDeclaredPublicMethod) {
                    hasDeclaredNonPublicMethod = true
                }
            }
        }
        super.visitMethodComplete(node)
    }
}
