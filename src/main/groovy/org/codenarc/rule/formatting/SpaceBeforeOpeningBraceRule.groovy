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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Check that there is at least one space (blank) or whitespace before each opening brace ("{").
 * This checks method/class/interface declarations, as well as try/for/while/if statements.
 *
 * A closure expression a preceded by an opening parenthesis, an opening square
 * brace ([), or a dollar sign ($) within a GString does not cause a violation.
 *
 * @author Chris Mair
 */
class SpaceBeforeOpeningBraceRule extends AbstractAstVisitorRule {

    String name = 'SpaceBeforeOpeningBrace'
    int priority = 3
    Class astVisitorClass = SpaceBeforeOpeningBraceAstVisitor
    boolean checkClosureMapEntryValue = true
}

class SpaceBeforeOpeningBraceAstVisitor extends AbstractSpaceAroundBraceAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {
        def line = sourceLineOrEmpty(node)
        def indexOfBrace = line.indexOf('{')
        if (indexOfBrace > 1) {
            if (isNotWhitespace(line, indexOfBrace)) {
                def typeName = node.isInterface() ? 'interface' : (node.isEnum() ? 'enum' : 'class')
                addViolation(node, "The opening brace for $typeName $currentClassName is not preceded by a space or whitespace")
            }
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        processMethodNode(node, 'method')
        super.visitMethodEx(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        processMethodNode(node, 'constructor')
        super.visitConstructor(node)
    }

    private void processMethodNode(MethodNode node, String keyword) {
        if (isFirstVisit(node.code) && node.code && !AstUtil.isFromGeneratedSourceCode(node)) {
            def line = sourceLineOrEmpty(node)
            if (line.contains('){')) {
                addOpeningBraceViolation(node, keyword)
            }
        }
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        if (isFirstVisit(block) && !AstUtil.isFromGeneratedSourceCode(block)) {
            def line = sourceLineOrEmpty(block)
            def startCol = block.columnNumber
            if (isNotWhitespace(line, startCol - 1)) {
                addOpeningBraceViolation(block, 'block')
            }
        }
        super.visitBlockStatement(block)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        isFirstVisit(expression.code)   // Register the code block so that it will be ignored in visitBlockStatement()
        if (isFirstVisit(expression)) {
            if (isCharacterPrecedingClosureInvalid(expression)) {
                addOpeningBraceViolation(expression, 'closure')
            }
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        if (!rule.checkClosureMapEntryValue && expression.valueExpression instanceof ClosureExpression) {
            isFirstVisit(expression.valueExpression)   // Register the closure so that it will be ignored in visitClosureExpression()
        }
        super.visitMapEntryExpression(expression)
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        if (isFirstVisit(statement)) {
            def line = sourceLineOrEmpty(statement)
            if (line.contains('){')) {
                addOpeningBraceViolation(statement, 'switch statement')
            }
        }
        super.visitSwitch(statement)
    }

    private boolean isCharacterPrecedingClosureInvalid(ClosureExpression expression) {
        String line = sourceLineOrEmpty(expression)
        int investigatedIndex = expression.columnNumber - 1
        return  isNotWhitespace(line, investigatedIndex) &&
                isNotOpeningParenthesis(line, investigatedIndex) &&
                isNotOpeningSquareBracket(line, investigatedIndex) &&
                isNotDollarInsideGString(line, investigatedIndex)
    }

    private boolean isNotDollarInsideGString(String line, int index) {
        if (isNotDollar(line, index)) {
            return true
        }
        return isNotInsideGString()
    }

    private boolean isNotDollar(String line, int index) {
        return isNotCharacter(line, '$' as char, index)
    }

    private boolean isNotOpeningParenthesis(String line, int index) {
        return isNotCharacter(line, '(' as char, index)
    }

    private boolean isNotOpeningSquareBracket(String line, int index) {
        return isNotCharacter(line, '[' as char, index)
    }

    private void addOpeningBraceViolation(ASTNode node, String keyword) {
        addViolation(node, "The opening brace for the $keyword in class $currentClassName is not preceded by a space or whitespace")
    }
}
