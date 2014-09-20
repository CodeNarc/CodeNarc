/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement

/**
 * Helper AST visitor that adds rule violation if a return statement is encountered that returns a null constant. 
 *
 * @author Hamlet D'Arcy
 */
class NullReturnTracker extends AbstractAstVisitor {

    // NOTE: The provided parent must be a subclass of AbstractAstVisitor and
    //       implement the void handleClosure(ClosureExpression expression) method

    def parent
    def errorMessage
    void visitReturnStatement(ReturnStatement statement) {
        def expression = statement.expression
        if (expressionReturnsNull(expression)) {
            parent.addViolation(statement, errorMessage)
        }
        super.visitReturnStatement(statement)
    }

    private expressionReturnsNull(Expression expression) {

        def stack = [expression] as Stack  // alternative to recursion
        while (stack) {
            def expr = stack.pop()
            if (expr == ConstantExpression.NULL) {
                return true
            } else if (expr instanceof ConstantExpression && expr.value == null) {
                return true
            } else if (expr instanceof TernaryExpression) {
                stack.push(expr.trueExpression)
                stack.push(expr.falseExpression)
            } 
        }
        false
    }

    void visitClosureExpression(ClosureExpression expression) {
        parent.handleClosure(expression)
        // do not keep walking, let the parent start a new walk for this new scope
    }
}
