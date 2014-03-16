/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.WildcardPattern

/**
 * Rule that verifies that the name of each method matches a regular expression. By default it checks that the
 * method name starts with a lowercase letter. Implicit method names are ignored (i.e., 'main' and 'run'
 * methods automatically created for Groovy scripts).
 * <p/>
 * The <code>regex</code> property specifies the regular expression to check the method name against. It is
 * required and cannot be null or empty. It defaults to '[a-z]\w*'.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more
 * (comma-separated) method names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class MethodNameRule extends AbstractAstVisitorRule {
    String name = 'MethodName'
    int priority = 2
    Class astVisitorClass = MethodNameAstVisitor
    String regex = /[a-z]\w*/
    String ignoreMethodNames
}

class MethodNameAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode methodNode) {
        assert rule.regex
        if (!new WildcardPattern(rule.ignoreMethodNames, false).matches(methodNode.name)) {
            if (!(methodNode.name ==~ rule.regex)) {
                addViolation(methodNode, "The method name $methodNode.name in class $currentClassName does not match $rule.regex")
            }
        }
    }

}
