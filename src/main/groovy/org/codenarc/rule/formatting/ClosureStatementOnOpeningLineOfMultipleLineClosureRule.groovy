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
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for closure logic on first line (after ->) for a multi-line closure
 *
 * @author Chris Mair
 */
class ClosureStatementOnOpeningLineOfMultipleLineClosureRule extends AbstractAstVisitorRule {

    String name = 'ClosureStatementOnOpeningLineOfMultipleLineClosure'
    int priority = 2
    Class astVisitorClass = ClosureStatementOnOpeningLineOfMultilineClosureAstVisitor
}

class ClosureStatementOnOpeningLineOfMultilineClosureAstVisitor extends AbstractAstVisitor {

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        boolean isMultiLineClosure = expression.lastLineNumber > expression.lineNumber
        boolean closureHasMultipleStatements = expression.code instanceof BlockStatement && expression.code.statements.size() > 1
        int closureStartLineNumber = expression.lineNumber
        if (isMultiLineClosure && closureHasMultipleStatements && closureStartLineNumber == expression.code.lineNumber) {
            addViolation(expression, "The multi-line closure within class $currentClassName contains a statement on the opening line of the closure.")
        }
        super.visitClosureExpression(expression)
    }
}
