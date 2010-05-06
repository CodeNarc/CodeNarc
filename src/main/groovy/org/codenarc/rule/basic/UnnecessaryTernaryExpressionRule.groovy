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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.VariableExpression

/**
 * Rule that checks for ternary expressions where the true and false expressions are merely returning
 * <code>true</code> and <code>false</code> constants, and thus can be replaced by a simple boolean
 * expression. Examples include:
 * <ul>
 *   <li><code>boolean result = x ? true : false</code> - can be replaced by <code>boolean result = x</code></li>
 *   <li><code>boolean result = x ? false : true</code> - can be replaced by <code>boolean result = !x</code></li>
 *   <li><code>boolean result = x ? Boolean.TRUE : Boolean.FALSE</code> - can be replaced by <code>boolean result = x</code></li>
 *   <li><code>boolean result = x ? Boolean.FALSE : Boolean.TRUE</code> - can be replaced by <code>boolean result = !x</code></li>
 * </ul>
 *
 * The rule also checks for ternary expressions where the true and false expressions are the same constant or
 * variable expression. Examples include:
 * <ul>
 *   <li><code>def result = x ? '123' : '123'</code> - can be replaced by <code>def result = '123'</code></li>
 *   <li><code>def result = x ? null : null</code> - can be replaced by <code>def result = null</code></li>
 *   <li><code>def result = x ? MAX_VALUE : MAX_VALUE</code> - can be replaced by <code>def result = MAX_VALUE</code></li>
 * </ul>
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnnecessaryTernaryExpressionRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryTernaryExpression'
    int priority = 3
    Class astVisitorClass = UnnecessaryTernaryExpressionAstVisitor
}

class UnnecessaryTernaryExpressionAstVisitor extends AbstractAstVisitor  {

    void visitTernaryExpression(TernaryExpression ternaryExpression) {
        if (isFirstVisit(ternaryExpression)) {
            def trueExpression = ternaryExpression.trueExpression
            def falseExpression = ternaryExpression.falseExpression
            if (areTrueAndFalse(trueExpression, falseExpression) || areBothTheSame(trueExpression, falseExpression)) {
                addViolation(ternaryExpression)
            }
        }
        super.visitTernaryExpression(ternaryExpression)
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

        return false
    }

    private boolean areTrueAndFalse(Expression trueExpression, Expression falseExpression) {
        return (isTrue(trueExpression) && isFalse(falseExpression)) ||
               (isFalse(trueExpression) && isTrue(falseExpression))
    }

    private boolean isTrue(Expression expression) {
        return expression.text == 'true' || expression.text == 'Boolean.TRUE'
    }

    private boolean isFalse(Expression expression) {
        return expression.text == 'false' || expression.text == 'Boolean.FALSE'
    }
}