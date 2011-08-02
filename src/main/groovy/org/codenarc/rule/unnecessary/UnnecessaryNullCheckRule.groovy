/*
 * Copyright 2011 the original author or authors.
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
 * Groovy contains the safe dereference operator, which can be used in boolean conditional statements to safely
 * replace explicit "x == null" tests.
 *
 * @author Hamlet D'Arcy
 */
class UnnecessaryNullCheckRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryNullCheck'
    int priority = 3
    Class astVisitorClass = UnnecessaryNullCheckAstVisitor
}

class UnnecessaryNullCheckAstVisitor extends AbstractAstVisitor {

    @SuppressWarnings('NestedBlockDepth')
    @Override
    void visitBooleanExpression(BooleanExpression expression) {
        if (isFirstVisit(expression)) {
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
            } else if (isNotNullCheckAgainstThisReference(exp)) {
                addViolation(expression, 'Testing the this reference for not null will always return true')
            } else if (isNullCheckAgainstThisReference(exp)) {
                addViolation(expression, 'Testing the this reference for null will always return false')
            } else if (isNotNullCheckAgainstSuperReference(exp)) {
                addViolation(expression, 'Testing the super reference for not null will always return true')
            } else if (isNullCheckAgainstSuperReference(exp)) {
                addViolation(expression, 'Testing the super reference for null will always return false')
            }
            super.visitBooleanExpression(expression)
        }
    }

    private static boolean isSuperReference(Expression expression) {
        expression instanceof VariableExpression && expression.variable == 'super'
    }

    private static boolean isNotNullCheckAgainstThisReference(Expression exp) {
        if (exp instanceof BinaryExpression && AstUtil.isNotNullCheck(exp)) {
            if (AstUtil.isThisReference(exp.leftExpression) || AstUtil.isThisReference(exp.rightExpression)) {
                return true
            } 
        }
        false
    }

    private static boolean isNotNullCheckAgainstSuperReference(Expression exp) {
        if (exp instanceof BinaryExpression && AstUtil.isNotNullCheck(exp)) {
            if (isSuperReference(exp.leftExpression) || isSuperReference(exp.rightExpression)) {
                return true
            }
        }
        false
    }

    private static boolean isNullCheckAgainstThisReference(Expression exp) {
        if (exp instanceof BinaryExpression && AstUtil.isNullCheck(exp)) {
            if (AstUtil.isThisReference(exp.leftExpression) || AstUtil.isThisReference(exp.rightExpression)) {
                return true
            }
        }
        false
    }

    private static boolean isNullCheckAgainstSuperReference(Expression exp) {
        if (exp instanceof BinaryExpression && AstUtil.isNullCheck(exp)) {
            if (isSuperReference(exp.leftExpression) || isSuperReference(exp.rightExpression)) {
                return true
            }
        }
        false
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
