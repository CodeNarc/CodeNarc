/*
 * Copyright 2014 the original author or authors.
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
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.NullReturnTracker
import org.codenarc.util.AstUtil

/**
 * Checks for toString() methods that return null.
 *
 * @author Chris Mair
 */
class ToStringReturnsNullRule extends AbstractAstVisitorRule {

    String name = 'ToStringReturnsNull'
    int priority = 2
    Class astVisitorClass = ToStringReturnsNullAstVisitor
}

class ToStringReturnsNullAstVisitor extends AbstractAstVisitor {

    private static final String ERROR_MESSAGE = 'The toString() method within class %s returns null'

    @Override
    protected void visitMethodEx(MethodNode node) {
        if (AstUtil.isMethodNode(node, 'toString', 0)) {
            def errorMessage = String.format(ERROR_MESSAGE, currentClassName)

            // This will find all of the explicit return statements
            node.code?.visit(new NullReturnTracker(parent:this, errorMessage:errorMessage))

            checkForImplicitNullReturns(node, errorMessage)
        }
        super.visitMethodEx(node)
    }

    void handleClosure(ClosureExpression expression) {
        super.visitClosureExpression(expression)
    }

    private void checkForImplicitNullReturns(MethodNode node, String errorMessage) {
        def statements = node.code.statements
        if (statements.empty) {
            addViolation(node, errorMessage)
            return
        }

        def lastStatement = statements[-1]
        if (lastStatement instanceof ExpressionStatement && AstUtil.isNull(lastStatement.expression)) {
            addViolation(lastStatement, errorMessage)
        }
    }
}
