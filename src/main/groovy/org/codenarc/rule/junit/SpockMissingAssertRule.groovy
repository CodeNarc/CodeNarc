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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
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
 * Spock treats all expressions on the first level of a then or expect block as an implicit assertion. However,
 * everything inside an if-block is not an implicit assert, just a useless comparison (unless wrapped by a `with` or `verifyAll`).
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
    private static final List<String> SPOCK_LABELS = ['given', 'when', 'then', 'expect', 'where', 'cleanup', 'setup']

    private static final List<String> LABELS_WITH_IMPLICIT_ASSERTIONS = ['then', 'expect']

    private static final List<String> METHODS_WITH_IMPLICIT_ASSERTIONS = ['with', 'verifyAll']

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
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        resetCurrentLabel()
        // Do not inspect helper methods
        if (isFeatureMethod(node)) {
            super.visitConstructorOrMethod(node, isConstructor)
        }
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        // Do not inspect declaration expressions
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (call instanceof VariableExpression) {
            boolean isThis = call.objectExpression.variable == 'this'
            boolean isMethodWithImplicitAssertion = METHODS_WITH_IMPLICIT_ASSERTIONS.contains(call.method.value)
            if (isThis && isMethodWithImplicitAssertion) {
                handleNestedImplicitAssertMethodCall {
                    super.visitMethodCallExpression(call)
                }
                return
            }
        }
        super.visitMethodCallExpression(call)
    }

    private static boolean isFeatureMethod(MethodNode node) {
        if (node.code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) node.code
            return block.statements.any(s -> s.getStatementLabels() != null && !s.getStatementLabels().isEmpty())
        }
        return false
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        updateCurrentLabel(statement)
        if (isMethodsWithImplicitAssertionsExpression(statement)) {
            return
        }
        boolean isInLabelWithImplicitAssertions = currentLabel in LABELS_WITH_IMPLICIT_ASSERTIONS
        boolean isInTopLevel = nNestedStatements == 0
        boolean isBoolean = isBooleanExpression(statement)
        boolean isInImplicitAssertMethodCall = nNestedImplicitAssertMethodCalls > 0
        if (isInLabelWithImplicitAssertions && !isInTopLevel && isBoolean && !isInImplicitAssertMethodCall) {
            addViolation(statement, "'${currentLabel}:' contains a boolean expression in a nested statement, which is not implicitly asserted")
        }
        super.visitExpressionStatement(statement)
    }

    private static boolean isBooleanExpression(ExpressionStatement statement) {
        statement.expression.type.name == 'boolean'
    }

    private static boolean isMethodsWithImplicitAssertionsExpression(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCall = statement.expression as MethodCallExpression
            if (methodCall.objectExpression instanceof VariableExpression) {
                VariableExpression variable = methodCall.objectExpression as VariableExpression
                if (variable.getName() == 'this') {
                    if (methodCall.method instanceof ConstantExpression) {
                        ConstantExpression method = methodCall.method as ConstantExpression
                        return METHODS_WITH_IMPLICIT_ASSERTIONS.contains(method.value)
                    }
                }
            }
        }
        return false
    }

    private void resetCurrentLabel() {
        currentLabel = null
    }

    private void updateCurrentLabel(Statement statement) {
        List<String> labels = statement.getStatementLabels()
        if (labels != null) {
            Collection<String> spockLabels = labels.intersect(SPOCK_LABELS)
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
