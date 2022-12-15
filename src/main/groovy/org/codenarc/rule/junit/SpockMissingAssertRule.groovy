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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Spocks treats all expressions on the first level of a then or expect block as an implicit assertion. However,
 * everything inside an if-block is not an implicit assert, just a useless comparison.
 *
 * This rule finds such expressions, where an explicit call to assert would be required.
 *
 * @author Jean André Gauthier
 * @author Daniel Clausen
  */
class SpockMissingAssertRule extends AbstractAstVisitorRule {

    String name = 'SpockMissingAssert'
    int priority = 2
    Class astVisitorClass = SpockMissingAssertAstVisitor
}

class SpockMissingAssertAstVisitor extends AbstractAstVisitor {

    // Intentionally omitting and, as it doesn't have any semantic impact
    private final List<String> spockLabels = ['given', 'when', 'then', 'expect', 'where', 'cleanup', 'setup']

    private final List<String> labelsWithImplicitAssertions = ['then', 'expect']

    private final List<String> methodsWithImplicitAssertions = ['with', 'verifyAll']

    private String currentLabel = null

    private int nNestedStatements = 0

    private int nNestedImplicitAssertMethodCalls = 0

    @Override
    void visitDoWhileLoop(DoWhileStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitDoWhileLoop(statement)
        }
    }

    @Override
    void visitForLoop(ForStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitForLoop(statement)
        }
    }

    @Override
    void visitIfElse(IfStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitIfElse(statement)
        }
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitSwitch(statement)
        }
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitTryCatchFinally(statement)
        }
    }

    @Override
    void visitWhileLoop(WhileStatement statement) {
        updateCurrentLabel(statement)
        handleNestedStatement {
            super.visitWhileLoop(statement)
        }
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        boolean isThis = call.objectExpression.variable == 'this'
        boolean isMethodWithImplicitAssertion = methodsWithImplicitAssertions.contains(call.method.value)
        if (isThis && isMethodWithImplicitAssertion) {
            handleNestedImplicitAssertMethodCall {
                super.visitMethodCallExpression(call)
            }
        } else {
            super.visitMethodCallExpression(call)
        }
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        updateCurrentLabel(statement)
        boolean isInLabelWithImplicitAssertions = currentLabel in labelsWithImplicitAssertions
        boolean isInTopLevel = nNestedStatements == 0
        boolean isBoolean = statement.expression.type.name == 'boolean'
        boolean isInImplicitAssertMethodCall = nNestedImplicitAssertMethodCalls > 0
        if (isInLabelWithImplicitAssertions && !isInTopLevel && isBoolean && !isInImplicitAssertMethodCall) {
            addViolation(statement, "'${currentLabel}:' contains a boolean expression in a nested statement, which is not implicitly asserted")
        }
        super.visitExpressionStatement(statement)
    }

    private void updateCurrentLabel(Statement statement) {
        var labels = statement.getStatementLabels()
        if (labels != null) {
            var spockLabels = labels.intersect(spockLabels)
            if (spockLabels.size() > 0) {
                currentLabel = spockLabels.last()
            }
        }
        super.visitStatement(statement)
    }

    private void handleNestedStatement(Closure callVisitorMethod) {
        nNestedStatements++
        callVisitorMethod()
        nNestedStatements--
    }

    private void handleNestedImplicitAssertMethodCall(Closure callVisitorMethod) {
        nNestedImplicitAssertMethodCalls++
        callVisitorMethod()
        nNestedImplicitAssertMethodCalls--
    }
}
