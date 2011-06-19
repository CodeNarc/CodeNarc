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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.ASTNode
import org.codenarc.util.SourceCodeUtil

/**
 * If a method is called and the last parameter is an inline closure it can be declared outside of the method call brackets.
 *
 * @author Marcin Erdmann
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class ClosureAsLastMethodParameterRule extends AbstractAstVisitorRule {
    String name = 'ClosureAsLastMethodParameter'
    int priority = 3
    Class astVisitorClass = ClosureAsLastMethodParameterAstVisitor
}

class ClosureAsLastMethodParameterAstVisitor extends AbstractAstVisitor {

    @Override
    protected String sourceLine(ASTNode node) {
        SourceCodeUtil.nodeSourceLines(sourceCode, node).join('\n')
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call)) {
            def arguments = AstUtil.getMethodArguments(call)
            if (arguments && arguments.last() instanceof ClosureExpression) {
                def lastColumnForClosure = arguments.last().lastColumnNumber
                def lastColumnForMethodCall = call.lastColumnNumber
                def lastColumnForMethodArguments = call.arguments.lastColumnNumber
                def sourceLine = sourceCode.lines[call.lineNumber - 1]
                def firstChar = sourceLine[call.columnNumber - 1]

                // If a method call is surrounded by parentheses (possibly unnecessary), then the AST includes those in the
                // MethodCall start/end column indexes. In that case, do not include the ending parentheses in the comparison.
                def endIndex = firstChar == '(' ? lastColumnForMethodArguments : lastColumnForMethodCall

                if (lastColumnForClosure < endIndex) {
                    addViolation(call, "The last parameter to the '$call.methodAsString' method call is a closure an can appear outside the parenthesis")
                }
            }
        }
        super.visitMethodCallExpression(call)
    }

}
