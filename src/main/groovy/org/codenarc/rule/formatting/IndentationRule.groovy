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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Check indentation for class and method declarations
 *
 * @author Chris Mair
 */
class IndentationRule extends AbstractAstVisitorRule {

    String name = 'Indentation'
    int priority = 3
    Class astVisitorClass = IndentationAstVisitor
    int spacesPerIndentLevel = 4

}

class IndentationAstVisitor extends AbstractAstVisitor {

    private int indentLevel = 0
    private final Set<Integer> fieldLineNumbers = []
    private final Set<Integer> statementLineNumbers = []

    @Override
    protected void visitClassEx(ClassNode node) {
        indentLevel = nestingLevelForClass(node)

        boolean isInnerClass = node instanceof InnerClassNode
        boolean isAnonymous = isInnerClass && node.anonymous
        if (!isAnonymous) {
            checkForCorrectColumn(node, "class ${node.getNameWithoutPackage()}")
        }
        indentLevel++
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        checkForCorrectColumn(node, "method ${node.name} in class ${currentClassName}")
        super.visitMethodEx(node)
    }

    @Override
    void visitField(FieldNode node) {
        if (!fieldLineNumbers.contains(node.lineNumber)) {
            fieldLineNumbers << node.lineNumber
            checkForCorrectColumn(node, "field ${node.name} in class ${currentClassName}")
        }
        super.visitField(node)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        indentLevel++
        block.statements.each { statement ->
            // Skip statements on the same line as another statement or a field declaration
            if (!statementLineNumbers.contains(statement.lineNumber) && !fieldLineNumbers.contains(statement.lineNumber)) {
                statementLineNumbers << statement.lineNumber

                // Ignore nested BlockStatement (e.g. finally blocks)
                boolean isBlockStatement = statement instanceof BlockStatement
                if (!isBlockStatement) {
                   checkForCorrectColumn(statement, "statement on line ${statement.lineNumber} in class ${currentClassName}")
                }
            }
        }
        indentLevel--
        super.visitBlockStatement(block)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        indentLevel++
        super.visitClosureExpression(expression)
        indentLevel--
    }

    @Override
    void visitIfElse(IfStatement ifElse) {
        indentLevel++
        super.visitIfElse(ifElse)
        indentLevel--
    }

    @Override
    void visitTryCatchFinally(TryCatchStatement statement) {
        indentLevel++
        super.visitTryCatchFinally(statement)
        indentLevel--
    }

    @Override
    void visitSwitch(SwitchStatement statement) {
        statement.caseStatements.each { caseStatement ->
            statementLineNumbers << caseStatement.lineNumber
        }
        statementLineNumbers << statement.defaultStatement.lineNumber
        indentLevel++
        super.visitSwitch(statement)
        indentLevel--
    }

    @Override
    void visitCaseStatement(CaseStatement statement) {
        indentLevel++
        super.visitCaseStatement(statement)
        indentLevel--
    }

    @Override
    void visitForLoop(ForStatement forLoop) {
        indentLevel++
        super.visitForLoop(forLoop)
        indentLevel--
    }

    private void checkForCorrectColumn(ASTNode node, String description) {
        int expectedColumn = columnForIndentLevel(indentLevel)
        if (node.columnNumber != expectedColumn) {
            addViolation(node, "The $description is at the incorrect indent level: Expected column $expectedColumn but was ${node.columnNumber}")
        }
    }

    private int nestingLevelForClass(ClassNode node) {
        // If this is a nested class, then add one to the outer class level
        int level = node.outerClass ? nestingLevelForClass(node.outerClass) + 1 : 0

        // If this class is defined within a method, add one to the level
        level += node.enclosingMethod ? 1 : 0

        return level
    }

    private int columnForIndentLevel(int indentLevel) {
        return indentLevel * rule.spacesPerIndentLevel + 1
    }

}
