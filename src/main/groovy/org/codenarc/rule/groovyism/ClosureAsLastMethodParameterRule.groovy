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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil
import org.codenarc.util.GroovyVersion
import org.codenarc.util.WildcardPattern

/**
 * If a method is called and the last parameter is an inline closure it can be declared outside of the method call brackets.
 *
 * @author Marcin Erdmann
 * @author Chris Mair
 */
class ClosureAsLastMethodParameterRule extends AbstractAstVisitorRule {

    String name = 'ClosureAsLastMethodParameter'
    int priority = 3
    Class astVisitorClass = ClosureAsLastMethodParameterAstVisitor
    String ignoreCallsToMethodNames = ''
}

class ClosureAsLastMethodParameterAstVisitor extends AbstractMethodCallExpressionVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        def arguments = AstUtil.getMethodArguments(call)
        if (arguments && isClosure(arguments.last())) {
            boolean isViolation = false
            def lastArgument = arguments.last()
            if (GroovyVersion.isGroovyVersion2()) {
                def sourceLine = sourceCode.lines[call.lineNumber - 1]
                def firstChar = sourceLine[call.columnNumber - 1]

                // If a method call is surrounded by parentheses (possibly unnecessary) OR braces, then the AST includes those in the
                // MethodCall start/end column indexes. In that case, it gets too complicated. Just bail.
                if (firstChar == '(' || firstChar == '{') {
                    super.visitMethodCallExpression(call)
                    return
                }

                isViolation = call.lastLineNumber > lastArgument.lastLineNumber ||
                        (call.lastLineNumber == lastArgument.lastLineNumber &&
                                call.lastColumnNumber > lastArgument.lastColumnNumber)
            } else {
                isViolation = lastArgument.lastLineNumber < call.arguments.lastLineNumber ||
                        (lastArgument.lastLineNumber == call.arguments.lastLineNumber && lastArgument.lastColumnNumber < call.arguments.lastColumnNumber)
            }
            if (isViolation && isNotIgnoredMethodName(call)) {
                addViolation(call, "The last parameter to the '$call.methodAsString' method call is a closure and can appear outside the parenthesis")
            }
        }
    }

    private boolean isClosure(Expression expression) {
        return expression.getClass() == ClosureExpression
    }

    private boolean isNotIgnoredMethodName(MethodCallExpression methodCallExpression) {
        !(new WildcardPattern(rule.ignoreCallsToMethodNames, false).matches(methodCallExpression.method.text))
    }

}
