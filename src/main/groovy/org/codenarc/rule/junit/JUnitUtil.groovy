/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.util.AstUtil

/**
 * Utility methods for JUnit rule classes. This class is not intended for general use.
 *
 * @author Chris Mair
  */
class JUnitUtil {

    /**
     * Return true if the MethodCallExpression represents a JUnit assert method call with the specified
     * method name and constant argument value. This handles either single-argument assert calls or
     * 2-argument assert methods where the first parameter is the assertion message.
     * @param methodCall - the MethodCallExpression of the method call
     * @param methodName - the name of the method
     * @param value - the argument value
     */
    protected static boolean isAssertCallWithConstantValue(MethodCallExpression methodCall, String methodName, Object value) {
        return isAssertCallWithValueMatching(methodCall, methodName) { v -> v == value }
    }

    protected static boolean isAssertCallWithNonNullConstantValue(MethodCallExpression methodCall, String methodName) {
        return isAssertCallWithValueMatching(methodCall, methodName) { v -> v != null }
    }

    private static boolean isAssertCallWithValueMatching(MethodCallExpression methodCall, String methodName, Closure closure) {
        def isMatch = false
        if (AstUtil.isMethodCall(methodCall, 'this', methodName)) {
            def args = methodCall.arguments.expressions
            def valueExpression = args.last()
            isMatch = args.size() in 1..2 &&
                valueExpression instanceof ConstantExpression &&
                closure(valueExpression.value)
        }
        isMatch
    }

    /**
     * Return true if the MethodCallExpression represents a JUnit assert method call with the specified
     * method name and constant argument value. This handles either single-argument assert calls or
     * 2-argument assert methods where the first parameter is the assertion message.
     * @param methodCall - the MethodCallExpression of the method call
     * @param methodName - the name of the method
     * @param literalEvaluatesToTrue - true if the argument value must evaluate to true using Groovy truth
     */
    protected static boolean isAssertCallWithLiteralValue(MethodCallExpression methodCall, String methodName, boolean literalEvaluatesToTrue) {
        def isMatch = false
        if (AstUtil.isMethodCall(methodCall, 'this', methodName)) {
            def args = methodCall.arguments.expressions
            isMatch = args.size() in 1..2 &&
            isLiteralWithValueThatEvaluatesTo(args.last(), literalEvaluatesToTrue)
        }
        isMatch
    }

    private static boolean isLiteralWithValueThatEvaluatesTo(Expression expression, boolean literalEvaluatesToTrue) {
        if (expression instanceof ConstantExpression) {
            def value = expression.properties['value']
            return value != null && (value as boolean) == literalEvaluatesToTrue
        }
        if (expression instanceof ListExpression) {
            return expression.expressions.isEmpty() == !literalEvaluatesToTrue
        }
        if (expression instanceof MapExpression) {
            return expression.mapEntryExpressions.isEmpty() == !literalEvaluatesToTrue
        }
    }

    protected static boolean isSetUpMethod(MethodNode methodNode) {
        (methodNode.name == 'setUp' &&
                methodNode.parameters.size() == 0 &&
                !AstUtil.getAnnotation(methodNode, 'Before') &&
                methodNode.code instanceof BlockStatement)
    }

    protected static boolean isTearDownMethod(MethodNode methodNode) {
        (methodNode.name == 'tearDown' &&
                methodNode.parameters.size() == 0 &&
                !AstUtil.getAnnotation(methodNode, 'After') &&
                methodNode.code instanceof BlockStatement)
    }

    /**
     * Tells you if an ASTNode is a test MethodNode. A method node is a MethodNode and is named test.* or is annotated
     * with @Test or @org.junit.Test,
     * @param node
     *       the node to analyze
     * @return
     *      true if the node is a test method
     */
    static boolean isTestMethod(ASTNode node) {
        if (!AstUtil.isPublic(node)) {
            return false
        }
        if (!(node instanceof MethodNode)) {
            return false
        }
        if (!node.isVoidMethod()) {
            return false
        }
        if (node.parameters?.length > 0) {
            return false
        }
        if (node.name?.startsWith('test')) {
            return true
        }
        node.properties['annotations']?.any { annotation ->
            def name = annotation?.properties['classNode']?.name
            name == 'Test' || name == 'org.junit.Test'
        }
    }

    /**
     * Private constructor. All members are static.
     */
    private JUnitUtil() { }

}
