/*
 * Copyright 2011 the original author or authors.
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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks the location of the opening brace ({) for constructors and methods. By default, requires them on the same line, but the sameLine property can be set to false to override this.
 *
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class BracesForMethodRule extends AbstractAstVisitorRule {

    String name = 'BracesForMethod'
    int priority = 2
    Class astVisitorClass = BracesForMethodAstVisitor
    boolean sameLine = true
    boolean allowBraceOnNextLineForMultilineDeclarations = false
}

class BracesForMethodAstVisitor extends AbstractAstVisitor {

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (node.declaringClass?.isInterface() || node.isAbstract() || node.lineNumber == -1) {
            return
        }

        boolean containsRegex = hasOpeningBraceOnSameLine(node)

        if (rule.sameLine && !containsRegex) {
            if (!(rule.allowBraceOnNextLineForMultilineDeclarations && isMultilineWithOpeningBraceInNewLine(node))) {
                addViolation(node, "Opening brace for the method $node.name should start on the same line")
            }
        }

        if (!rule.sameLine && containsRegex) {
            addViolation(node, "Opening brace for the method $node.name should start on a new line")
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    private boolean hasOpeningBraceOnSameLine(MethodNode node) {
        if (node.exceptions) {
            def throwsLines = joinThrowsLines(node)
            return throwsLines =~ /throws\s+.+\{/
        }

        // Does not declare any exceptions
        int lastLineOfDeclaration = node.parameters ? node.parameters[-1].lineNumber : node.lineNumber
        int firstLineOfBlock = node.code.lineNumber
        def range = firstLineOfBlock..lastLineOfDeclaration
        return range.find { lineNumber ->
            return sourceCode.line(lineNumber - 1) =~ /\)\s*\{/
        }
    }

    private String joinThrowsLines(MethodNode node) {
        def range = node.exceptions[0].lineNumber .. node.exceptions[-1].lastLineNumber
        def lines = range.collect { lineNumber -> sourceCode.line(lineNumber - 1) }
        return lines.join(' ')
    }

    private boolean isMultilineWithOpeningBraceInNewLine(MethodNode methodNode) {
        int firstLineOfDeclaration = methodNode.lineNumber
        int lastLineOfDeclaration = methodNode.parameters ? methodNode.parameters[-1].lineNumber : methodNode.lineNumber
        lastLineOfDeclaration = methodNode.exceptions ? methodNode.exceptions[-1].lineNumber : lastLineOfDeclaration

        if (firstLineOfDeclaration == lastLineOfDeclaration) {
            return false
        }

        return sourceCode.line(methodNode.code.lineNumber - 1).trim().startsWith('{')
    }

}
