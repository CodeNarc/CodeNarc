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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.stmt.*

/**
 * Rule that checks for unnecessary if statements. If/else statements are considered unnecessary for
 * the four scenarios described below.
 * <p/>
 * (1) When the if and else blocks contain only an explicit return of <code>true</code> and <code>false</code>
 * constants. These cases can be replaced by a simple return statement. Examples include:
 * <ul>
 *   <li><code>if (someExpression) return true else return false</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { return true } else { return false }</code> - can be replaced by <code>return someExpression</code></li>
 *   <li><code>if (someExpression) { return Boolean.TRUE } else { return Boolean.FALSE }</code> - can be replaced by <code>return someExpression</code></li>
 * </ul>
 *
 * (2) When the if statement is the last statement in a block and the if and else blocks contain only
 * <code>true</code> and <code>false</code> expressions. This is an implicit return of true/false.
 * For example, the if statement in the following code can be replaced by <code>someExpression</code>:
 *      <code>
 *      boolean myMethod() {
 *          doSomething()
 *          if (someExpression) true; else false
 *      }
 *      </code>
 *
 * (3) When the second-to-last statement in a block is an if statement with no else, where the block contains a single
 * return statement, and the last statement in the block is a return statement, and one return statement returns a
 * <code>true</code> expression and the other returns a <code>false</code> expression.
 * For example, the if statement in the following code can be replaced by <code>return expression1</code>:
 *      <code>
 *        if (expression1) {
 *            return true
 *        }
 *        return false
 *      </code>
 *
 *   NOTE: This check is disabled by setting checkLastStatementImplicitElse to false.
 *
 * (4) When either the if block or else block of an if statement that is not the last statement in a
 * block contain only a single constant or literal expression 
 * For example, the if statement in the following code has no effect and can be removed:
 *      <code>
 *      def myMethod() {
 *          if (someExpression) { 123 }
 *          doSomething()
 *      }
 *      </code>
 *
 * @author Chris Mair
  */
class UnnecessaryIfStatementRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryIfStatement'
    int priority = 3
    Class astVisitorClass = UnnecessaryIfStatementAstVisitor
    boolean checkLastStatementImplicitElse = true
}

class UnnecessaryIfStatementAstVisitor extends AbstractAstVisitor  {

    @Override
    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement) && hasElseBlock(ifStatement)) {
            if (areReturningTrueAndFalse(ifStatement.ifBlock, ifStatement.elseBlock)) {
                addViolation(ifStatement, 'The if and else blocks merely return true/false')
            }
        }
        super.visitIfElse(ifStatement)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        def allStatements = block.statements
        def allExceptLastStatement = allExceptLastElement(allStatements)
        allExceptLastStatement.each { statement ->
            if (statement instanceof IfStatement) {
                visitIfElseThatIsNotTheLastStatementInABlock(statement)
            }
        }
        if (allStatements && allStatements.last() instanceof IfStatement) {
            visitIfElseThatIsTheLastStatementInABlock(allStatements.last())
        }

        if (allStatements.size() > 1 && rule.checkLastStatementImplicitElse) {
            def nextToLastStatement = allStatements[-2]
            if (nextToLastStatement instanceof IfStatement && hasNoElseBlock(nextToLastStatement)) {
                def lastStatement = allStatements[-1]
                if (areReturningTrueAndFalse(nextToLastStatement.ifBlock, lastStatement)) {
                    addViolation(nextToLastStatement, 'The if block and the subsequent fall-through statement merely return true/false')
                }
            }
        }

        super.visitBlockStatement(block)
    }

    private void visitIfElseThatIsNotTheLastStatementInABlock(IfStatement ifStatement) {
        if (isOnlyAConstantOrLiteralExpression(ifStatement.ifBlock)) {
            addViolation(ifStatement, 'The if block is a constant or literal')
        }
        if (isOnlyAConstantOrLiteralExpression(ifStatement.elseBlock)) {
            addViolation(ifStatement.elseBlock, 'The else block is a constant or literal')
        }
    }

    private void visitIfElseThatIsTheLastStatementInABlock(IfStatement ifStatement) {
        if (isFirstVisit([CHECK_FOR_CONSTANT:ifStatement]) && hasElseBlock(ifStatement)) {
            if (areTrueAndFalseExpressions(ifStatement.ifBlock, ifStatement.elseBlock)) {
                addViolation(ifStatement, 'The if and else blocks implicitly return true/false')
            }
        }
    }

    private boolean areReturningTrueAndFalse(Statement ifBlock, Statement elseBlock) {
        (isReturnTrue(ifBlock) && isReturnFalse(elseBlock)) ||
               (isReturnFalse(ifBlock) && isReturnTrue(elseBlock))
    }

    private boolean isReturnTrue(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        statement instanceof ReturnStatement && AstUtil.isTrue(statement.expression)
    }

    private boolean isReturnFalse(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        statement instanceof ReturnStatement && AstUtil.isFalse(statement.expression)
    }

    private boolean areTrueAndFalseExpressions(Statement ifBlock, Statement elseBlock) {
        (isTrueExpression(ifBlock) && isFalseExpression(elseBlock)) ||
               (isFalseExpression(ifBlock) && isTrueExpression(elseBlock))
    }

    private boolean isTrueExpression(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        statement instanceof ExpressionStatement && AstUtil.isTrue(statement.expression)
    }

    private boolean isFalseExpression(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        statement instanceof ExpressionStatement && AstUtil.isFalse(statement.expression)
    }

    private boolean hasElseBlock(IfStatement ifStatement) {
        !ifStatement.elseBlock.empty
    }

    private boolean hasNoElseBlock(IfStatement ifStatement) {
        ifStatement.elseBlock.empty
    }

    private boolean isOnlyAConstantOrLiteralExpression(Statement blockStatement) {
        def statement = getStatement(blockStatement)
        statement instanceof ExpressionStatement && AstUtil.isConstantOrLiteral(statement.expression)
    }

    private Statement getStatement(Statement statement) {
        isSingleStatementBlock(statement) ? statement.statements.get(0) : statement
    }

    private boolean isSingleStatementBlock(Statement statement) {
        statement instanceof BlockStatement && statement.statements.size() == 1
    }

    private allExceptLastElement(list) {
        def lastElement = list.empty ? null : list.last()
        list - lastElement
    }
}
