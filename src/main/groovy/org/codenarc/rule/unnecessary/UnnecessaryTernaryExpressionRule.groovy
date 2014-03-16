/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * Rule that checks for ternary expressions where the conditional expression always evaluates to
 * a boolean and the true and false expressions are merely returning <code>true</code> and
 * <code>false</code> constants. These cases can be replaced by a simple boolean expression.
 * Examples include:
 * <ul>
 *   <li><code>boolean result = x==99 ? true : false</code> - can be replaced by <code>boolean result = x==99</code></li>
 *   <li><code>boolean result = x && y ? true : false</code> - can be replaced by <code>boolean result = x && y</code></li>
 *   <li><code>def result = x||y ? false : true</code> - can be replaced by <code>def result = !(x||y)</code></li>
 *   <li><code>boolean result = x >= 1 ? true: false</code> - can be replaced by <code>boolean result = x >= 1</code></li>
 *   <li><code>boolean result = x < 99 ? Boolean.TRUE : Boolean.FALSE</code> - can be replaced by <code>boolean result = x < 99</code></li>
 *   <li><code>def result = !x ? true : false</code> - can be replaced by <code>def result = !x</code></li>
 * </ul>
 *
 * The rule also checks for ternary expressions where the true and false expressions are the same constant or
 * variable expression. Examples include:
 * <ul>
 *   <li><code>def result = x ? '123' : '123'</code> - can be replaced by <code>def result = '123'</code></li>
 *   <li><code>def result = x ? null : null</code> - can be replaced by <code>def result = null</code></li>
 *   <li><code>def result = x ? 23 : 23</code> - can be replaced by <code>def result = 23</code></li>
 *   <li><code>def result = x ? MAX_VALUE : MAX_VALUE</code> - can be replaced by <code>def result = MAX_VALUE</code></li>
 * </ul>
 *
 * @author Chris Mair
  */
class UnnecessaryTernaryExpressionRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryTernaryExpression'
    int priority = 3
    Class astVisitorClass = UnnecessaryTernaryExpressionAstVisitor
}

class UnnecessaryTernaryExpressionAstVisitor extends AbstractAstVisitor  {

    private static final BOOLEAN_COMPARISON_OPERATIONS = ['<', '>', '>=', '<=', '==', '!=', '==~']
    private static final BOOLEAN_LOGIC_OPERATIONS = ['&&', '||']
    
    void visitTernaryExpression(TernaryExpression ternaryExpression) {
        if (isFirstVisit(ternaryExpression)) {
            def trueExpression = ternaryExpression.trueExpression
            def falseExpression = ternaryExpression.falseExpression
            def booleanExpression = ternaryExpression.booleanExpression
            if (areBothTheSame(trueExpression, falseExpression)
                || (isBooleanConditionalExpression(booleanExpression) && areTrueAndFalse(trueExpression, falseExpression))) {

                addViolation(ternaryExpression, 'The ternary expression is useless or nonsensical')
            }
        }
        super.visitTernaryExpression(ternaryExpression)
    }

    private boolean isBooleanConditionalExpression(BooleanExpression conditionalExpression) {
        def expression = conditionalExpression.expression
        if (expression instanceof NotExpression) {
            return true
        }
        if (expression instanceof BinaryExpression && isOperationThatReturnsABoolean(expression)) {
            return true
        }
        false
    }

    private boolean isOperationThatReturnsABoolean(expression) {
        def operationName = expression.operation.text
        if (operationName in BOOLEAN_COMPARISON_OPERATIONS) {
            return true
        }
        if (operationName in BOOLEAN_LOGIC_OPERATIONS) {
            return true
        }
        false
    }

    private boolean areBothTheSame(Expression trueExpression, Expression falseExpression) {
        if (trueExpression instanceof ConstantExpression && falseExpression instanceof ConstantExpression
            && trueExpression.getValue() == falseExpression.getValue()) {
            return true
        }

        if (trueExpression instanceof VariableExpression && falseExpression instanceof VariableExpression
            && trueExpression.getName() == falseExpression.getName()) {
            return true
        }

        false
    }

    private boolean areTrueAndFalse(Expression trueExpression, Expression falseExpression) {
        (AstUtil.isTrue(trueExpression) && AstUtil.isFalse(falseExpression)) ||
               (AstUtil.isFalse(trueExpression) && AstUtil.isTrue(falseExpression))
    }

}
