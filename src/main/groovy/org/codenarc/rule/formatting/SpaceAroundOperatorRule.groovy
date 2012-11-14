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

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.BooleanExpression

/**
 * Check that there is at least one space (blank) or whitespace around each binary operator,
 * including: +, -, *, /, >>, <<, &&, ||, &, |, ?:, =.
 *
 * Do not check dot ('.') operator. Do not check unary operators (!, +, -, ++, --, ?.).
 * Do not check array ('[') operator.
 *
 * Known limitation: Does not catch violations of missing space around equals operator (=) within a
 * declaration expression, e.g.   def x=23
 *
 * Known limitation: Does not catch violations of certain ternary expressions.
 *
 * @author Chris Mair
  */
class SpaceAroundOperatorRule extends AbstractAstVisitorRule {

    String name = 'SpaceAroundOperator'
    int priority = 3
    Class astVisitorClass = SpaceAroundOperatorAstVisitor
}

class SpaceAroundOperatorAstVisitor extends AbstractAstVisitor {

    private boolean withinDeclarationExpression

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        withinDeclarationExpression = true
        super.visitDeclarationExpression(expression)
        withinDeclarationExpression = false
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression) {
        if (expression instanceof ElvisOperatorExpression) {
            processElvisExpression(expression)
        }
        else {
            processTernaryExpression(expression)
        }
        super.visitTernaryExpression(expression)
    }

    private void processTernaryExpression(TernaryExpression expression) {
        def opColumn = expression.columnNumber
        def line = sourceLine(expression)
        def beforeChar = line[opColumn - 2] as char

        // Known limitation: Ternary expression column does not always indicate column of '?'
        if (opColumn <= leftMostColumn(expression.booleanExpression)) {
            return
        }

        if (!Character.isWhitespace(beforeChar)) {
            addViolation(expression, "The operator \"?\" within class $currentClassName is not preceded by a space or whitespace")
        }
        if (opColumn < line.size() && !Character.isWhitespace(line[opColumn] as char)) {
            addViolation(expression, "The operator \"?\" within class $currentClassName is not followed by a space or whitespace")
        }

        if (rightMostColumn(expression.trueExpression) + 1 == leftMostColumn(expression.falseExpression)) {
            addViolation(expression, "The operator \":\" within class $currentClassName is not surrounded by a space or whitespace")
        }
    }

    private void processElvisExpression(ElvisOperatorExpression expression) {
        if (expression.columnNumber == rightMostColumn(expression.trueExpression)) {
            addViolation(expression, "The operator \"?:\" within class $currentClassName is not preceded by a space or whitespace")
        }
        if (expression.lastColumnNumber == leftMostColumn(expression.falseExpression)) {
            addViolation(expression, "The operator \"?:\" within class $currentClassName is not followed by a space or whitespace")
        }
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        def op = expression.operation
        def opText = op.text
        def opEndColumn = op.startColumn + opText.size() - 1
        def line = sourceCode.lines[op.startLine - 1]

        boolean assignmentWithinDeclaration = (opText == '=') && withinDeclarationExpression
        boolean arrayOperator = opText == '['
        boolean ignore = assignmentWithinDeclaration || arrayOperator

        if (!ignore && op.startColumn > 1) {
            def beforeChar = line[op.startColumn - 2] as char
            if (!Character.isWhitespace(beforeChar)) {
                addViolation(expression, "The operator \"${expression.operation.text}\" within class $currentClassName is not preceded by a space or whitespace")
            }
        }

        if (!ignore && opEndColumn < line.size()) {
            def afterChar = line[opEndColumn] as char
            if (!Character.isWhitespace(afterChar)) {
                addViolation(expression, "The operator \"${expression.operation.text}\" within class $currentClassName is not followed by a space or whitespace")
            }
        }
        super.visitBinaryExpression(expression)
    }

    private int rightMostColumn(expression) {
        if (expression instanceof BinaryExpression) {
            return rightMostColumn(expression.rightExpression)
        }
        if (expression instanceof MethodCallExpression) {
            return expression.arguments.lastColumnNumber
        }
        if (expression instanceof PropertyExpression) {
            return expression.property.lastColumnNumber
        }
        if (expression instanceof BooleanExpression) {
            return rightMostColumn(expression.expression)
        }
        expression.lastColumnNumber
    }

    private int leftMostColumn(expression) {
        return expression instanceof BinaryExpression ? leftMostColumn(expression.leftExpression) : expression.columnNumber
    }
}