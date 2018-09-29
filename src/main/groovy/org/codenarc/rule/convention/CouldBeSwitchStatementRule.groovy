/*
 * Copyright 2017 the original author or authors.
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
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for multiple if statements that could be converted to a switch
 *
 * @author Jenn Strater
 */
class CouldBeSwitchStatementRule extends AbstractAstVisitorRule {

    String name = 'CouldBeSwitchStatement'
    int priority = 3
    Class astVisitorClass = CouldBeSwitchStatementAstVisitor
    String errorMessage = 'Code could use switch statement'
}

class CouldBeSwitchStatementAstVisitor extends AbstractAstVisitor {

    private BinaryExpression prev = null
    private Integer ifCounter = 0
    private Expression firstIfNode = null

    @Override
    void visitIfElse(IfStatement node) {
        checkIfStatementCanBeSwitch(node)
        super.visitIfElse(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        ifCounter = 0
    }

    private void checkIfStatementCanBeSwitch(IfStatement node) {
        def current = node.booleanExpression?.expression

        if (current instanceof BinaryExpression && isSupportedLeftExpressionType(current.leftExpression) &&
                inSupportedOperation(current.operation)) {
            if (isSameLeftExpressionAsPreviousIfStatement(current.leftExpression, prev?.leftExpression)) {
                ifCounter++
            } else {
                ifCounter = 1
                firstIfNode = current
            }

            if (ifCounter > 2) {
                addViolation(firstIfNode, rule.errorMessage)
                ifCounter = 0
                prev = null
            }

            prev = current
        } else {
            ifCounter = 0
            prev = null
        }
    }

    private Boolean inSupportedOperation(Token operation) {
        operation.type in [Types.COMPARE_EQUAL, Types.KEYWORD_INSTANCEOF]
    }

    private Boolean isSupportedLeftExpressionType(Expression expression) {
        switch(expression) {
            case PropertyExpression:
            case VariableExpression:
            case ConstantExpression:
                return true
            default:
                return false
        }
    }

    private Boolean isSameLeftExpressionAsPreviousIfStatement(Expression current, Expression prev) {
        if (!prev || current.class != prev.class) {
            return false
        }
        switch(current) {
            case PropertyExpression:
                return isSameLeftExpressionAsPreviousIfStatement(current.objectExpression, prev?.objectExpression) &&
                        isSameLeftExpressionAsPreviousIfStatement(current.property, prev.property)
            case VariableExpression:
                return current.variable == prev?.variable
            case ConstantExpression:
                return current.value == prev.value
            default:
                false
        }
    }
}
