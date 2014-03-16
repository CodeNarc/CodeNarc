/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.expr.ClosureListExpression

/**
 * Check that there is at least one space (blank) or whitespace following a semicolon that separates:
 *      - multiple statements on a single line
 *      - the clauses within a classic for loop, e.g. for (i=0;i<10;i++)
 *
 * @author Chris Mair
  */
class SpaceAfterSemicolonRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterSemicolon'
    int priority = 3
    Class astVisitorClass = SpaceAfterSemicolonAstVisitor
}

class SpaceAfterSemicolonAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBlockStatement(BlockStatement block) {
        def lastLine
        def lastColumn
        block.statements.each { statementExpression ->
            if (lastLine && statementExpression.lineNumber == lastLine && statementExpression.columnNumber == lastColumn + 1) {
                def stmtText = AstUtil.getNodeText(statementExpression, sourceCode)
                addViolation(statementExpression, "The statement \"${stmtText}\" within class $currentClassName is not preceded by a space or whitespace")
            }
            lastLine = statementExpression.lineNumber
            lastColumn = statementExpression.lastColumnNumber
        }
        super.visitBlockStatement(block)
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        if (forLoop.collectionExpression instanceof ClosureListExpression) {
            def lastColumn
            forLoop.collectionExpression.expressions.each { expression ->
                if (lastColumn && expression.columnNumber == lastColumn + 1) {
                    def exprText = AstUtil.getNodeText(expression, sourceCode)
                    addViolation(forLoop, "The for loop expression \"$exprText\" within class $currentClassName is not preceded by a space or whitespace")
                }
                lastColumn = expression.lastColumnNumber
            }
        }
        super.visitForLoop(forLoop)
    }
}
