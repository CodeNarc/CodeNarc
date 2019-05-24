/*
 * Copyright 2018 the original author or authors.
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

import groovy.transform.Memoized
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil

/**
 * Check whether the class starts with a blank line. By default, it enforces that there must be a blank line after
 * the opening class brace, except:
 *   <ul>
 *     <li>If the class is synthetic (generated)</li>
 *     <li>If the class is empty and is written in a single line</li>
 *     <li>If the class is a Script class</li>
 *   <ul>
 * <p>
 * A blank line is defined as any line that does not contain any visible characters.
 * <p>
 * This rule can be configured with the following properties:
 * <ul>
 *   <li><i>ignoreSingleLineClasses</i>: a boolean property to ignore single line classes.
 *     If it is false, then single line classes are considered a violation. Default value is true.</li>
 *   <li><i>ignoreInnerClasses</i>: a boolean property to ignore inner classes.
 *     If it is false, then inner classes can cause violations. Default value is false.</li>
 *   <li><i>blankLineRequired</i>: a boolean property to define if there may be a blank line after the opening class
 *     brace. If it is false, the first content after the brace must not be a blank line. Otherwise, it must be a blank
 *     line. Default value is true</li>
 * <ul>
 *
 * @author David Ausín
 */
class ClassStartsWithBlankLineRule extends AbstractAstVisitorRule {

    String name = 'ClassStartsWithBlankLine'
    int priority = 3
    Class astVisitorClass = ClassStartsWithBlankLineAstVisitor
    boolean ignoreSingleLineClasses = true
    boolean ignoreInnerClasses = false
    boolean blankLineRequired = true
}

class ClassStartsWithBlankLineAstVisitor extends AbstractAstVisitor {

    private static final String OPENING_BRACE_CHARACTER = '{'

    @Override
    protected void visitClassComplete(ClassNode classNode) {
        if (classNode.lineNumber == -1) { return }

        if (classNode.isScript()) { return }

        if (classNode.outerClass && rule.ignoreInnerClasses) { return }

        if (isSingleLineClassViolation() && isSingleLineClass(classNode)) { return }

        if (rule.blankLineRequired) {
            checkIfThereIsBlankLineAfterOpeningBrace(classNode)
        } else {
            checkIfThereIsNotBlankLineAfterOpeningBrace(classNode)
        }
    }

    private void checkIfThereIsNotBlankLineAfterOpeningBrace(ClassNode classNode) {
        int openingBraceLineNumber = findLineNumberOfClassOpeningBrace(classNode)
        String openingBraceLine = getLine(openingBraceLineNumber)
        String charactersAfterOpeningBraceLine = getCharactersAfterFirstOpeningBrace(openingBraceLine)
        int lineAfterOpeningBraceNumber = openingBraceLineNumber + 1
        String lineAfterOpeningBrace = getLine(lineAfterOpeningBraceNumber)
        if (!lineAfterOpeningBrace.trim() && !charactersAfterOpeningBraceLine.trim()) {
            addViolation('Class starts with a blank line after the opening brace', lineAfterOpeningBraceNumber)
        }
    }

    private String getLine(int lineNumber) {
        AstUtil.getRawLine(sourceCode, lineNumber - 1)
    }

    private String getCharactersAfterFirstOpeningBrace(String line) {
        int openBracePosition = line.indexOf(OPENING_BRACE_CHARACTER)
        if (openBracePosition == -1) {
            return ''
        }
        line.drop(openBracePosition + 1)
    }

    private int findLineNumberOfClassOpeningBrace(ClassNode classNode) {
        int linesToOpeningBrace = sourceCode.lines.drop(classNode.lineNumber - 1)
                .findIndexOf { String currentLine -> currentLine.contains(OPENING_BRACE_CHARACTER) }
        classNode.lineNumber + linesToOpeningBrace
    }

    private void checkIfThereIsBlankLineAfterOpeningBrace(ClassNode classNode) {
        if (isSingleLineClass(classNode)) {
            addViolation('Single line classes are not allowed', classNode.lineNumber)
            return
        }

        int openingBraceLineNumber = findLineNumberOfClassOpeningBrace(classNode)
        String openingBraceLine = getLine(openingBraceLineNumber)
        String charactersAfterOpeningBraceLine = getCharactersAfterFirstOpeningBrace(openingBraceLine)
        int lineAfterOpeningBraceNumber = openingBraceLineNumber + 1
        String lineAfterOpeningBrace = getLine(lineAfterOpeningBraceNumber)
        if (charactersAfterOpeningBraceLine.trim()) {
            addViolation('Class does not start with a blank line after the opening brace',
                    openingBraceLineNumber)
            return
        }

        if (lineAfterOpeningBrace.trim()) {
            addViolation('Class does not start with a blank line after the opening brace',
                    lineAfterOpeningBraceNumber)
        }
    }

    private boolean isSingleLineClassViolation() {
        rule.ignoreSingleLineClasses || !rule.blankLineRequired
    }

    @Memoized
    private Boolean isSingleLineClass(ClassNode classNode) {
        return AstUtil.getNodeText(classNode, sourceCode) == AstUtil.getLastLineOfNodeText(classNode, sourceCode)
    }

    private void addViolation(String message, int lineNumber) {
        if (lineNumber >= 0) {
            String sourceLine = getLine(lineNumber)
            Violation violation = new Violation()
            violation.rule = rule
            violation.lineNumber = lineNumber
            violation.sourceLine = sourceLine
            violation.message  = message
            violations.add(violation)
        }
    }
}
