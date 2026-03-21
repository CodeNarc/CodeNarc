/*
 * Copyright 2026 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.util.WildcardPattern

import java.util.regex.Pattern

/**
 * Utility methods for Spock rule classes. This class is not intended for general use.
 *
 * @author Leonard Bruenings
 */
class SpockUtil {

    // Intentionally omitting 'and', as it doesn't have any semantic impact
    static final List<String> SPOCK_LABELS = ['given', 'when', 'then', 'expect', 'where', 'cleanup', 'setup', 'combined', 'filter']

    static final List<String> LABELS_WITH_IMPLICIT_ASSERTIONS = ['then', 'expect', 'filter']

    static final List<String> METHODS_WITH_IMPLICIT_ASSERTIONS = ['with', 'verifyAll']

    static final List<String> METHODS_FOR_COLLECTION_ITERATION = ['each', 'eachWithIndex', 'times']

    private static final List<Pattern> BOOLEAN_METHOD_PATTERNS = [
        ~/^is(\p{Lu}.*)?/,
        ~/^has(\p{Lu}.*)?/,
        ~/^asBoolean$/,
        ~/^any(\p{Lu}.*)?/,
        ~/^contains(\p{Lu}.*)?/,
        ~/^every(\p{Lu}.*)?/,
        ~/^equals(\p{Lu}.*)?/,
    ]

    private static final Set<String> BOOLEAN_OPERATORS = [
        '==', '!=', '<', '<=', '>', '>=', '===', '!==',  // relational
        '&&', '||',                                      // logical
        '==~',                                           // regex
        'instanceof',                                    // type check
        'in',                                            // membership
    ] as Set

    static boolean isSpockSpecification(ClassNode classNode, String specificationSuperclassNames, String specificationClassNames) {
        def superClassPattern = new WildcardPattern(specificationSuperclassNames)
        def classNamePattern = new WildcardPattern(specificationClassNames, false)
        return superClassPattern.matches(classNode.superClass.name) || classNamePattern.matches(classNode.name)
    }

    static boolean isBooleanExpression(ExpressionStatement statement) {
        if (statement.expression.type.name == 'boolean' || statement.expression.type.name == 'Boolean') {
            return true
        }
        if (statement.expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = statement.expression as BinaryExpression
            if (binaryExpression.operation.text in BOOLEAN_OPERATORS) {
                return true
            }
        }
        var variableAndMethod = getVariableAndMethod(statement)
        var method = variableAndMethod.v2
        return method != null && BOOLEAN_METHOD_PATTERNS.any { it -> method.value.toString().matches(it) }
    }

    static boolean isImplicitAssertBlock(String label) {
        return label in LABELS_WITH_IMPLICIT_ASSERTIONS
    }

    static boolean isSpockFeatureMethod(MethodNode node) {
        if (node.code instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) node.code
            return block.statements.any(s -> s.statementLabels != null && !s.statementLabels.intersect(SPOCK_LABELS).isEmpty())
        }
        return false
    }

    static Tuple2<VariableExpression, ConstantExpression> getVariableAndMethod(ExpressionStatement statement) {
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
        return Tuple.tuple(variable, method)
    }

    static String getMethodName(MethodCallExpression methodCall) {
        if (methodCall.method instanceof ConstantExpression) {
            return (methodCall.method as ConstantExpression).value?.toString()
        }
        return null
    }

    static ClosureExpression getClosureArgument(MethodCallExpression methodCall) {
        def args = methodCall.arguments
        if (args.expressions) {
            def lastArg = args.expressions.last()
            if (lastArg instanceof ClosureExpression) {
                return lastArg as ClosureExpression
            }
        }
        return null
    }

    static boolean closureContainsAssertions(ClosureExpression closure, boolean checkBooleanExpressions) {
        if (!(closure.code instanceof BlockStatement)) {
            return false
        }
        BlockStatement block = closure.code as BlockStatement
        return block.statements.any { Statement stmt ->
            if (stmt instanceof AssertStatement) {
                return true
            }
            if (checkBooleanExpressions && stmt instanceof ExpressionStatement) {
                return isBooleanExpression(stmt as ExpressionStatement)
            }
            return false
        }
    }

    private SpockUtil() { }
}
