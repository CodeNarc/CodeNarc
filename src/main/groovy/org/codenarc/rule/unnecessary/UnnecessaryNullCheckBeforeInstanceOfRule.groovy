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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * There is no need to check for null before an instanceof; the instanceof keyword returns false when given a null argument.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UnnecessaryNullCheckBeforeInstanceOfRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryNullCheckBeforeInstanceOf'
    int priority = 2
    Class astVisitorClass = UnnecessaryNullCheckBeforeInstanceOfAstVisitor
}

class UnnecessaryNullCheckBeforeInstanceOfAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBooleanExpression(BooleanExpression expression) {
        def exp = expression.expression
        if (exp instanceof BinaryExpression && exp.operation.text == '&&') {
            if (isNotNullCheck(exp.leftExpression) || isNotNullCheck(exp.rightExpression)) {
                if (isInstanceOfCheck(exp.leftExpression) || isInstanceOfCheck(exp.rightExpression)) {
                    addViolationIfTargetsMatch(expression)
                }
            }
        }
        super.visitBooleanExpression(expression)
    }

    private addViolationIfTargetsMatch(BooleanExpression expression) {
        BinaryExpression exp = expression.expression
        def nullTarget = getNullComparisonTarget(exp.leftExpression) ?: getNullComparisonTarget(exp.rightExpression)
        def instanceofTarget = getInstanceOfTarget(exp.leftExpression) ?: getInstanceOfTarget(exp.rightExpression)
        if (nullTarget && instanceofTarget && nullTarget == instanceofTarget) {
            def suggestion = isInstanceOfCheck(exp.leftExpression) ? exp.leftExpression.text : exp.rightExpression.text
            addViolation(expression, "The condition $exp.text can be safely simplified to $suggestion")
        }
    }

    private static boolean isNotNullCheck(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == '!=') {
            if (AstUtil.isNull(expression.leftExpression) || AstUtil.isNull(expression.rightExpression)) {
                return true
            }
        }
        false
    }

    private static String getNullComparisonTarget(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == '!=') {
            if (AstUtil.isNull(expression.leftExpression)) {
                return expression.rightExpression.text
            } else if (AstUtil.isNull(expression.rightExpression)) {
                return expression.leftExpression.text
            }
        }
        null
    }

    private static boolean isInstanceOfCheck(expression) {
        (expression instanceof BinaryExpression && expression.operation.text == 'instanceof')
    }

    private static String getInstanceOfTarget(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == 'instanceof') {
            return expression.leftExpression.text
        }
        null
    }
}
