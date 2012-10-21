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
package org.codenarc.rule.basic

import static org.codenarc.util.AstUtil.isNull

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.*

/**
 * Looks for faulty checks for null in boolean conditions, e.g.
 *      if (name != null || name.length > 0) { }
 *      if (name != null || name.size() > 0) { }
 *      if (string == null && string.equals("")) { }
 *
 * @author Chris Mair
 */
class BrokenNullCheckRule extends AbstractAstVisitorRule {
    String name = 'BrokenNullCheck'
    int priority = 2
    Class astVisitorClass = BrokenNullCheckAstVisitor
}

class BrokenNullCheckAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression)) {
            if (expression.leftExpression instanceof BinaryExpression) {
                if (isBrokenNullCheck(expression, '||', '!=') || isBrokenNullCheck(expression, '&&', '==')) {
                    addViolation(expression, "The expression ${expression.text} within class $currentClassName incorrectly checks for null, and can throw a NullPointerException.")
                }
            }
        }
        super.visitBinaryExpression(expression)
    }

    private boolean isBrokenNullCheck(BinaryExpression expression, String andOrOperator, String comparisonWithNullOperator) {
        if (expression.operation.text == andOrOperator) {
            def leftBinary = expression.leftExpression
            def rightBinary = expression.rightExpression
            if (isComparisonWithNull(leftBinary, comparisonWithNullOperator)) {
                def varName = leftBinary.leftExpression.name
                if (isExpressionAccessingMemberNamed(rightBinary, varName)) {
                    return true
                }
            }
        }
        return false
    }

    private boolean isExpressionAccessingMemberNamed(Expression rightBinary, varName) {
        return isPropertyAccessForName(rightBinary, varName) || isMethodCallOnName(rightBinary, varName)
    }

    private boolean isComparisonWithNull(BinaryExpression expression, String operation) {
        return expression.leftExpression instanceof VariableExpression &&
            expression.operation.text == operation &&
            isNull(expression.rightExpression)
    }

    private boolean isPropertyAccessForName(Expression expression, String varName) {
        boolean isDirectPropertyAccess =
           expression instanceof PropertyExpression &&
           expression.objectExpression instanceof VariableExpression &&
           expression.objectExpression.name == varName
        return isDirectPropertyAccess ||
            isBinaryExpressionWithPropertyAccessForName(expression, varName) ||
            isNotExpressionWithPropertyAccessForName(expression, varName)
    }

    private boolean isBinaryExpressionWithPropertyAccessForName(Expression expression, String varName) {
         return expression instanceof BinaryExpression &&
             isPropertyAccessForName(expression.leftExpression, varName)
    }

    private boolean isNotExpressionWithPropertyAccessForName(Expression expression, String varName) {
         return expression instanceof NotExpression &&
             isPropertyAccessForName(expression.expression, varName)
    }

    private boolean isMethodCallOnName(Expression expression, String varName) {
        boolean isDirectMethodCall = expression instanceof MethodCallExpression &&
            expression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.name == varName
        return isDirectMethodCall ||
            isBinaryExpressionWithMethodCallOnName(expression, varName) ||
            isNotExpressionWithMethodCallOnName(expression, varName)
    }

    private boolean isBinaryExpressionWithMethodCallOnName(Expression expression, String varName) {
         return expression instanceof BinaryExpression &&
             isMethodCallOnName(expression.leftExpression, varName)
    }

    private boolean isNotExpressionWithMethodCallOnName(Expression expression, String varName) {
         return expression instanceof NotExpression &&
             isMethodCallOnName(expression.expression, varName)
    }

}
