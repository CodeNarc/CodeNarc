/*
 * Copyright 2021 the original author or authors.
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

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation

/**
 * Check for whitespace after opening parentheses and before closing parentheses
 *
 * @author Chris Mair
 */
class SpaceInsideParenthesesRule extends AbstractAstVisitorRule {

    String name = 'SpaceInsideParentheses'
    int priority = 3
    Class astVisitorClass = SpaceInsideParenthesesAstVisitor
}

class SpaceInsideParenthesesAstVisitor extends AbstractAstVisitor {

    private static final String MESSAGE_SPACE_AFTER_OPENING_PARENTHESIS = 'The opening parenthesis is followed by whitespace'
    private static final String MESSAGE_SPACE_BEFORE_CLOSING_PARENTHESIS = 'The closing parenthesis is preceded by whitespace'
    private static final String SINGLE_QUOTE = "'"
    private static final String DOUBLE_QUOTE = '"'
    private static final String FORWARD_SLASH = '/'
    private static final String END_OF_COMMENT = '*/'
    private static final String EMPTY_STRING = ''

    private boolean withinMultilineGString = false
    private boolean withinMultilineString = false
    private boolean withinMultilineComment = false

    @Override
    protected void visitClassComplete(ClassNode node) {
        if (node.lineNumber < 0) {
            return
        }

        int firstLine = node.lineNumber
        int lastLine = node.lastLineNumber
        (firstLine..lastLine).each { lineNumber ->
            String line = sourceCode.getLines()[lineNumber - 1]
            processSourceLine(line, lineNumber)
        }

        super.visitClassComplete(node)
    }

    private void processSourceLine(String line, int lineNumber) {
        String text = stripComments(line.trim())

        if (text.isEmpty()) {
            return
        }

        def countTripleDoubleQuote = text.count('"""')
        if (countTripleDoubleQuote || withinMultilineGString) {
            if (isOdd(countTripleDoubleQuote)) {
                withinMultilineGString = !withinMultilineGString
            }
            return
        }

        def countTripleSingleQuote = text.count("'''")
        if (countTripleSingleQuote || withinMultilineString) {
            if (isOdd(countTripleSingleQuote)) {
                withinMultilineString = !withinMultilineString
            }
            return
        }

        if (hasSpaceAfterOpeningParenthesis(text)) {
            addViolation(new Violation(rule: rule, lineNumber: lineNumber, sourceLine: text, message: MESSAGE_SPACE_AFTER_OPENING_PARENTHESIS))
        }

        if (hasSpaceBeforeClosingParenthesis(text)) {
            addViolation(new Violation(rule: rule, lineNumber: lineNumber, sourceLine: text, message: MESSAGE_SPACE_BEFORE_CLOSING_PARENTHESIS))
        }
    }

    private boolean isOdd(int number) {
        return number % 2 != 0
    }

    private String stripComments(String fullText) {
        def containsEndOfComment = fullText.contains(END_OF_COMMENT)
        if (withinMultilineComment && containsEndOfComment) {
            withinMultilineComment = false
            return EMPTY_STRING
        }

        if (withinMultilineComment) {
            return EMPTY_STRING
        }

        // Strip off from // or /* to the end of line
        def text = fullText
        int startCommentIndex = startOfCommentIndex(text)
        if (startCommentIndex != -1) {
            // This is a multi-line comment IF the line does not also contain the */ comment terminator
            withinMultilineComment = text.contains('/*') && !containsEndOfComment

            text = text.substring(0, startCommentIndex)
        }

        // We already stripped off trailing comments; if the line contains */ or **/, assume the preceding part of the line is a comment
        return text.contains(END_OF_COMMENT) ? EMPTY_STRING : text
    }

    private boolean hasSpaceAfterOpeningParenthesis(String text) {
        return matchesAndNotWithinString(text, /\(\s+\S/)
    }

    private boolean hasSpaceBeforeClosingParenthesis(String text) {
        return matchesAndNotWithinString(text, /\S\s+\)/)
    }

    private boolean matchesAndNotWithinString(String text, String regex) {
        def matcher = text =~ regex
        if (!matcher) {
            return false
        }
        // Ignore match if preceded and followed by the same quote or slash char
        def preceding = text.substring(0, matcher.start())
        def following = text.substring(matcher.end())
        def surroundedBySingleQuotes = preceding.contains(SINGLE_QUOTE) && following.contains(SINGLE_QUOTE)
        def surroundedByDoubleQuotes = preceding.contains(DOUBLE_QUOTE) && following.contains(DOUBLE_QUOTE)
        def surroundedByForwardSlashes = preceding.contains(FORWARD_SLASH) && following.contains(FORWARD_SLASH)
        return !surroundedBySingleQuotes && !surroundedByDoubleQuotes && !surroundedByForwardSlashes
    }

    private int startOfCommentIndex(String text) {
        int index = text.indexOf('//')
        if (index != -1) {
            return index
        }
        return text.indexOf('/*')
    }

}
