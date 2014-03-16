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

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern

/**
 * Rule that verifies that the name of each parameter matches a regular expression. This rule applies
 * to method parameters, constructor parameters and closure parameters. By default it checks that
 * parameter names start with a lowercase letter and contains only letters or numbers.
 * <p/>
 * The <code>regex</code> property specifies the default regular expression used to validate the
 * parameter name. It is required and cannot be null or empty. It defaults to '[a-z][a-zA-Z0-9]*'.
 * <p/>
 * The <code>ignoreParameterNames</code> property optionally specifies one or more
 * (comma-separated) parameter names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class ParameterNameRule extends AbstractAstVisitorRule {
    String name = 'ParameterName'
    int priority = 2
    String regex = DEFAULT_VAR_NAME
    String ignoreParameterNames
    Class astVisitorClass = ParameterNameAstVisitor

    void validate() {
        assert regex
    }
}

class ParameterNameAstVisitor extends AbstractAstVisitor  {

    void visitMethodEx(MethodNode methodNode) {
        processParameters(methodNode.parameters, methodNode.name)
        super.visitMethodEx(methodNode)
    }

    @Override
    void visitConstructor(ConstructorNode constructorNode) {
        processParameters(constructorNode.parameters, '<init>')
        super.visitConstructor(constructorNode)
    }

    void visitClosureExpression(ClosureExpression closureExpression) {
        if (isFirstVisit(closureExpression)) {
            processParameters(closureExpression.parameters, '<closure>')
        }
        super.visitClosureExpression(closureExpression)
    }

    private void processParameters(parameters, methodName) {
        parameters.each { parameter ->
            if (!new WildcardPattern(rule.ignoreParameterNames, false).matches(parameter.name)) {
                if (parameter.lineNumber >= 0 && !(parameter.name ==~ rule.regex)) {
                    addViolation(parameter, "The parameter named $parameter.name in method $methodName of class $currentClassName does not match ${rule.regex.toString()}")
                }
            }
        }
    }

}
