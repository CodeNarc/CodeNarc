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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for parameters to private methods that are not referenced within the method body.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedPrivateMethodParameterRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateMethodParameter'
    int priority = 2
    Class astVisitorClass = UnusedPrivateMethodParameterAstVisitor
}

//@SuppressWarnings('DuplicateLiteral')
class UnusedPrivateMethodParameterAstVisitor extends AbstractAstVisitor  {

    private unusedParameterNames = []

    void visitMethodEx(MethodNode node) {
        def isPrivate = node.modifiers & FieldNode.ACC_PRIVATE
        if (isPrivate) {
            unusedParameterNames = node.parameters*.name
            super.visitMethodEx(node)
        }
    }

    protected void visitMethodComplete(MethodNode node) {
        unusedParameterNames.each { parameterName ->
            addViolation(node, "Method parameter [$parameterName] is never referenced")
        }
        super.visitMethodComplete(node)
    }

    void visitVariableExpression(VariableExpression expression) {
        removeUnusedParameterName(expression.name)
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        // Check for method call on a method with the same name as the parameter.
        // This handles the case of a parameter being a closure that is then invoked within the method, e.g.:
        //      private myMethod1(Closure closure) {
        //          println closure()
        //      }
        if (AstUtil.isMethodCallOnObject(call, 'this') && call.method instanceof ConstantExpression) {
            removeUnusedParameterName(call.method.value)
        }
        super.visitMethodCallExpression(call)
    }

    private void removeUnusedParameterName(String name) {
        unusedParameterNames.remove(name)
    }
}