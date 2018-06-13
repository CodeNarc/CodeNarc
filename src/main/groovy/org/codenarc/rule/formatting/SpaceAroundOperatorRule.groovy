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
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.*
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Check that there is at least one space (blank) or whitespace around each binary operator,
 * including: +, -, *, /, >>, <<, &&, ||, &, |, ?:, =, as.
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
    boolean ignoreParameterDefaultValueAssignments = true
    Class astVisitorClass = SpaceAroundOperatorAstVisitor
}

class SpaceAroundOperatorAstVisitor extends AbstractAstVisitor {

    private static final String QUOTE = '"'
    private static final String PRECEDED = 'preceded'
    private static final String FOLLOWED = 'followed'
    private static final String SURROUNDED = 'surrounded'

    private boolean withinDeclarationExpression

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        withinDeclarationExpression = true
        super.visitDeclarationExpression(expression)
        withinDeclarationExpression = false
    }

    @Override
    void visitTernaryExpression(TernaryExpression expression) {
        if (isFirstVisit(expression)) {
            if (expression instanceof ElvisOperatorExpression) {
                processElvisExpression(expression)
            } else {
                processTernaryExpression(expression)
            }
        }
        super.visitTernaryExpression(expression)
    }

    private void processTernaryExpression(TernaryExpression expression) {
        def opColumn = expression.columnNumber
        def line = sourceLine(expression)
        def beforeChar = line[opColumn - 2] as char

        // Known limitation: Ternary expression column does not always indicate column of '?'
        if (opColumn <= leftMostColumn(expression.booleanExpression)) {
            checkForSpaceAroundTernaryOperator(expression, line)
            return
        }

        if (!Character.isWhitespace(beforeChar)) {
            addViolationForOperator(expression, '?', PRECEDED)
        }
        if (opColumn < line.size() && !Character.isWhitespace(line[opColumn] as char)) {
            addViolationForOperator(expression, '?', FOLLOWED)
        }

        if (rightMostColumn(expression.trueExpression) + 1 == leftMostColumn(expression.falseExpression)) {
            addViolationForOperator(expression, ':', SURROUNDED)
        }
    }

    private void addViolationForOperator(ASTNode node, String operatorName, String precededFollowedOrSurrounded) {
        addViolation(node, "The operator ${QUOTE}${operatorName}${QUOTE} within class $currentClassName is not ${precededFollowedOrSurrounded} by a space or whitespace")
    }

//    private void addViolationForOperatorAndDescription(ASTNode node, String operatorName, String description, String precededFollowedOrSurrounded) {
//        addViolation(node, "The operator ${QUOTE}${operatorName}${QUOTE} for $description within class $currentClassName is not ${precededFollowedOrSurrounded} by a space or whitespace")
//    }

    private void checkForSpaceAroundTernaryOperator(TernaryExpression expression, String line) {
        if (expression.lineNumber == expression.lastLineNumber) {
            def hasWhitespaceAroundQuestionMark = (line =~ /\s\?\s/)
            if (!hasWhitespaceAroundQuestionMark) {
                addViolationForOperator(expression, '?', SURROUNDED)
            }

            def hasWhitespaceAroundColon = (line =~ /\s\:\s/)
            if (!hasWhitespaceAroundColon) {
                addViolationForOperator(expression, ':', SURROUNDED)
            }
        }
    }

    private void processElvisExpression(ElvisOperatorExpression expression) {
        String line = sourceCode.lines[expression.lineNumber - 1]

        /* for assert statements expression.columnNumber is not where the elvis operator "?:" starts, but where the
         * assertion starts. To handle this problem, look for the first instance of "?:" starting from the columnNumber
         */
        int index = line.indexOf('?:', expression.columnNumber - 1)

        if (index >= 0) {
            if (index > 0 && line.subSequence(index - 1, index + 2) =~ /\S\?\:/) {
                addViolationForOperator(expression, '?:', PRECEDED)
            }
            if (index + 3 < line.size() && line.subSequence(index, index + 3) =~ /\?\:(\S)/) {
                addViolationForOperator(expression, '?:', FOLLOWED)
            }
        }
    }

    @Override
    void visitField(FieldNode node) {
        if (node.initialExpression) {
            def line = sourceCode.lines[node.lineNumber - 1]
            if (line.contains('=')) {
                checkAssignmentWithinString(node, line)
            }
        }
        super.visitField(node)
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (!rule.ignoreParameterDefaultValueAssignments) {
            node.parameters.each { Parameter parameter ->
                checkMethodParameter(parameter)
            }
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    private void checkMethodParameter(Parameter parameter) {
        if (parameter.initialExpression) {
            def line = sourceCode.lines[parameter.lineNumber - 1]
            boolean allOnSameLine = parameter.lineNumber == parameter.lastLineNumber
            int endColumn = allOnSameLine ? parameter.lastColumnNumber : line.size()
            String paramString = line[(parameter.columnNumber - 1)..(endColumn - 2)]
            if (paramString.contains('=')) {
                checkAssignmentWithinString(parameter, paramString)
            }
        }
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (!isFirstVisit(expression)) {
            return
        }

        def op = expression.operation
        def opText = op.text
        def opEndColumn = op.startColumn + opText.size() - 1
        def line = sourceCode.lines[op.startLine - 1]

        boolean assignmentWithinDeclaration = (opText == '=') && withinDeclarationExpression

        if (expression instanceof DeclarationExpression && assignmentWithinDeclaration) {
            checkAssignmentWithinDeclaration(expression, line)
        }

        boolean arrayOperator = opText == '['
        boolean isOperatorAtIndex = op.startColumn != -1 && (line[op.startColumn - 1] == opText[0])
        boolean ignore = assignmentWithinDeclaration || arrayOperator || !isOperatorAtIndex
        String operator = expression.operation.text

        if (!ignore && op.startColumn > 1) {
            def beforeChar = line[op.startColumn - 2] as char

            if (!Character.isWhitespace(beforeChar)) {
                addViolationForOperator(expression, operator, PRECEDED)
            }
        }

        if (!ignore && opEndColumn != -1 && opEndColumn < line.size()) {
            def afterChar = line[opEndColumn] as char
            if (!Character.isWhitespace(afterChar)) {
                addViolationForOperator(expression, operator, FOLLOWED)
            }
        }
        super.visitBinaryExpression(expression)
    }

    private void checkAssignmentWithinDeclaration(BinaryExpression expression, String line) {
        DeclarationExpression declarationExpression = expression
        int left = declarationExpression.leftExpression.lastColumnNumber
        int right = declarationExpression.rightExpression.columnNumber
        boolean allOnSameLine = declarationExpression.leftExpression.lastLineNumber == declarationExpression.rightExpression.lineNumber
        if (allOnSameLine && left > 0 && right > 0) {
            String inBetweenString = line[(left - 1)..(right - 2)]
            checkAssignmentWithinString(expression, inBetweenString)
        }
    }

    private void checkAssignmentWithinString(ASTNode node, String string ) {
        if (!(string =~ '\\s=')) {
            addViolationForOperator(node, '=', PRECEDED)
        }
        if (!(string =~ '=\\s')) {
            addViolationForOperator(node, '=', FOLLOWED)
        }
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        if (expression.coerce && expression.lineNumber != -1) {
            boolean containsAsWithSpaces = (expression.lineNumber..expression.lastLineNumber).find { lineNumber ->
                String line = sourceCode.lines[lineNumber - 1]
                return line.find(/\sas\s/)
            }

            if (!containsAsWithSpaces) {
                addViolationForOperator(expression, 'as', SURROUNDED)
            }
        }
        super.visitCastExpression(expression)
    }

    private int rightMostColumn(Expression expression) {
        switch (expression) {
            case BinaryExpression:
                return rightMostColumn(expression.rightExpression)
            case MethodCallExpression:
                return expression.arguments.lastColumnNumber
            case PropertyExpression:
                return expression.property.lastColumnNumber
            case BooleanExpression:
                return rightMostColumn(expression.expression)
            default:
                expression.lastColumnNumber
        }
    }

    private int leftMostColumn(Expression expression) {
        return expression instanceof BinaryExpression ? leftMostColumn(expression.leftExpression) : expression.columnNumber
    }
}
