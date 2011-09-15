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
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * CodeNarc Rule. Checks for using a comparison operator or equals() or compareTo() to compare two
 * constants to each other or two literal that contain only constant values, e.g.:
 * 23 == 67, [3,2] != [1,2]
 * Boolean.FALSE != false
 * 23 < 88, 0.17 <= 0.99, "abc" > "ddd", [a] >= [x],
 * [a:1] <=> [a:99.87]
 * [1,2].equals([17,true])
 * [a:1].compareTo([a:3])
 *
 * @author Chris Mair
 */
class ComparisonOfTwoConstantsRule extends AbstractAstVisitorRule {
    String name = 'ComparisonOfTwoConstants'
    int priority = 2
    Class astVisitorClass = ComparisonOfTwoConstantsAstVisitor
}

class ComparisonOfTwoConstantsAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression) && AstUtil.isBinaryExpressionType(expression, AstUtil.COMPARISON_OPERATORS)) {
            def left = expression.leftExpression
            def right = expression.rightExpression
            addViolationIfBothAreConstantsOrLiterals(expression, left, right)
        }
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call) && (AstUtil.isMethodCall(call, 'equals', 1) || AstUtil.isMethodCall(call, 'compareTo', 1))) {
            def left = call.objectExpression
            def right = call.arguments.getExpression(0)
            addViolationIfBothAreConstantsOrLiterals(call, left, right)
        }
        super.visitMethodCallExpression(call)
    }

    private void addViolationIfBothAreConstantsOrLiterals(node, left, right) {
        if (AstUtil.isConstantOrConstantLiteral(left) && AstUtil.isConstantOrConstantLiteral(right)) {
            addViolation(node, "Comparing two constants or constant literals is useless and may indicate a bug: ${node.text}")
        }
    }
}
