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
package org.codenarc.rule.braces

import groovy.transform.Memoized
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil

/**
 * Check the location of the closing brace of a class. By default, enforce that there must be a blank line before
 * the closing class brace, except if the class is empty and is written in a single line. A blank line is defined as
 any line that does not contain any visible characters.
 * This rule can be configured with the following properties:
 * <ul>
 *  <li><i>singleLineClassesAllowed</i>: a boolean property to forbid single line classes.
 *  If it is false single line classes are considered a violation. Default value is true</li>
 *  <li><i>blankLineBeforeClosingBrace</i>: a boolean property to define if there may be a blank line before the closing
 *  class brace. If it is false, the last line before the brace must not be blank. Otherwise, it must be blank. Default
 *  value is true</li>
 *<ul>
 * @author David Ausín
 */
class ClassEndsWithBlankLineBeforeClosingBraceRequiredRule extends AbstractAstVisitorRule {

    String name = 'ClassEndsWithBlankLineBeforeClosingBraceRequired'
    int priority = 2
    boolean singleLineClassesAllowed = true
    boolean blankLineBeforeClosingBrace = true
    Class astVisitorClass = ClassEndsWithBlankLineBeforeClosingBraceRequiredAstVisitor
}

class ClassEndsWithBlankLineBeforeClosingBraceRequiredAstVisitor extends AbstractAstVisitor {

    private static final int PENULTIMATE_LINE_OFFSET = 2
    private static final char CLOSE_BRACE_CHARACTER = '}'

    @Override
    protected void visitClassComplete(final ClassNode classNode) {

        if (isSingleLineClassViolation() && isSingleLineClass(classNode)) { return }

        if (rule.blankLineBeforeClosingBrace) {
            checkIfThereIsBlankLineBeforeClosingBrace(classNode)
        } else {
            checkIfThereIsNotBlankLineBeforeClosingBrace(classNode)
        }
    }

    private boolean isSingleLineClassViolation() {
        rule.singleLineClassesAllowed || !rule.blankLineBeforeClosingBrace
    }

    private void checkIfThereIsNotBlankLineBeforeClosingBrace(final ClassNode classNode) {
        final String trimmedLineBeforeClosingBrace = getPenultimateLine(classNode).trim()
        final String trimmedLineOfClosingBrace = getLastLine(classNode).trim()

        if (trimmedLineOfClosingBrace == CLOSE_BRACE_CHARACTER && trimmedLineBeforeClosingBrace.isEmpty()) {
            addViolation(classNode, 'Class ends with an empty line before the closing brace', classNode.getLastLineNumber())
        }
    }

    private void checkIfThereIsBlankLineBeforeClosingBrace(final ClassNode classNode) {
        if (isSingleLineClass(classNode)) {
            addViolation(classNode, 'Single line classes are not allowed', classNode.getLastLineNumber())
            return
        }

        final String lineBeforeClosingBrace = getPenultimateLine(classNode)
        final String trimmedLineOfClosingBrace = getLastLine(classNode).trim()
        if (trimmedLineOfClosingBrace != CLOSE_BRACE_CHARACTER || !lineBeforeClosingBrace.isEmpty()) {
            addViolation(classNode, 'Class does not end with a blank line before the closing brace', classNode.getLastLineNumber())
        }
    }

    private String getPenultimateLine(final ClassNode classNode) {
        Integer penultimateLastLineNumber = classNode.lastLineNumber - PENULTIMATE_LINE_OFFSET
        return AstUtil.getRawLine(sourceCode, penultimateLastLineNumber)
    }

    private String getLastLine(final ClassNode classNode) {
        return  AstUtil.getLastLineOfNodeText(classNode, sourceCode)
    }

    @Memoized
    private Boolean isSingleLineClass(final ClassNode classNode) {
        return AstUtil.getNodeText(classNode, sourceCode) == AstUtil.getLastLineOfNodeText(classNode, sourceCode)
    }

    private void addViolation(final ASTNode node, final String message, final int lineNumber) {
        if (lineNumber >= 0) {
            String sourceLine = AstUtil.getLastLineOfNodeText(node, getSourceCode())
            Violation violation = new Violation()
            violation.setRule(rule)
            violation.setLineNumber(lineNumber)
            violation.setSourceLine(sourceLine)
            violation.setMessage(message)
            violations.add(violation)
        }
    }
}