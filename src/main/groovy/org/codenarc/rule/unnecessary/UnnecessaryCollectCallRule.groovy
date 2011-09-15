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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * Some method calls to Object.collect(Closure) can be replaced with the spread operator. For instance, list.collect { it.multiply(2) } can be replaced by list*.multiply(2). 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCollectCallRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryCollectCall'
    int priority = 3
    Class astVisitorClass = UnnecessaryCollectCallAstVisitor
}

class UnnecessaryCollectCallAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'collect', 1)) {
            def args = AstUtil.getMethodArguments(call)

            whenOneStatementClosureFound(args) { exp, parmName ->
                if (isPossibleViolation(exp, parmName)) {
                    addViolationWithMessage(call, exp)
                }
            }
        }
    }

    private whenOneStatementClosureFound(List args, Closure callback) {
        // do we have one closure parameter?
        if (args[0] instanceof ClosureExpression && args[0].parameters?.length < 2) {
            ClosureExpression c = args[0]
            // do we have one statement in the closure?
            if (c.code instanceof BlockStatement && c.code.statements?.size() == 1) {
                def parmName = 'it'
                if (c.parameters?.length == 1) {
                    parmName = c.parameters[0].name
                }
                if (c.code.statements[0] instanceof ExpressionStatement) {
                    def exp = c.code.statements[0].expression
                    callback(exp, parmName)
                }
            }
        }
    }

    private static boolean isPossibleViolation(Expression exp, String parmName) {
        if (!(exp instanceof MethodCallExpression) && !(exp instanceof PropertyExpression)) {
            return false
        }

        if (!(exp.objectExpression instanceof VariableExpression)) {
            return false
        }
        if (exp.objectExpression.name != parmName) {
            return false
        }

        def finder = new VariableUsageFinder(targetVariable: parmName)

        if (exp instanceof MethodCallExpression) {
            exp.method.visit finder
            exp.arguments.visit finder
        } else if (exp instanceof PropertyExpression) {
            exp.property.visit finder
        }
        !finder.found
    }

    @SuppressWarnings('CatchThrowable')
    private addViolationWithMessage(MethodCallExpression call, MethodCallExpression exp) {
        try {
            addViolation(call, "The call to collect could probably be rewritten as a spread expression: ${call.objectExpression.text}*.${exp.method.text}${exp.arguments.text}")
        } catch (Throwable t) {
            addViolation(call, 'The call to collect could probably be rewritten as a spread expression. ')
        }
    }

    @SuppressWarnings('CatchThrowable')
    private addViolationWithMessage(MethodCallExpression call, PropertyExpression exp) {
        try {
            addViolation(call, "The call to collect could probably be rewritten as a spread expression: ${call.objectExpression.text}*.${exp.property.text}")
        } catch (Throwable t) {
            addViolation(call, 'The call to collect could probably be rewritten as a spread expression. ')
        }
    }
}

class VariableUsageFinder extends AbstractAstVisitor {
    String targetVariable
    boolean found = false

    @Override
    void visitVariableExpression(VariableExpression expression) {
        if (expression.name == targetVariable) {
            found = true
        } else {
            super.visitVariableExpression expression
        }
    }

}
