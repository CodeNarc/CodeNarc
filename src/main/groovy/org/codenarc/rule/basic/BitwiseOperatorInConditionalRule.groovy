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
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for bitwise operations in conditionals, if you need to do a bitwise operation then it is best practive to extract a temp variable.
 *
 * @author Jeff Beck
 */
class BitwiseOperatorInConditionalRule extends AbstractAstVisitorRule {
    String name = 'BitwiseOperatorInConditional'
    int priority = 2
    Class astVisitorClass = BitwiseOperatorInConditionalAstVisitor
}

class BitwiseOperatorInConditionalAstVisitor extends AbstractAstVisitor {

    private addViolationBitwiseConditional(Expression expression) {

        if (!isFirstVisit(expression)) {
            return
        }

        def parms = [expression] as Stack

        while (parms) {
            def node = parms.pop()
            if (node instanceof BinaryExpression) {

                if (AstUtil.isBinaryExpressionType(node, '|')) {
                    addViolation(node, 'Use of a bitwise or (|) operator in a conditional')
                } else if (AstUtil.isBinaryExpressionType(node, '&')) {
                    addViolation(node, 'Use of a bitwise and (&) operator in a conditional')
                } else {
                    parms.addAll(collectChildren(node))
                }
            }
        }
    }

    private collectChildren(BinaryExpression node) {
        def results = []
        if (node.leftExpression instanceof BinaryExpression) {
            results.add(node.leftExpression)
        }
        if (node.rightExpression instanceof BinaryExpression) {
            results.add(node.rightExpression)
        }
        results
    }

    @Override
    void visitIfElse(IfStatement node) {
        addViolationBitwiseConditional(node.booleanExpression.expression)
        super.visitIfElse(node)
    }

    @Override
    void visitWhileLoop(WhileStatement node) {
        addViolationBitwiseConditional(node.booleanExpression.expression)
        super.visitWhileLoop(node)
    }

    @Override
    void visitTernaryExpression(TernaryExpression node) {
        addViolationBitwiseConditional(node.booleanExpression.expression)
        super.visitTernaryExpression(node)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression node) {
        addViolationBitwiseConditional(node.booleanExpression.expression)
        super.visitShortTernaryExpression(node)
    }
}
