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
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil

/**
 * Check whether the class ends with a blank line. By default, it enforces that there must be a blank line before
 * the closing class brace, except:
 *   <ul>
 *     <li>If the class is synthetic (generated)</li>
 *     <li>If the class is empty and is written in a single line</li>
 *     <li>If the class is a Script class</li>
 *   <ul>
 * <p>
 * A blank line is defined as any line that does not contain any visible characters.
 * <p>
 * This rule can be configured with the following properties:
 *  <ul>
 *    <li><i>ignoreSingleLineClasses</i>: a boolean property to forbid single line classes.
 *      If it is false, then single line classes are considered a violation. Default value is true</li>
 *    <li><i>ignoreInnerClasses</i>: a boolean property to ignore inner classes.
 *      If it is false, then inner classes can cause violations. Default value is false.</li>
 *    <li><i>blankLineRequired</i>: a boolean property to define if there may be a blank line before the closing
 *      class brace. If it is false, the last line before the brace must not be blank. Otherwise, it must be blank. Default
 *      value is true</li>
 *  <ul>
 *
 * @author David Ausín
 */
class ClassEndsWithBlankLineRule extends AbstractAstVisitorRule {

    String name = 'ClassEndsWithBlankLine'
    int priority = 3
    boolean ignoreSingleLineClasses = true
    boolean ignoreInnerClasses = false
    boolean blankLineRequired = true
    Class astVisitorClass = ClassEndsWithBlankLineAstVisitor
}

class ClassEndsWithBlankLineAstVisitor extends AbstractAstVisitor {

    private static final int PENULTIMATE_LINE_OFFSET = 2
    private static final char CLOSE_BRACE_CHARACTER = '}'

    @Override
    protected void visitClassComplete(ClassNode classNode) {
        if (classNode.lineNumber == -1) { return }

        if (classNode.isScript()) { return }

        if (classNode.outerClass && rule.ignoreInnerClasses) { return }

        if (isSingleLineClassViolation() && isSingleLineClass(classNode)) { return }

        if (rule.blankLineRequired) {
            checkIfThereIsBlankLineBeforeClosingBrace(classNode)
        } else {
            checkIfThereIsNotBlankLineBeforeClosingBrace(classNode)
        }
    }

    private boolean isSingleLineClassViolation() {
        rule.ignoreSingleLineClasses || !rule.blankLineRequired
    }

    private void checkIfThereIsNotBlankLineBeforeClosingBrace(ClassNode classNode) {
        String trimmedLineBeforeClosingBrace = getPenultimateLine(classNode).trim()
        String trimmedLineOfClosingBrace = getLastLine(classNode).trim()

        if (trimmedLineOfClosingBrace == CLOSE_BRACE_CHARACTER && trimmedLineBeforeClosingBrace.isEmpty()) {
            addViolation(classNode, 'Class ends with an empty line before the closing brace', classNode.getLastLineNumber())
        }
    }

    private void checkIfThereIsBlankLineBeforeClosingBrace(ClassNode classNode) {
        if (isSingleLineClass(classNode)) {
            addViolation(classNode, 'Single line classes are not allowed', classNode.getLastLineNumber())
            return
        }

        String trimmedLineBeforeClosingBrace = getPenultimateLine(classNode).trim()
        String trimmedLineOfClosingBrace = getLastLine(classNode).trim()
        if (trimmedLineOfClosingBrace != CLOSE_BRACE_CHARACTER || !trimmedLineBeforeClosingBrace.isEmpty()) {
            addViolation(classNode, 'Class does not end with a blank line before the closing brace', classNode.getLastLineNumber())
        }
    }

    private String getPenultimateLine(ClassNode classNode) {
        Integer penultimateLastLineNumber = classNode.lastLineNumber - PENULTIMATE_LINE_OFFSET
        return AstUtil.getRawLine(sourceCode, penultimateLastLineNumber)
    }

    private String getLastLine(ClassNode classNode) {
        return  AstUtil.getLastLineOfNodeText(classNode, sourceCode)
    }

    @Memoized
    private Boolean isSingleLineClass(ClassNode classNode) {
        return AstUtil.getNodeText(classNode, sourceCode) == AstUtil.getLastLineOfNodeText(classNode, sourceCode)
    }

    private void addViolation(ASTNode node, String message, int lineNumber) {
        if (lineNumber >= 0) {
            String sourceLine = AstUtil.getLastLineOfNodeText(node, getSourceCode())
            Violation violation = new Violation()
            violation.rule = rule
            violation.lineNumber = lineNumber
            violation.sourceLine = sourceLine
            violation.message  = message
            violations.add(violation)
        }
    }
}
