/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Script bindings can cause bad concurrency bugs and can usually be replaced with local variables.
 *
 * @author Josh Chorlton
 * @author Chris Mair
 */
class NoScriptBindingsRule extends AbstractAstVisitorRule {

    String name = 'NoScriptBindings'
    int priority = 3

    Class astVisitorClass = NoScriptBindingsAstVisitor
}

class NoScriptBindingsAstVisitor extends AbstractAstVisitor {

    private final Stack variableNamesByBlockScope = [] as Stack
    private Set variableNamesInCurrentBlockScope

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression) &&
                currentClassNode.isScript() &&
                !(expression instanceof DeclarationExpression) &&
                expression.operation.text == '=' &&
                expression.leftExpression instanceof VariableExpression) {
            def name = expression.leftExpression.name

            if (!isVariableName(name)) {
                addViolation(expression, "The script variable [$name] does not have a type declaration. It will be bound to the script which could cause concurrency issues.")
            }
        }
        super.visitBinaryExpression(expression)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            variableNamesInCurrentBlockScope.addAll(varExpressions.name)
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        beforeBlock()
        super.visitBlockStatement(block)
        afterBlock()
    }

    private void beforeBlock() {
        variableNamesInCurrentBlockScope = []
        variableNamesByBlockScope.push(variableNamesInCurrentBlockScope)
    }

    private void afterBlock() {
        variableNamesByBlockScope.pop()
        variableNamesInCurrentBlockScope = variableNamesByBlockScope.empty() ? null : variableNamesByBlockScope.peek()
    }

    private boolean isVariableName(String name) {
        return name in variableNamesByBlockScope.flatten()
    }

}
