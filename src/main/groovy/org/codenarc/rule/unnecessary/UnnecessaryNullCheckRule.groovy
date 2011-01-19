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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * Groovy contains the safe dereference operator, which can be used in boolean conditional statements to safely
 * replace explicit "x == null" tests.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class UnnecessaryNullCheckRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryNullCheck'
    int priority = 2
    Class astVisitorClass = UnnecessaryNullCheckAstVisitor
}

class UnnecessaryNullCheckAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBooleanExpression(BooleanExpression expression) {
        def exp = expression.expression
        if (exp instanceof BinaryExpression && exp.operation.text == '&&') {
            if (AstUtil.isNotNullCheck(exp.leftExpression)) {
                // perform should use null-safe dereference test
                def nullTarget = AstUtil.getNullComparisonTarget(exp.leftExpression)
                if (isPropertyInvocation(exp.rightExpression, nullTarget)) {
                    def suggestion = "$nullTarget?.$exp.rightExpression.property.text"
                    addViolation(expression, "The expression $expression.text can be simplified to ($suggestion)")
                } else if (isMethodInvocation(exp.rightExpression, nullTarget)) {
                    def suggestion = "$nullTarget?.${exp.rightExpression.method.text}${exp.rightExpression.arguments.text}"
                    addViolation(expression, "The expression $expression.text can be simplified to ($suggestion)")
                }
            } else if (AstUtil.isNotNullCheck(exp.rightExpression)) {
                def nullTarget = AstUtil.getNullComparisonTarget(exp.rightExpression)
                // perform pointless null check test
                if (isPropertyInvocation(exp.leftExpression, nullTarget) || isMethodInvocation(exp.leftExpression, nullTarget)) {
                    def suggestion = exp.leftExpression.text
                    addViolation(expression, "The expression $expression.text can be simplified to ($suggestion)")
                }
            }
        }
        super.visitBooleanExpression(expression)
    }

    private static boolean isPropertyInvocation(expression, String targetName) {
        if (expression instanceof PropertyExpression) {
            if (expression.objectExpression instanceof VariableExpression) {
                if (expression.objectExpression.variable == targetName) {
                    return true
                }
            }
        }
        false
    }

    private static boolean isMethodInvocation(expression, String targetName) {
        if (expression instanceof MethodCallExpression) {
            if (expression.objectExpression instanceof VariableExpression) {
                if (expression.objectExpression.variable == targetName) {
                    return true
                }
            }
        }
        false
    }
}
