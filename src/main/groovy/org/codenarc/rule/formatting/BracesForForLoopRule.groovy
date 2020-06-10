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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.expr.ClosureListExpression
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks the location of the opening brace ({) for for loops. By default, requires them on the same line,
 * but the sameLine property can be set to false to override this.
 *
 * @author Hamlet D'Arcy
  */
class BracesForForLoopRule extends AbstractAstVisitorRule {

    String name = 'BracesForForLoop'
    int priority = 2
    boolean sameLine = true
    Class astVisitorClass = BracesForForLoopAstVisitor

}

class BracesForForLoopAstVisitor extends AbstractAstVisitor {

    @Override
    void visitForLoop(ForStatement node) {
        if (AstUtil.isBlock(node.loopBlock)) {
            int loopExpressionLastLine = findLastLineOfForExpressions(node)

            boolean isBraceOnSameLine = node.loopBlock.lineNumber == loopExpressionLastLine
            if (rule.sameLine) {
                if (!isBraceOnSameLine) {
                    addViolation(node, 'Braces should start on the same line')
                }
            } else {
                if (isBraceOnSameLine) {
                    addViolation(node, 'Braces should start on a new line')
                }
            }
        }
        super.visitForLoop(node)
    }

    private int findLastLineOfForExpressions(ForStatement node) {
        int loopExpressionLastLine = node.lineNumber

        // Groovy 3.x
        if (node.collectionExpression instanceof ClosureListExpression) {
            node.collectionExpression.expressions.each { e ->
                if (e.lastLineNumber > loopExpressionLastLine) {
                    loopExpressionLastLine = e.lastLineNumber
                }
            }
        }

        return loopExpressionLastLine
    }

}
