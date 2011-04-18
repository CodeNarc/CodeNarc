/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for object constructions that are not assigned or used (i.e., ignored).
 * Ignores unassigned objects if the construction is the last statement within a block,
 * because it may be the intentional return value.
 *
 * By default, this rule does not apply to test files.
 *
 * @author Chris Mair
 */
class UnusedObjectRule extends AbstractAstVisitorRule {
    String name = 'UnusedObject'
    int priority = 2
    Class astVisitorClass = UnusedObjectAstVisitor
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
}

class UnusedObjectAstVisitor extends AbstractLastStatementInBlockAstVisitor {

    void visitExpressionStatement(ExpressionStatement statement) {
        if (isFirstVisit(statement) &&
                statement.expression instanceof ConstructorCallExpression &&
                !statement.expression.isSuperCall() &&
                !statement.expression.isThisCall() &&
                !isLastStatementInBlock(statement)) {
            addViolation(statement, 'The instantiated object is not used')
        }
        super.visitExpressionStatement(statement)
    }
}
