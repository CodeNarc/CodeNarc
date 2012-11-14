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
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.MethodNode

/**
 * Check that there is at least one space (blank) or whitespace before each opening brace ("{").
 * This checks method/class/interface declarations, as well as try/for/while/if statements.
 *
 * @author Chris Mair
 */
class SpaceBeforeOpeningBraceRule extends AbstractAstVisitorRule {

    String name = 'SpaceBeforeOpeningBrace'
    int priority = 3
    Class astVisitorClass = SpaceBeforeOpeningBraceAstVisitor
}

class SpaceBeforeOpeningBraceAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        def line = sourceLineOrEmpty(node)
        def indexOfBrace = line.indexOf('{')
        if (indexOfBrace > 1) {
            if (!Character.isWhitespace(line[indexOfBrace - 1] as char)) {
                def typeName = node.isInterface() ? 'interface' : (node.isEnum() ? 'enum' : 'class')
                addViolation(node, "The opening brace for $typeName $currentClassName is not preceded by a space or whitespace")
            }
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        checkForOpeningBrace(node, "method ${node.name}")
        super.visitMethodEx(node)
    }

    @Override
    void visitIfElse(IfStatement ifElse) {
        if (ifElse.ifBlock instanceof BlockStatement) {
            checkForOpeningBraceAfter(ifElse.booleanExpression, 'if block')
        }
        if (ifElse.elseBlock instanceof BlockStatement) {
            def line = sourceLineOrEmpty(ifElse.elseBlock)
            if (line.contains('else{')) {
                addOpeningBraceViolation(ifElse.elseBlock, 'else block')
            }
        }
        super.visitIfElse(ifElse)
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        if (forLoop.loopBlock instanceof BlockStatement) {
            checkForOpeningBraceAfter(forLoop.collectionExpression, 'for block')
        }
        super.visitForLoop(forLoop)
    }

    @Override
    void visitWhileLoop(WhileStatement loop) {
        if (loop.loopBlock instanceof BlockStatement) {
            checkForOpeningBraceAfter(loop.booleanExpression, 'while block')
        }
        super.visitWhileLoop(loop)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        def tryLine = sourceLineOrEmpty(statement)
        if (tryLine.contains('try{')) {
            addOpeningBraceViolation(statement, 'try block')
        }

        statement.catchStatements.each { catchStatement ->
            def line = sourceLine(catchStatement)
            if (line =~ /catch\(.*\)\{/) {
                addOpeningBraceViolation(catchStatement, 'catch block')
            }
        }

        def finallyLine = sourceLineOrEmpty(statement.finallyStatement)
        if (finallyLine.contains('finally{')) {
            addOpeningBraceViolation(statement.finallyStatement, 'finally block')
        }

        super.visitTryCatchFinally(statement)
    }

    private String sourceLineOrEmpty(node) {
        node.lineNumber == -1 ? '' : sourceLine(node)
    }

    protected String lastSourceLineOrEmpty(ASTNode node) {
        return node.lastLineNumber == -1 ? '' : sourceCode.getLines().get(node.lastLineNumber - 1)
    }

    private void checkForOpeningBrace(ASTNode node, String keyword, int fromIndex=0) {
        def line = sourceLineOrEmpty(node)
        checkForOpeningBrace(node, line, keyword, fromIndex)
    }

    private void checkForOpeningBrace(ASTNode node, String line, String keyword, int fromIndex=0) {
        def indexOfBrace = line.indexOf('{', fromIndex)
        if (indexOfBrace > 1) {
            if (!Character.isWhitespace(line[indexOfBrace - 1] as char)) {
                addOpeningBraceViolation(node, keyword)
            }
        }
    }

    private void checkForOpeningBraceAfter(ASTNode node, String keyword) {
        def fromIndex = node.lastColumnNumber
        def lastLine = lastSourceLineOrEmpty(node)
        checkForOpeningBrace(node, lastLine, keyword, fromIndex)
    }

    private void addOpeningBraceViolation(ASTNode node, String keyword) {
        addViolation(node, "The opening brace for the $keyword in class $currentClassName is not preceded by a space or whitespace")
    }

}