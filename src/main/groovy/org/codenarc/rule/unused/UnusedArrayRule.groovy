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

import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for array allocations that are not assigned or used (i.e., it is ignored).
 * Ignores unassigned arrays if the construction is the last statement within a block,
 * because it may be the intentional return value.
 *
 * @author Chris Mair
  */
class UnusedArrayRule extends AbstractAstVisitorRule {
    String name = 'UnusedArray'
    int priority = 2
    Class astVisitorClass = UnusedArrayAstVisitor
}

class UnusedArrayAstVisitor extends AbstractLastStatementInBlockAstVisitor {

    void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof ArrayExpression && !isLastStatementInBlock(statement)) {
            addViolation(statement, 'The result of the array expression is ignored')
        }
        super.visitExpressionStatement(statement)
    }
}
