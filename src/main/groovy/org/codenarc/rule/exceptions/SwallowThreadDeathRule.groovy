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
package org.codenarc.rule.exceptions

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Detects code that catches java.lang.ThreadDeath without re-throwing it. If ThreadDeath is caught by a method, it is important that it be rethrown so that the thread actually dies.
 *
 * @author Rob Fletcher
 * @author Klaus Baumecker
  */
class SwallowThreadDeathRule extends AbstractAstVisitorRule {
    String name = 'SwallowThreadDeath'
    int priority = 2
    Class astVisitorClass = SwallowThreadDeathAstVisitor
}

class SwallowThreadDeathAstVisitor extends AbstractAstVisitor {

    private boolean withinCatchThreadDeath = false
    private String caughtVariableName = null
    private boolean foundThrow = false

    @Override
    void visitCatchStatement(CatchStatement statement) {
        if (statement.exceptionType.name == ThreadDeath.simpleName) {
            withinCatchThreadDeath = true
            foundThrow = false
            caughtVariableName = statement.variable.name
            super.visitCatchStatement(statement)
            withinCatchThreadDeath = false
            if (!foundThrow) {
                addViolation statement, 'ThreadDeath caught without being re-thrown. If ThreadDeath is caught by a method, it is important that it be rethrown so that the thread actually dies.'
            }
        }
    }

    @Override
    void visitThrowStatement(ThrowStatement statement) {
        if (withinCatchThreadDeath) {
            def expression = statement.expression
            if (expression instanceof VariableExpression && expression.name == caughtVariableName) {
                foundThrow = true
            } else if (expression instanceof ConstructorCallExpression && expression.type.name == ThreadDeath.simpleName) {
                foundThrow = true
            }
        }
    }

}
