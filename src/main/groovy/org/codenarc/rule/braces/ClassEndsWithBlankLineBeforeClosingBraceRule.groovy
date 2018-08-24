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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Violation
import org.codenarc.util.AstUtil

/**
 * Require a newline before the closing brace of a class
 *
 * @author David Ausín
 */
class ClassEndsWithBlankLineBeforeClosingBraceRule extends AbstractAstVisitorRule {

    String name = 'ClassEndsWithBlankLineBeforeClosingBrace'
    int priority = 2
    Class astVisitorClass = ClassEndsWithBlankLineBeforeClosingBraceAstVisitor
}

class ClassEndsWithBlankLineBeforeClosingBraceAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassComplete(final ClassNode classNode) {
    final String lineBeforeClosingBrace = AstUtil.getRawLine(sourceCode,  classNode.lastLineNumber - 2)
    if (!lineBeforeClosingBrace.isEmpty()) {
        addViolation(classNode, 'Class does not end with a blank line before the closing brace')
    }

    //final String getLastLineOfNodeText = AstUtil.getLastLineOfNodeText(node, getSourceCode())
    }

    @Override
    protected void addViolation(ASTNode node, String message) {
        int lineNumber = node.getLastLineNumber()
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