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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil
import org.codenarc.util.SourceCodeUtil

/**
 * If a method is called and the only parameter to that method is an inline closure then the brackets of the method call can be omitted.
 *
 * @author Marcin Erdmann
  */
class UnnecessaryParenthesesForMethodCallWithClosureRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryParenthesesForMethodCallWithClosure'
    int priority = 3
    Class astVisitorClass = UnnecessaryParenthesesForMethodCallWithClosureAstVisitor
}

class UnnecessaryParenthesesForMethodCallWithClosureAstVisitor extends AbstractMethodCallExpressionVisitor {
    private final static EMPTY_BRACKETS_PATTERN = /\s*\(\s*\)\s*/

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (!AstUtil.isFromGeneratedSourceCode(call.method)) {
            def arguments = AstUtil.getMethodArguments(call)
            if (arguments.size() == 1 && arguments.first() instanceof ClosureExpression) {
                def sourceBetweenMethodAndClosure = SourceCodeUtil.sourceLinesBetweenNodes(sourceCode, call.method, arguments.first()).join()
                if (sourceBetweenMethodAndClosure ==~ EMPTY_BRACKETS_PATTERN) {
                    addViolation(call, "Parentheses in the '$call.methodAsString' method call are unnecessary and can be removed.")
                }
            }
        }
    }
}
