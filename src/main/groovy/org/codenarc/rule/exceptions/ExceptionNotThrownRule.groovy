/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Checks for an exception constructor call as the last statement within a catch block.
 *
 * @author Chris Mair
 */
class ExceptionNotThrownRule extends AbstractAstVisitorRule {

    String name = 'ExceptionNotThrown'
    int priority = 2
    Class astVisitorClass = ExceptionNotThrownAstVisitor
}

class ExceptionNotThrownAstVisitor extends AbstractAstVisitor {

    @Override
    void visitCatchStatement(CatchStatement statement) {
        def lastStatement = getLastStatement(statement.code)
        if (isLastStatementAnExceptionConstructorCall(lastStatement)) {
            def typeName = lastStatement.expression.type.name
            addViolation(lastStatement, "The catch statement within class $currentClassName constructs a [$typeName] but does not throw it")
        }
        super.visitCatchStatement(statement)
    }

    private Statement getLastStatement(BlockStatement block) {
        return block.statements.size() > 0 ? block.statements[-1] : null
    }

    private boolean isLastStatementAnExceptionConstructorCall(Statement lastStatement) {
        return lastStatement &&
            lastStatement instanceof ExpressionStatement &&
            lastStatement.expression instanceof ConstructorCallExpression &&
            lastStatement.expression.type.name.endsWith('Exception')
    }
}
