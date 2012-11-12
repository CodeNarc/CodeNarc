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
        def line = sourceCode.lines[node.lineNumber - 1]
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
            checkForOpeningBrace(ifElse, 'if block')
        }
        if (ifElse.elseBlock instanceof BlockStatement) {
            def line = sourceCode.lines[ifElse.elseBlock.lineNumber - 1]
            if (line.contains('else{')) {
                addViolation(ifElse.elseBlock, "The opening brace for the else block in class $currentClassName is not preceded by a space or whitespace")
            }
        }
        super.visitIfElse(ifElse)
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        if (forLoop.loopBlock instanceof BlockStatement) {
            checkForOpeningBrace(forLoop, 'for block')
        }
        super.visitForLoop(forLoop)
    }

    @Override
    void visitWhileLoop(WhileStatement loop) {
        if (loop.loopBlock instanceof BlockStatement) {
            checkForOpeningBrace(loop, 'while block')
        }
        super.visitWhileLoop(loop)
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        checkForOpeningBrace(statement, 'try block')

        statement.catchStatements.each { catchStatement ->
            def line = sourceCode.lines[catchStatement.lineNumber - 1]
            if (line =~ /catch\(.*\)\{/) {
                addViolation(catchStatement, "The opening brace for the catch block in class $currentClassName is not preceded by a space or whitespace")
            }
        }

        def finallyLine = sourceCode.lines[statement.finallyStatement.lineNumber - 1]
        if (finallyLine.contains('finally{')) {
            addViolation(statement.finallyStatement, "The opening brace for the finally block in class $currentClassName is not preceded by a space or whitespace")
        }

        super.visitTryCatchFinally(statement)
    }

    private void checkForOpeningBrace(ASTNode node, String keyword) {
        def line = sourceCode.lines[node.lineNumber - 1]
        def indexOfBrace = line.indexOf('{')
        if (indexOfBrace > 1) {
            if (!Character.isWhitespace(line[indexOfBrace - 1] as char)) {
                addViolation(node, "The opening brace for the $keyword in class $currentClassName is not preceded by a space or whitespace")
            }
        }
    }
}