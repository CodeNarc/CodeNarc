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
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.ASTNode

/**
 * Check that there is at least one space (blank) or whitespace after each closing brace ("}").
 * This checks method/class/interface declarations, closure expressions and block statements.
 *
 * A closure expression followed by a dot operator (.), a comma, a closing parenthesis, a
 * closing square bracket (]), the spread-dot operator (*.), a semicolon or the null-safe
 * operator (?.) does not cause a violation.
 *
 * @author Chris Mair
 */
class SpaceAfterClosingBraceRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterClosingBrace'
    int priority = 3
    Class astVisitorClass = SpaceAfterClosingBraceAstVisitor

    /**
     * @deprecated Ignored
     * */
    boolean checkClosureMapEntryValue = true
}

class SpaceAfterClosingBraceAstVisitor extends AbstractSpaceAroundBraceAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        def line = lastSourceLineOrEmpty(node)
        def startColumn = node.columnNumber >= 0 ? node.columnNumber : 0
        def indexOfBrace = line.indexOf('}', startColumn)
        if (indexOfBrace > 1) {
            if (isNotWhitespace(line, indexOfBrace + 2) && isNotAllowedCharacterAfterClass(line, indexOfBrace + 2)) {
                def typeName = node.isInterface() ? 'interface' : (node.isEnum() ? 'enum' : 'class')
                addViolation(node, "The closing brace for $typeName $currentClassName is not followed by a space or whitespace")
            }
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        processMethodNode(node)
        super.visitMethodEx(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        processMethodNode(node)
        super.visitConstructor(node)
    }

    private void processMethodNode(MethodNode node) {
        if (isFirstVisit(node.code) && node.code && !AstUtil.isFromGeneratedSourceCode(node)) {
            def line = lastSourceLineOrEmpty(node.code)
            def lastCol = node.code.lastColumnNumber
            if (line.size() >= lastCol && isNotWhitespace(line, lastCol + 1) && line[lastCol - 1] == '}' ) {
                addClosingBraceViolation(node.code, 'block')
            }
        }
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        if (isFirstVisit(block) && !AstUtil.isFromGeneratedSourceCode(block)) {
            def startLine = sourceLineOrEmpty(block)
            def line = lastSourceLineOrEmpty(block)
            def lastCol = block.lastColumnNumber
            def startCol = block.columnNumber
            if (startLine[startCol - 1] == '{') {
                int lastIndex = indexOfClosingBrace(line, lastCol)
                if (isNotWhitespace(line, lastIndex + 2)) {
                    addClosingBraceViolation(block, 'block')
                }
            }
        }
        super.visitBlockStatement(block)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        isFirstVisit(expression.code)   // Register the code block so that it will be ignored in visitBlockStatement()
        if (isFirstVisit(expression)) {
            String line = lastSourceLineOrEmpty(expression)
            int lastCol = expression.lastColumnNumber
            int investigatedIndex = indexOfClosingBrace(line, lastCol) + 2
            if (isNotWhitespace(line, investigatedIndex) &&
                    isNotAllowedCharacterAfterClosure(line, investigatedIndex) &&
                    isNotInsideGString()) {
                addClosingBraceViolation(expression, 'closure')
            }
        }
        super.visitClosureExpression(expression)
    }

    private boolean isNotAllowedCharacterAfterClosure(String line, int index) {
        return index in 1..line.size() && !(line[index - 1] in ['.', ',', ')', ']', '*', '?', ';'])
    }

    private boolean isNotAllowedCharacterAfterClass(String line, int index) {
        return index in 1..line.size() && !(line[index - 1] in ['.', ')', ';'])
    }

    private void addClosingBraceViolation(ASTNode node, String keyword) {
        addViolation(node, "The closing brace for the $keyword in class $currentClassName is not followed by a space or whitespace")
    }

}
