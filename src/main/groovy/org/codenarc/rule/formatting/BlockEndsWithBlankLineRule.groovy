/*
 * Copyright 2017 the original author or authors.
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

import static org.codenarc.util.AstUtil.findFirstNonAnnotationLine

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation

/**
 * Checks that code blocks such as method bodies, closures and control structure bodies do not end with an empty line.
 *
 * @author Marcin Erdmann
 */
class BlockEndsWithBlankLineRule extends AbstractAstVisitorRule {

    String name = 'BlockEndsWithBlankLine'
    int priority = 3
    Class astVisitorClass = BlockEndsWithBlankLineAstVisitor

}

class BlockEndsWithBlankLineAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBlockStatement(BlockStatement block) {
        if (isFirstVisit(block)) {
            checkForViolations(block)
        }
        super.visitBlockStatement(block)
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        isFirstVisit(node.code) && checkForViolations(node) //this is needed because of GROOVY-8426
        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        isFirstVisit(expression.code) && checkForViolations(expression) //this is needed because line numbers are incorrect on closure block statements
        super.visitClosureExpression(expression)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        //this is needed to avoid double violations because finally statement is a block statement that consists a single block statement
        if (statement.finallyStatement instanceof BlockStatement) {
            BlockStatement finallyStatement = statement.finallyStatement
            finallyStatement.statements*.class == [BlockStatement] && isFirstVisit(finallyStatement)
        }
        super.visitTryCatchFinally(statement)
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        isFirstVisit(statement.defaultStatement)
        checkForViolations(statement)
        super.visitSwitch(statement)
    }

    private void checkForViolations(ASTNode node) {
        def firstLineNumber = findFirstNonAnnotationLine(node, sourceCode)
        def length = node.lastLineNumber - firstLineNumber + 1
        if (length > 1 && lastSourceLine(node).trim().startsWith('}') && lastButOneCodeLine(node).trim().empty) {
            addViolation(node)
        }
    }

    private String lastButOneCodeLine(ASTNode node) {
        sourceCode.lines.get(node.lastLineNumber - 2)
    }

    private void addViolation(ASTNode node) {
        def violation = new Violation(
            rule: rule,
            lineNumber: node.lastLineNumber - 1,
            sourceLine: '',
            message: 'Code block ends with a blank line.'
        )
        violations.add(violation)
    }
}
