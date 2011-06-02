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
import java.util.regex.Pattern

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
    // \/\*.*?\*\/ matches multiline comments on a single line
    // \/\*.*$ matches from opening of a multiline comment to the end of the line
    // \/\/.* matches single line comments
    private final static Pattern COMMENTS_REGEXP = ~/(\/\*.*?\*\/)|(\/\*.*$)|(\/\/.*)/

    @Override
    protected String sourceLine(ASTNode node) {
        def nodeLines = sourceCode.lines[(node.lineNumber - 1)..(node.lastLineNumber - 1)] as ArrayList
        if (nodeLines.size() > 1) {
            nodeLines[0] = nodeLines.first()[(node.columnNumber - 1)..-1]
            nodeLines[-1] = nodeLines.last()[0..(node.lastColumnNumber - 2)]
            return nodeLines.join('\n')
        }
        return nodeLines.first()[(node.columnNumber - 1)..(node.lastColumnNumber - 2)]
    }


    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call)) {
            def arguments = AstUtil.getMethodArguments(call)
            if (arguments && arguments.last() instanceof ClosureExpression) {
                def lastExpressionLine = sourceCode.lines[call.lastLineNumber - 1][0..(call.lastColumnNumber - 2)]
                if (getLastCharacterAfterRemovingCommentsAndTrimming(lastExpressionLine) != '}') {
                    addViolation(call, "The last parameter to the '$call.methodAsString' method call is a closure an can appear outside the parenthesis")
                }
            }
        }
        super.visitMethodCallExpression(call)
    }

    private String getLastCharacterAfterRemovingCommentsAndTrimming(String lastExpressionLine) {
        lastExpressionLine.replaceAll(COMMENTS_REGEXP, '').trim()[-1]
    }

}
