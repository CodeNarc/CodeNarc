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

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The Math.random() method returns a double result greater than or equal to 0.0 and less than 1.0. If you coerce this
 * result into an Integer or int, then it is coerced to zero. Casting the result to int, or assigning it to an int
 * field is probably a bug.
 *
 * @author Hamlet D'Arcy
 */
class RandomDoubleCoercedToZeroRule extends AbstractAstVisitorRule {
    String name = 'RandomDoubleCoercedToZero'
    int priority = 2
    Class astVisitorClass = RandomDoubleCoercedToZeroAstVisitor
}

class RandomDoubleCoercedToZeroAstVisitor extends AbstractAstVisitor {
    @Override
    void visitCastExpression(CastExpression expression) {
        if (isFirstVisit(expression)) {
            if (AstUtil.isMethodCall(expression.expression, 'Math', 'random', 0)) {
                if (expression.type?.name == 'long' || expression.type?.name == 'Long') {
                    addViolation(expression, "Casting the result of Math.random() to a ${expression.type.name} always results in 0")
                } else if (expression.type?.name == 'int' || expression.type?.name == 'Integer') {
                    addViolation(expression, "Casting the result of Math.random() to an ${expression.type.name} always results in 0")
                }
            }
        }
        super.visitCastExpression(expression)
    }

    @Override
    void visitField(FieldNode node) {

        if (isFirstVisit(node)) {
            if (node.initialExpression && AstUtil.isMethodCall(node.initialExpression, 'Math', 'random', 0)) {
                if (node.type?.name == 'long' || node.type?.name == 'Long') {
                    addViolation(node, "Assigning the result of Math.random() to a ${node.type.name} always results in 0")
                } else if (node.type?.name == 'int' || node.type?.name == 'Integer') {
                    addViolation(node, "Assigning the result of Math.random() to an ${node.type.name} always results in 0")
                }
            }
        }
        super.visitField(node)
    }

    @Override
    void visitMethodEx(MethodNode node) {
        if (isFirstVisit(node)) {
            if (node.returnType.name in ['int', 'Integer']) {
                node.code?.visit(new MathRandomTracker(callbackFunction: {
                    addViolation(it, "Returning the result of Math.random() from an ${node.returnType.name}-returning method always returns 0")
                }))
            }
            if (node.returnType.name in ['long', 'Long']) {
                node.code?.visit(new MathRandomTracker(callbackFunction: {
                    addViolation(it, "Returning the result of Math.random() from a ${node.returnType.name}-returning method always returns 0")
                }))
            }
        }
    }
}

class MathRandomTracker extends AbstractAstVisitor {
    def callbackFunction

    void visitReturnStatement(ReturnStatement statement) {
        callBackForMathRandomReturns(statement.expression)
        super.visitReturnStatement(statement)
    }

    private callBackForMathRandomReturns(Expression exp) {
        def stack = [exp] as Stack
        while (stack) {
            def expression = stack.pop()
            if (AstUtil.isMethodCall(expression, 'Math', 'random', 0)) {
                callbackFunction(expression)
            } else if (expression instanceof TernaryExpression) {
                stack.push(expression.trueExpression)
                stack.push(expression.falseExpression)
            }
        }
    }
}
