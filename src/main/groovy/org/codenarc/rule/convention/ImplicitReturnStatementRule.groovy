/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for methods that are missing an explicit return statement.
 * Skips void methods and 'def' methods.
 *
 * @author Chris Mair
 */
class ImplicitReturnStatementRule extends AbstractAstVisitorRule {

    String name = 'ImplicitReturnStatement'
    int priority = 3
    Class astVisitorClass = ImplicitReturnStatementAstVisitor
}

class ImplicitReturnStatementAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitMethodEx(MethodNode node) {
        boolean ignoreThisMethod = node.isVoidMethod() || node.isDynamicReturnType()

        if (!ignoreThisMethod) {
            boolean isMissingReturn = node.code &&
                    node.code instanceof BlockStatement &&
                    (isEmptyMethod(node) || !lastStatementIsReturnOrOtherAllowed(node))

            if (isMissingReturn) {
                String message = "The method ${node.name} in class ${currentClassName} is missing an explicit return"
                addViolation(node, message)
            }
        }
        super.visitMethodEx(node)
    }

    private boolean isEmptyMethod(MethodNode node) {
        return node.code.statements.isEmpty()
    }

    private boolean lastStatementIsReturnOrOtherAllowed(MethodNode node) {
        def lastStatement = node.code.statements[-1]
        return lastStatement instanceof ReturnStatement ||
                lastStatement instanceof TryCatchStatement ||
                lastStatement instanceof IfStatement ||
                lastStatement instanceof ForStatement ||
                lastStatement instanceof WhileStatement ||
                lastStatement instanceof DoWhileStatement ||
                lastStatement instanceof SwitchStatement ||
                lastStatement instanceof ThrowStatement
    }

}
