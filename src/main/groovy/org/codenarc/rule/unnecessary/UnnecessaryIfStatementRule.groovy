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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Rule that checks for if statements where the if and else blocks are merely returning
 * <code>true</code> and <code>false</code> constants. These cases can be replaced by a simple return
 * statement. Examples include:
 * <ul>
 *   <li><code>if (someExpression) return true else return false</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { return true } else { return false }</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { return Boolean.TRUE } else { return Boolean.FALSE }</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) true; else false</code> - (implicit return) can be replaced by <code>someExpression</code></li>
 * </ul>
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnnecessaryIfStatementRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryIfStatement'
    int priority = 3
    Class astVisitorClass = UnnecessaryIfStatementAstVisitor
}

class UnnecessaryIfStatementAstVisitor extends AbstractAstVisitor  {

    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement) && hasElseBlock(ifStatement)) {
            if (areReturningTrueAndFalse(ifStatement.ifBlock, ifStatement.elseBlock)) {
                addViolation(ifStatement)
            }
        }
        super.visitIfElse(ifStatement)
    }

    private boolean areReturningTrueAndFalse(Statement ifBlock, Statement elseBlock) {
        (isReturnTrue(ifBlock) && isReturnFalse(elseBlock)) ||
               (isReturnFalse(ifBlock) && isReturnTrue(elseBlock))
    }

    private boolean isReturnTrue(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        (statement instanceof ReturnStatement || statement instanceof ExpressionStatement) &&
            AstUtil.isTrue(statement.expression)     
    }

    private boolean isReturnFalse(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        (statement instanceof ReturnStatement || statement instanceof ExpressionStatement) &&
            AstUtil.isFalse(statement.expression)
    }

    private boolean hasElseBlock(IfStatement ifStatement) {
        !ifStatement.elseBlock.empty
    }

    private Statement getStatement(Statement statement) {
        isSingleStatementBlock(statement) ? statement.statements.get(0) : statement
    }

    private boolean isSingleStatementBlock(Statement statement) {
        statement instanceof BlockStatement && statement.statements.size() == 1
    }
}