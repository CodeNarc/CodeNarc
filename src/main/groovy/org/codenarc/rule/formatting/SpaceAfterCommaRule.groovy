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
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.Expression

/**
 * Check that there is at least one space (blank) or whitespace following each comma. That includes checks
 * for method and closure declaration parameter lists, method call parameter lists, Map literals and List literals.
 *
 * @author Chris Mair
  */
class SpaceAfterCommaRule extends AbstractAstVisitorRule {

    // Initialize displayed text for AST ClosureExpression
    {
        ClosureExpression.metaClass.getText = { return CLOSURE_TEXT }
    }

    String name = 'SpaceAfterComma'
    int priority = 3
    Class astVisitorClass = SpaceAfterCommaAstVisitor
}

class SpaceAfterCommaAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        def lastColumn
        node.parameters.each { parameter ->
            if (lastColumn && parameter.columnNumber == lastColumn + 1) {
                addViolation(node, "The parameter ${parameter.name} of method ${node.name} within class $currentClassName is not preceded by a space or whitespace")
            }
            lastColumn = parameter.lastColumnNumber
        }
        super.visitConstructorOrMethod(node, isConstructor)
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
        processMethodOrConstructorCall(call)
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        processMethodOrConstructorCall(call)
        super.visitConstructorCallExpression(call)
    }

    private void processMethodOrConstructorCall(Expression call) {
        if (isFirstVisit(call)) {
            def arguments = call.arguments
            def parameterExpressions = arguments.expressions
            def lastColumn

            parameterExpressions.each { e ->
                if (lastColumn && e.columnNumber == lastColumn + 1 && !isClosureParameterOutsideParentheses(e, arguments)) {
                    addViolation(call, "The parameter ${e.text} in the call to method ${call.methodAsString} within class $currentClassName is not preceded by a space or whitespace")
                }
                lastColumn = e.lastColumnNumber
            }
        }
    }

    private boolean isClosureParameterOutsideParentheses(Expression e, arguments) {
        e instanceof ClosureExpression && e.columnNumber > arguments.lastColumnNumber
    }

    @Override
    void visitListExpression(ListExpression listExpression) {
        if (isFirstVisit(listExpression)) {
            def lastColumn
            listExpression.expressions.each { e ->
                if (lastColumn && e.columnNumber == lastColumn + 1) {
                    addViolation(listExpression, "The list element ${e.text} within class $currentClassName is not preceded by a space or whitespace")
                }
                lastColumn = e.lastColumnNumber
            }
        }
        super.visitListExpression(listExpression)
    }

    @Override
    void visitMapExpression(MapExpression mapExpression) {
        if (isFirstVisit(mapExpression)) {
            def lastColumn
            mapExpression.mapEntryExpressions.each { e ->
                if (lastColumn && e.keyExpression.columnNumber == lastColumn + 1) {
                    def mapEntryAsString = e.keyExpression.text + ':' + e.valueExpression.text
                    addViolation(mapExpression, "The map entry $mapEntryAsString within class $currentClassName is not preceded by a space or whitespace")
                }
                lastColumn = e.valueExpression.lastColumnNumber
            }
        }
        super.visitMapExpression(mapExpression)
    }
}
