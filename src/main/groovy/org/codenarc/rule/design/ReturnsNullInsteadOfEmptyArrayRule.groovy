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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.NullReturnTracker

/**
 * This rule detects when null is returned from a method that might return an
 * array. Instead of null, return a zero length array. 
 *
 * @author Hamlet D'Arcy
 */
class ReturnsNullInsteadOfEmptyArrayRule extends AbstractAstVisitorRule {
    String name = 'ReturnsNullInsteadOfEmptyArray'
    int priority = 2
    Class astVisitorClass = ReturnsNullInsteadOfEmptyArrayAstVisitor
}

class ReturnsNullInsteadOfEmptyArrayAstVisitor extends AbstractAstVisitor {

    private static final String ERROR_MSG = 'Returning null from a method that might return an Array'

    void visitMethodEx(MethodNode node) {
        if (methodReturnsArray(node)) {
            // does this method ever return null?
            node.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        super.visitMethodEx(node)
    }

    void handleClosure(ClosureExpression expression) {
        if (closureReturnsArray(expression)) {
            // does this closure ever return null?
            expression.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        super.visitClosureExpression(expression)
    }

    private static boolean methodReturnsArray(MethodNode node) {
        if (node.returnType.isArray()) {
            return true
        }

        boolean returnsArray = false
        node.code?.visit(new ArrayReturnTracker(callbackFunction: { returnsArray = true }))
        returnsArray
    }

    private static boolean closureReturnsArray(ClosureExpression node) {
        boolean returnsArray = false
        node.code?.visit(new ArrayReturnTracker(callbackFunction: { returnsArray = true }))
        returnsArray
    }
}

class ArrayReturnTracker extends AbstractAstVisitor {
    def callbackFunction

    void visitReturnStatement(ReturnStatement statement) {

        callBackForArrayReturns(statement.expression)
        super.visitReturnStatement(statement)
    }

    private callBackForArrayReturns(Expression expression) {
        def stack = [expression] as Stack
        while (stack) {
            def expr = stack.pop()
            if (expr instanceof CastExpression) {
                if (expr.type.isArray()) {
                    callbackFunction()
                }
            } else if (expr instanceof TernaryExpression) {
                stack.push(expr.trueExpression)
                stack.push(expr.falseExpression)
            }
        }
    }
}

