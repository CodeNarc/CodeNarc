/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Check that there is at least one space (blank) or whitespace following each comma. That includes
 * checks for method and closure declaration parameter lists, method calls, Map literals and List literals.
 *
 * @author Chris Mair
  */
class SpaceAfterCommaRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterComma'
    int priority = 2
    Class astVisitorClass = SpaceBetweenParametersAstVisitor
}

class SpaceBetweenParametersAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitMethodEx(MethodNode node) {
        def lastColumn
        node.parameters.each { parameter ->
            if (lastColumn && parameter.columnNumber == lastColumn + 1) {
                addViolation(node, "The parameter ${parameter.name} of method ${node.name} within class $currentClassName is not preceded by a space or whitespace")

            }
            lastColumn = parameter.lastColumnNumber
        }
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        if (isFirstVisit(expression)) {
            def lastColumn
            expression.parameters.each { parameter ->
                if (lastColumn && parameter.columnNumber == lastColumn + 1) {
                    addViolation(expression, "The closure parameter ${parameter.name} within class $currentClassName is not preceded by a space or whitespace")

                }
                lastColumn = parameter.lastColumnNumber
            }
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call)) {
            def arguments = call.arguments
            def parameterExpressions = arguments.expressions
            def lastColumn

            parameterExpressions.each { e ->
                if (lastColumn && e.columnNumber == lastColumn + 1) {
                    addViolation(call, "The parameter ${e.text} in the call to method ${call.methodAsString} within class $currentClassName is not preceded by a space or whitespace")

                }
                lastColumn = e.lastColumnNumber
            }
        }
    }

}