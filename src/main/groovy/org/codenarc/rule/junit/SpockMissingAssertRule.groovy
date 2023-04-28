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
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.util.regex.Pattern

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
    int priority = 2
    Class astVisitorClass = SpockMissingAssertAstVisitor
}

class SpockMissingAssertAstVisitor extends AbstractAstVisitor {

    // Intentionally omitting and, as it doesn't have any semantic impact
    private static final List<String> SPOCK_LABELS = ['given', 'when', 'then', 'expect', 'where', 'cleanup', 'setup']

    private static final List<String> LABELS_WITH_IMPLICIT_ASSERTIONS = ['then', 'expect']

    private static final List<String> METHODS_WITH_IMPLICIT_ASSERTIONS = ['with', 'verifyAll']

    private static final List<String> METHODS_FOR_COLLECTION_ITERATION = ['each', 'eachWithIndex', 'times']

    private static final List<Pattern> BOOLEAN_METHOD_PATTERNS = [~/^is(\p{Lu}.*)?/, ~/^has(\p{Lu}.*)?/, ~/^asBoolean$/, ~/^any(\p{Lu}.*)?/, ~/^contains(\p{Lu}.*)?/, ~/^every(\p{Lu}.*)?/, ~/^equals(\p{Lu}.*)?/]

    private static final List<String> RELATIONAL_OPERATORS = ['==', '!=', '<', '<=', '>', '>=', '===', '!==']

    private static final List<String> LOGICAL_OPERATORS = ['&&', '||']

    private static final List<String> REGEX_OPERATORS = ['==~']

    private static final List<String> INSTANCEOF_OPERATORS = ['instanceof']

    private static final List<String> MEMBERSHIP_OPERATORS = ['in']

    private String currentLabel = null

    private int nNestedStatements = 0

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
        // Do not inspect fixture / helper methods
        if (isSpockFeatureMethod(node)) {
            super.visitConstructorOrMethod(node, isConstructor)
        }
    }

    private void resetCurrentLabel() {
        currentLabel = null
    }

    private static boolean isSpockFeatureMethod(MethodNode node) {
        if (node.code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) node.code
            // To be considered as a feature method by Spock, the method must have at least one statement label.
            // More details can be found in org.spockframework.compiler.SpecParser.isFeatureMethod() at
            // https://github.com/spockframework/spock/blob/52e7688b3f89533857006539e5905c9b4121f32b/spock-core/src/main/java/org/spockframework/compiler/SpecParser.java#LL153C5-L153C5
            return block.statements.any(s -> s.statementLabels != null && !s.statementLabels.isEmpty())
        }
        return false
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
        updateCurrentLabel(statement)
        // Do not inspect content in with/verifyAll methods
        if (isMethodsWithImplicitAssertionsExpression(statement)) {
            return
        }
        boolean isInLabelWithImplicitAssertions = currentLabel in LABELS_WITH_IMPLICIT_ASSERTIONS
        boolean isInTopLevel = nNestedStatements == 0
        boolean isBoolean = isBooleanExpression(statement)
        if (isInLabelWithImplicitAssertions && !isInTopLevel && isBoolean) {
            addViolation(statement, "'${currentLabel}:' might contain a boolean expression in a nested statement, which is not implicitly asserted")
        }
        visitCollectionIterationMethods(statement)
    }

    private static boolean isBooleanExpression(ExpressionStatement statement) {
        // Handles literals & casts / coercion operators
        if (statement.expression.type.name == 'boolean' || statement.expression.type.name == 'Boolean') {
            return true
        }
        // Handles binary expressions
        if (statement.expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = statement.expression as BinaryExpression
            if (binaryExpression.operation.text in RELATIONAL_OPERATORS
                || binaryExpression.operation.text in LOGICAL_OPERATORS
                || binaryExpression.operation.text in REGEX_OPERATORS
                || binaryExpression.operation.text in INSTANCEOF_OPERATORS
                || binaryExpression.operation.text in MEMBERSHIP_OPERATORS
            ) {
                return true
            }
        }
        var variableAndMethod = getVariableAndMethod(statement)
        var method = variableAndMethod.v2
        // Heuristic: assume that methods whose name matches BOOLEAN_METHOD_PATTERNS return a boolean
        return method != null && BOOLEAN_METHOD_PATTERNS.any { it -> method.value.toString().matches(it) }
    }

    private static boolean isMethodsWithImplicitAssertionsExpression(ExpressionStatement statement) {
        var variableAndMethod = getVariableAndMethod(statement)
        var variable = variableAndMethod.v1
        var method = variableAndMethod.v2
        // To keep things simple, we only consider methods called on this
        return variable != null && variable.name == 'this' && method != null && METHODS_WITH_IMPLICIT_ASSERTIONS.contains(method.value)
    }

    private void visitCollectionIterationMethods(ExpressionStatement statement) {
        // Inspect the arguments from collection iteration methods (i.e loop equivalents)
        if (isCollectionIterationMethods(statement) && statement.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCallExpression = statement.expression as MethodCallExpression
            methodCallExpression.arguments.visit(this)
        }
    }

    private static boolean isCollectionIterationMethods(ExpressionStatement statement) {
        var variableAndMethod = getVariableAndMethod(statement)
        var method = variableAndMethod.v2
        // Heuristic: assume that methods whose name matches METHODS_FOR_COLLECTION_ITERATION are equivalent to loops
        return method != null && METHODS_FOR_COLLECTION_ITERATION.contains(method.value)
    }

    private static Tuple2<VariableExpression, ConstantExpression> getVariableAndMethod(ExpressionStatement statement) {
        var variable = null
        var method = null
        if (statement.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCall = statement.expression as MethodCallExpression
            if (methodCall.objectExpression instanceof VariableExpression) {
                variable = methodCall.objectExpression as VariableExpression
            }
            if (methodCall.method instanceof ConstantExpression) {
                method = methodCall.method as ConstantExpression
            }
        }
        return new Tuple2<>(variable, method)
    }

    private void updateCurrentLabel(Statement statement) {
        // Spock only treats top-level labels as blocks
        if (nNestedStatements == 0) {
            List<String> labels = statement.statementLabels
            if (labels != null) {
                Collection<String> spockLabels = labels.intersect(SPOCK_LABELS)
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
}
