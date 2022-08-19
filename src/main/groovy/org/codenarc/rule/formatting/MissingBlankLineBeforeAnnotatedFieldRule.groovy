/*
 * Copyright 2020 the original author or authors.
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

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks that there is a blank line before a field declaration that uses annotations.
 */
class MissingBlankLineBeforeAnnotatedFieldRule extends AbstractAstVisitorRule {

    String name = 'MissingBlankLineBeforeAnnotatedField'
    int priority = 3
    Class astVisitorClass = MissingBlankLineBeforeAnnotatedFieldRuleAstVisitor
}

class MissingBlankLineBeforeAnnotatedFieldRuleAstVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode node) {
        if (node.annotations && node.lineNumber > 1) {
            def previousLine = sourceCode.line(node.lineNumber - 2).trim()
            if (previousLine && !isComment(previousLine)
                    && annotationIsNotOnFirstLineOfClass(node)
                    && annotationsNotOnSameLineAsFieldDeclaration(node)) {
                addViolation(node, 'There is no blank line before the declaration for field "' + node.name + '" that has annotations')
            }
        }
        super.visitField(node)
    }

    private boolean annotationIsNotOnFirstLineOfClass(FieldNode node) {
        node.lineNumber != AstUtil.findFirstNonAnnotationLine(getCurrentClassNode(), sourceCode) + 1
    }

    private boolean annotationsNotOnSameLineAsFieldDeclaration(FieldNode node) {
        int firstNonAnnotationLine = AstUtil.findFirstNonAnnotationLine(node, sourceCode)
        return node.annotations[0].lineNumber != firstNonAnnotationLine
    }

    private boolean isComment(String line) {
        line.startsWith('//') || line.startsWith('/*') || line.startsWith('*')
    }
}
