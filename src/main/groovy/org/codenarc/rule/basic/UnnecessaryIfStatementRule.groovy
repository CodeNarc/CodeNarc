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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.Expression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for if statements where the if and else blocks are merely returning
 * <code>true</code> and <code>false</code> constants. These cases can be replaced by a simple return
 * statement. Examples include:
 * <ul>
 *   <li><code>if (someExpression) return true else return false</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { return true } else { return false }</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { Boolean.TRUE } else { return Boolean.FALSE }</code> - can be replaced by <code>return someExpression</code></li>
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
        return (isReturnTrue(ifBlock) && isReturnFalse(elseBlock)) ||
               (isReturnFalse(ifBlock) && isReturnTrue(elseBlock))
    }

    private boolean isReturnTrue(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        return statement instanceof ReturnStatement && isTrue(statement.expression)
    }

    private boolean isReturnFalse(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        return statement instanceof ReturnStatement && isFalse(statement.expression)
    }

    private boolean isTrue(Expression expression) {
        return ((expression instanceof ConstantExpression) && expression.isTrueExpression()) ||
                expression.text == 'Boolean.TRUE'
    }

    private boolean isFalse(Expression expression) {
        return ((expression instanceof ConstantExpression) && expression.isFalseExpression()) || 
                expression.text == 'Boolean.FALSE'
    }

    private boolean hasElseBlock(IfStatement ifStatement) {
        return !ifStatement.elseBlock.empty
    }

    private Statement getStatement(Statement statement) {
        isSingleStatementBlock(statement) ? statement.statements.get(0) : statement
    }

    private boolean isSingleStatementBlock(Statement statement) {
        return statement instanceof BlockStatement && statement.statements.size() == 1
    }
}