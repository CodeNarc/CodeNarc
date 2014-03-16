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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Semicolons as line terminators are not required in Groovy: remove them. Do not use a semicolon as a replacement for empty braces on for and while loops; this is a confusing practice. 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessarySemicolonRule extends AbstractAstVisitorRule {
    String name = 'UnnecessarySemicolon'
    int priority = 3
    // ^\\s*\\*.*   == any line that starts whitespace and a *
    // ^\\*.*       == any line that starts with a *
    // /\*.*        == any line that contains the /* sequence
    // .*//.*       == any line that contains the // sequence
    // .*\*/.*      == any line that contains the */ sequence

    String excludePattern = '^\\s*\\*.*|^\\*.*|/\\*.*|.*//.*|.*\\*/.*'

    Class astVisitorClass = UnnecessarySemicolonAstVisitor

    // this rule is shared across threads and has state, so make the state thread local
    def temporaryViolations = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            []
        }
    }

    @Override
    void applyTo(SourceCode sourceCode, List violations) {

        temporaryViolations.get().addAll(getViolationsForSource(sourceCode))
        super.applyTo(sourceCode, violations)
        if (temporaryViolations.get()) {
            violations.addAll(temporaryViolations.get())
        }
        temporaryViolations.get().clear()
    }

    private List<Violation> getViolationsForSource(SourceCode sourceCode) {
        def result = []

        List lines = sourceCode.lines
        if (!lines) {
            return result
        }
        int lineNumber = 1
        for (String line : lines) {
            if (line.trim().endsWith(';') && !line.matches(excludePattern)) {
                result.add(
                        new Violation(
                                rule: this, lineNumber: lineNumber, sourceLine: line,
                                message: 'Semi-colons as line endings can be removed safely'
                        )
                )
            }
            lineNumber++
        }
        result
    }

}

class UnnecessarySemicolonAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstantExpression(ConstantExpression node) {

        // search inside multiline strings
        if (node.value instanceof String && node.lineNumber != node.lastLineNumber) {
            removeViolationsInRange(node.lineNumber, node.lastLineNumber - 1)
        }

        super.visitConstantExpression(node)
    }

    private removeViolationsInRange(start, end) {
        rule.temporaryViolations.get().removeAll { Violation v ->
            v.lineNumber >= start && v.lineNumber <= end
        }
    }
}
