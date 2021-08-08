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
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
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
 * @author Chris Mair
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
        int classStartLine = AstUtil.findFirstNonAnnotationLine(classNode, sourceCode)

        boolean hasFieldOnStartingLine = classNode.fields.find { fieldNode -> classStartLine == fieldNode.lineNumber }
        boolean hasMethodOnStartingLine = classNode.methods.find { methodNode -> classStartLine == methodNode.lineNumber }

        if (hasFieldOnStartingLine || hasMethodOnStartingLine) {
            return
        }

        String nextLine = getLine(classStartLine + 1)
        if (!nextLine.trim()) {
            addViolation('Class starts with a blank line after the opening brace', classStartLine + 1)
        }
    }

    private void checkIfThereIsBlankLineAfterOpeningBrace(ClassNode classNode) {
        if (isSingleLineClass(classNode)) {
            addViolation('Single line classes are not allowed', classNode.lineNumber)
            return
        }

        int classStartLine = AstUtil.findFirstNonAnnotationLine(classNode, sourceCode)

        if (getLine(classStartLine).contains(classNode.nameWithoutPackage)) {
            String nextLine = findFirstLineAfterOpeningBrace(classStartLine)
            if (nextLine.trim()) {
                addViolation('Class does not start with a blank line after the opening brace', classStartLine + 1)
                return
            }
        }

        classNode.fields.each { fieldNode -> checkNonEmptyLineNumber(classStartLine, fieldNode.lineNumber) }
        classNode.methods.each { methodNode -> checkNonEmptyLineNumber(classStartLine, methodNode.lineNumber) }
    }

    private String findFirstLineAfterOpeningBrace(int startLine) {
        int lineNumber = startLine
        while(true) {
            String nextLine = getLine(lineNumber)
            if (nextLine == null) {
                return ''
            }
            if (nextLine.contains('{')) {
                return getLine(lineNumber + 1)
            }
            lineNumber++
        }
    }

    private String getLine(int lineNumber) {
        sourceCode.line(lineNumber - 1)
    }

    private void checkNonEmptyLineNumber(int classStartLine, int lineNumber) {
        if (lineNumber == classStartLine || lineNumber == classStartLine + 1) {
            addViolation('Class does not start with a blank line after the opening brace', lineNumber)
        }
    }

    private boolean isSingleLineClassViolation() {
        rule.ignoreSingleLineClasses || !rule.blankLineRequired
    }

    @Memoized
    private boolean isSingleLineClass(ClassNode classNode) {
        int classDeclarationLine = AstUtil.findClassDeclarationLineNumber(classNode, getSourceCode())
        return classDeclarationLine == classNode.lastLineNumber
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
