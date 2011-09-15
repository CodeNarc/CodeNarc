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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * CodeNarc Rule. Checks for using a comparison operator or equals() or compareTo() to compare a
 * variable to itself, e.g.:
 * x == x, x != x, x <=> x,
 * x < x, x > x, x <= x, x >= x,
 * x.equals(x), x.compareTo(x)
 * where x is a variable
 *
 * @author Chris Mair
 */
class ComparisonWithSelfRule extends AbstractAstVisitorRule {
    String name = 'ComparisonWithSelf'
    int priority = 2
    Class astVisitorClass = ComparisonWithSelfAstVisitor
}

class ComparisonWithSelfAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression) && AstUtil.isBinaryExpressionType(expression, AstUtil.COMPARISON_OPERATORS)) {
            def left = expression.leftExpression
            def right = expression.rightExpression
            addViolationIfBothAreTheSameVariable(expression, left, right)
        }
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call) && (AstUtil.isMethodCall(call, 'equals', 1) || AstUtil.isMethodCall(call, 'compareTo', 1))) {
            def left = call.objectExpression
            def right = call.arguments.getExpression(0)
            addViolationIfBothAreTheSameVariable(call, left, right)
        }
        super.visitMethodCallExpression(call)
    }

    private void addViolationIfBothAreTheSameVariable(node, left, right) {
        if (left instanceof VariableExpression && right instanceof VariableExpression && left.name == right.name) {
            addViolation(node, "Comparing an object to itself is useless and may indicate a bug: ${node.text}")
        }
    }
}
