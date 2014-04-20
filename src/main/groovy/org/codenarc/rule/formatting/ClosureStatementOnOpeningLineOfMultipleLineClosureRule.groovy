/*
 * Copyright 2013 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for closure logic on first line (after ->) for a multi-line closure
 *
 * @author Chris Mair
 */
class ClosureStatementOnOpeningLineOfMultipleLineClosureRule extends AbstractAstVisitorRule {

    String name = 'ClosureStatementOnOpeningLineOfMultipleLineClosure'
    int priority = 3
    Class astVisitorClass = ClosureStatementOnOpeningLineOfMultipleLineClosureAstVisitor
}

class ClosureStatementOnOpeningLineOfMultipleLineClosureAstVisitor extends AbstractAstVisitor {

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        def block = expression.code
        boolean hasAtLeastOneStatement = !block.isEmpty()
        if (hasAtLeastOneStatement) {
            int closureStartLineNumber = expression.lineNumber
            def lastStatement = block.statements[-1]
            boolean isMultiLineClosure = lastStatement.lastLineNumber > block.lineNumber
            boolean hasCodeOnStartingLine = closureStartLineNumber == block.lineNumber
            if (isMultiLineClosure && hasCodeOnStartingLine) {
                addViolation(expression, "The multi-line closure within class $currentClassName contains a statement on the opening line of the closure.")
            }
        }
        super.visitClosureExpression(expression)
    }
}
