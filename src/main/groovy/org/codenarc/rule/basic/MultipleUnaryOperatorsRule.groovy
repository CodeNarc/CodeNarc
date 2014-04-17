/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for multiple consecutive unary operators. These are confusing, and are likely typos and bugs.
 *
 * @author Chris Mair
 */
class MultipleUnaryOperatorsRule extends AbstractAstVisitorRule {

    String name = 'MultipleUnaryOperators'
    int priority = 2
    Class astVisitorClass = MultipleUnaryOperatorsAstVisitor
}

class MultipleUnaryOperatorsAstVisitor extends AbstractAstVisitor {

    private static final UNARY_OPERATORS = [
        (BitwiseNegationExpression):'~',
        (NotExpression):'!',
        (UnaryMinusExpression):'-',
        (UnaryPlusExpression):'+' ]
    private static final UNARY_OPERATOR_CLASSES = UNARY_OPERATORS.keySet()
    private static final ERROR_MESSAGE = 'The expression (%s) in class %s contains confusing multiple consecutive unary operators'

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        checkForSecondUnaryOperator(expression, BitwiseNegationExpression)
        super.visitBitwiseNegationExpression(expression)
    }

    @Override
    void visitNotExpression(NotExpression expression) {
        checkForSecondUnaryOperator(expression, NotExpression)
        super.visitNotExpression(expression)
    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        checkForSecondUnaryOperator(expression, UnaryMinusExpression)
        super.visitUnaryMinusExpression(expression)
    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        checkForSecondUnaryOperator(expression, UnaryPlusExpression)
        super.visitUnaryPlusExpression(expression)
    }

    private void checkForSecondUnaryOperator(Expression expression, Class<Expression> firstOperatorClass) {
        if (expression.expression.class in UNARY_OPERATOR_CLASSES) {
            String operators = UNARY_OPERATORS[firstOperatorClass] + UNARY_OPERATORS[expression.expression.class]
            String expressionText = operators + expression.text
            addViolation(expression, String.format(ERROR_MESSAGE, expressionText, currentClassName))
        }
    }

}
