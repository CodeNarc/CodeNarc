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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.expr.ArrayExpression

/**
 * Checks for array allocations that are not assigned or used (i.e., it is ignored).
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedArrayRule extends AbstractAstVisitorRule {
    String name = 'UnusedArray'
    int priority = 2
    Class astVisitorClass = UnusedArrayAstVisitor
}

class UnusedArrayAstVisitor extends AbstractAstVisitor {

    void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof ArrayExpression) {
            addViolation(statement)
        }
        super.visitExpressionStatement(statement)
    }
}
