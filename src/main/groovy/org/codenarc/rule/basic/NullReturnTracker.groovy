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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor

/**
 * Helper AST visitor that adds rule violation if a return statement is encountered that returns a null constant. 
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class NullReturnTracker extends AbstractAstVisitor {

    def parent
    def errorMessage
    def void visitReturnStatement(ReturnStatement statement) {
        def expression = statement.expression
        if (expressionReturnsNull(expression)) {
            parent.addViolation(statement, errorMessage)
        }
        super.visitReturnStatement(statement)
    }

    private expressionReturnsNull(Expression expression) {

        def stack = [expression] as Stack  // alternative to recursion
        while (stack) {
            expression = stack.pop()
            if (expression == ConstantExpression.NULL) {
                return true
            } else if (expression instanceof ConstantExpression && expression.value == null) {
                return true
            } else if (expression instanceof TernaryExpression) {
                stack.push(expression.trueExpression)
                stack.push(expression.falseExpression)
            } 
        }
        false
    }

    def void visitClosureExpression(ClosureExpression expression) {
        parent.handleClosure(expression)
        // do not keep walking, let the parent start a new walk for this new scope
    }
}
