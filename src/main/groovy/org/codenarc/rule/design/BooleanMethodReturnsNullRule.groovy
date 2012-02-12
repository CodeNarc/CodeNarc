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
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*
import org.codenarc.rule.NullReturnTracker

/**
 * Method with Boolean return type returns explicit null. A method that returns either Boolean.TRUE, Boolean.FALSE or
 * null is an accident waiting to happen. This method can be invoked as though it returned a value of type boolean,
 * and the compiler will insert automatic unboxing of the Boolean value. If a null value is returned, this will
 * result in a NullPointerException.
 *
 * @author Hamlet D'Arcy
 */
class BooleanMethodReturnsNullRule extends AbstractAstVisitorRule {
    String name = 'BooleanMethodReturnsNull'
    int priority = 2
    Class astVisitorClass = BooleanMethodReturnsNullAstVisitor
}

class BooleanMethodReturnsNullAstVisitor extends AbstractAstVisitor {
    private static final String ERROR_MSG = 'Returning null from a method that might return a Boolean'

    void visitMethodEx(MethodNode node) {
        if (methodReturnsBoolean(node)) {
            // does this method ever return null?
            node.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        super.visitMethodEx(node)
    }

    void handleClosure(ClosureExpression expression) {
        if (closureReturnsBoolean(expression)) {
            // does this closure ever return null?
            expression.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        super.visitClosureExpression(expression)
    }

    private static boolean methodReturnsBoolean(MethodNode node) {
        if (AstUtil.classNodeImplementsType(node.returnType, Boolean) || AstUtil.classNodeImplementsType(node.returnType, Boolean.TYPE)) {
            return true
        }

        boolean returnsBoolean = false
        node.code?.visit(new BooleanReturnTracker(callbackFunction: {returnsBoolean = true}))
        returnsBoolean
    }

    private static boolean closureReturnsBoolean(ClosureExpression node) {
        boolean returnsBoolean = false
        node.code?.visit(new BooleanReturnTracker(callbackFunction: {returnsBoolean = true}))
        returnsBoolean
    }
}

class BooleanReturnTracker extends AbstractAstVisitor {
    def callbackFunction

    void visitReturnStatement(ReturnStatement statement) {
        callbackOnBoolean(statement.expression)
        super.visitReturnStatement(statement)
    }

    private callbackOnBoolean(Expression expression) {

        def stack = [expression] as Stack
        while (stack) {
            def expr = stack.pop()
            if (AstUtil.isBoolean(expr)) {
                callbackFunction()
            } else if (expr instanceof BooleanExpression) {
                callbackFunction()
            } else if (expr instanceof CastExpression && AstUtil.classNodeImplementsType(expr.type, Boolean)) {
                callbackFunction()
            } else if (expr instanceof TernaryExpression) {
                stack.push(expr.trueExpression)
                stack.push(expr.falseExpression)
            }
        }
    }
}
