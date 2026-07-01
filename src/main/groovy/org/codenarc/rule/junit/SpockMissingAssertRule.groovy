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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
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
 * everything inside if/for/switch/... blocks is not an implicit assert, just a useless comparison (unless wrapped by a `with` or `verifyAll`).
 *
 * This rule finds such expressions, where an explicit call to assert would be required. Please note that the rule might
 * produce false positives, as it relies on method names to determine whether an expression has a boolean type or not.
 *
 * @author Jean André Gauthier
 * @author Daniel Clausen
  */
class SpockMissingAssertRule extends AbstractAstVisitorRule {

    String name = 'SpockMissingAssert'
    int priority = 3
    String specificationSuperclassNames = '*Specification'
    String specificationClassNames = null
    Class astVisitorClass = SpockMissingAssertAstVisitor
}

class SpockMissingAssertAstVisitor extends AbstractAstVisitor<SpockMissingAssertRule> {

    private String currentLabel = null

    private int nNestedStatements = 0

    @Override
    void visitDoWhileLoop(DoWhileStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitDoWhileLoop(statement)
            }
        }
    }

    @Override
    void visitForLoop(ForStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitForLoop(statement)
            }
        }
    }

    @Override
    void visitIfElse(IfStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitIfElse(statement)
            }
        }
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitSwitch(statement)
            }
        }
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitTryCatchFinally(statement)
            }
        }
    }

    @Override
    void visitWhileLoop(WhileStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            handleNestedStatement {
                super.visitWhileLoop(statement)
            }
        }
    }

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        visitIfInSpockClass {
            resetCurrentLabel()
            // Do not inspect fixture / helper methods
            if (SpockUtil.isSpockFeatureMethod(node)) {
                super.visitConstructorOrMethod(node, isConstructor)
            }
        }
    }

    private void resetCurrentLabel() {
        currentLabel = null
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        // Do not inspect declaration expressions
    }

    @Override
    void visitAssertStatement(AssertStatement statement) {
        // Do not inspect assert expressions
    }

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        visitIfInSpockClass {
            updateCurrentLabel(statement)
            // Do not inspect content in with/verifyAll methods
            if (isMethodsWithImplicitAssertionsExpression(statement)) {
                return
            }
            boolean isInLabelWithImplicitAssertions = currentLabel in SpockUtil.LABELS_WITH_IMPLICIT_ASSERTIONS
            boolean isInTopLevel = nNestedStatements == 0
            boolean isBoolean = SpockUtil.isBooleanExpression(statement)
            if (isInLabelWithImplicitAssertions && !isInTopLevel && isBoolean) {
                addViolation(statement, "'${currentLabel}:' might contain a boolean expression in a nested statement, which is not implicitly asserted")
            }
            visitCollectionIterationMethods(statement)
        }
    }

    private static boolean isMethodsWithImplicitAssertionsExpression(ExpressionStatement statement) {
        var variableAndMethod = SpockUtil.getVariableAndMethod(statement)
        var variable = variableAndMethod.v1
        var method = variableAndMethod.v2
        // To keep things simple, we only consider methods called on this
        return variable != null && variable.name == 'this' && method != null && SpockUtil.METHODS_WITH_IMPLICIT_ASSERTIONS.contains(method.value)
    }

    private void visitCollectionIterationMethods(ExpressionStatement statement) {
        // Inspect the arguments from collection iteration methods (i.e loop equivalents)
        if (isCollectionIterationMethods(statement) && statement.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCallExpression = statement.expression as MethodCallExpression
            methodCallExpression.arguments.visit(this)
        }
    }

    private static boolean isCollectionIterationMethods(ExpressionStatement statement) {
        var variableAndMethod = SpockUtil.getVariableAndMethod(statement)
        var method = variableAndMethod.v2
        // Heuristic: assume that methods whose name matches METHODS_FOR_COLLECTION_ITERATION are equivalent to loops
        return method != null && SpockUtil.METHODS_FOR_COLLECTION_ITERATION.contains(method.value)
    }

    private void updateCurrentLabel(Statement statement) {
        // Spock only treats top-level labels as blocks
        if (nNestedStatements == 0) {
            List<String> labels = statement.statementLabels
            if (labels != null) {
                Collection<String> spockLabels = labels.intersect(SpockUtil.SPOCK_LABELS)
                if (spockLabels.size() > 0) {
                    currentLabel = spockLabels.last()
                }
            }
        }
        super.visitStatement(statement)
    }

    private void handleNestedStatement(Closure callVisitorMethod) {
        nNestedStatements++
        callVisitorMethod()
        nNestedStatements--
    }

    private void visitIfInSpockClass(Closure callVisitorMethod) {
        if (SpockUtil.isSpockSpecification(currentClassNode, rule.specificationSuperclassNames, rule.specificationClassNames)) {
            callVisitorMethod()
        }
    }
}
