/*
 * Copyright 2021 the original author or authors.
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
import org.codenarc.rule.NullReturnTracker
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * This rule detects when null is returned from a method or Closure that might return a
 * collection. Instead of null, return an empty collection.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class ReturnsNullInsteadOfEmptyCollectionRule extends AbstractAstVisitorRule {

    String name = 'ReturnsNullInsteadOfEmptyCollection'
    int priority = 2
    Class astVisitorClass = ReturnsNullInsteadOfEmptyCollectionRuleAstVisitor
}

class ReturnsNullInsteadOfEmptyCollectionRuleAstVisitor extends AbstractAstVisitor {

    private static final String ERROR_MSG = 'Returning null from a method that might return a Collection or Map'

    @Override
    void visitMethodEx(MethodNode node) {
        if (methodReturnsCollection(node)) {
            // does this method ever return null?
            node.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        super.visitMethodEx(node)
    }

    void handleClosure(ClosureExpression expression) {
        if (closureReturnsCollection(expression)) {
            // does this closure ever return null?
            expression.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
        // Do not keep walking into nested Closures
        //super.visitClosureExpression(expression)
    }

    private static boolean methodReturnsCollection(MethodNode node) {
        if (AstUtil.classNodeImplementsType(node.returnType, Iterable)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, Map)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, List)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, Collection)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, ArrayList)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, Set)) {
            return true
        }
        if (AstUtil.classNodeImplementsType(node.returnType, HashSet)) {
            return true
        }

        boolean returnsCollection = false
        node.code?.visit(new CollectionReturnTracker(callbackFunction: { returnsCollection = true }))
        returnsCollection
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        if (closureReturnsCollection(expression)) {
            // does this closure ever return null?
            expression.code?.visit(new NullReturnTracker(parent: this, errorMessage: ERROR_MSG))
        }
    }

    private static boolean closureReturnsCollection(ClosureExpression node) {
        boolean returnsArray = false
        node.code?.visit(new CollectionReturnTracker(callbackFunction: { returnsArray = true }))
        returnsArray
    }
}

class CollectionReturnTracker extends AbstractAstVisitor {

    Closure callbackFunction

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        // skip nested closures
    }

    @Override
    void visitReturnStatement(ReturnStatement statement) {
        expressionReturnsList(statement.expression)

        // Do not keep walking into nested Closures
        //super.visitReturnStatement(statement)
    }

    private void expressionReturnsList(Expression expression) {
        def stack = [expression] as Stack  // as alternative to recursion
        while (stack) {
            def expr = stack.pop()
            if (expr instanceof ListExpression || expr instanceof MapExpression) {
                callbackFunction()
            }
            if (expr instanceof ConstructorCallExpression || expr instanceof CastExpression) {
                [Map, Iterable, List, Collection, ArrayList, Set, HashSet].findAll {
                    AstUtil.classNodeImplementsType(expr.type, it)
                }.each {
                    callbackFunction()
                }
            }
            if (expr instanceof TernaryExpression) {
                stack.push(expr.trueExpression)
                stack.push(expr.falseExpression)
            }
        }
    }
}
