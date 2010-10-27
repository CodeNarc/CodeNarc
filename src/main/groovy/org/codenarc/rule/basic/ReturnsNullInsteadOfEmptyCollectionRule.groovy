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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.ClosureExpression

import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.util.AstUtil

/**
 * This rule detects when null is returned from a method that might return a
 * collection. Instead of null, return a zero length array.
 * 
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class ReturnsNullInsteadOfEmptyCollectionRule extends AbstractAstVisitorRule {
    String name = 'ReturnsNullInsteadOfEmptyCollectionRule'
    int priority = 2
    Class astVisitorClass = ReturnsNullInsteadOfEmptyCollectionRuleAstVisitor
}

class ReturnsNullInsteadOfEmptyCollectionRuleAstVisitor extends AbstractAstVisitor {

    def void visitMethod(MethodNode node) {
        if (methodReturnsCollection(node)) {
            // does this method ever return null?
            node.code?.visit(new NullReturnTracker(parent: this))
        }
        super.visitMethod(node)
    }

    def void handleClosure(ClosureExpression expression) {
        if (closureReturnsCollection(expression)) {
            // does this closure ever return null?
            expression.code?.visit(new NullReturnTracker(parent: this))
        }
        super.visitClosureExpression(expression)
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
        node.code?.visit(new CollectionReturnTracker(callbackFunction: {returnsCollection = true}))
        return returnsCollection
    }

    private static boolean closureReturnsCollection(ClosureExpression node) {
        boolean returnsArray = false
        node.code?.visit(new CollectionReturnTracker(callbackFunction: {returnsArray = true}))
        return returnsArray
    }
}

private class CollectionReturnTracker extends AbstractAstVisitor {
    def callbackFunction

    def void visitReturnStatement(ReturnStatement statement) {
        def expression = statement.expression
        if (expression instanceof ListExpression || expression instanceof MapExpression) {
            callbackFunction()
        }
        if (expression instanceof ConstructorCallExpression || expression instanceof CastExpression) {
            [Map, Iterable, List, Collection, ArrayList, Set, HashSet].each {
                if (AstUtil.classNodeImplementsType(statement.expression.type, it)) {
                    callbackFunction()
                }
            }
        }
        super.visitReturnStatement(statement)
    }
}