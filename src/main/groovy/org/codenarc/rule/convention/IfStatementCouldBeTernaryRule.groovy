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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.ASTNode
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.GStringExpression

/**
 * Checks for:
 *
 * (1) An if statements where both the if and else blocks contain only a single return statement with a constant or
 * literal value.
 *
 * (2) When the second-to-last statement in a block is an if statement with no else, where the block contains a single
 * return statement, and the last statement in the block is a return statement, and both return statements return a
 * constant or literal value. This checks can be disabled by setting checkLastStatementImplicitElse to false.
 *
 * @author Chris Mair
 */
class IfStatementCouldBeTernaryRule extends AbstractAstVisitorRule {

    String name = 'IfStatementCouldBeTernary'
    int priority = 2
    Class astVisitorClass = IfStatementCouldBeTernaryAstVisitor
    boolean checkLastStatementImplicitElse = true
}

class IfStatementCouldBeTernaryAstVisitor extends AbstractAstVisitor {

    @Override
    void visitIfElse(IfStatement ifElse) {
        if (isFirstVisit(ifElse) && isOnlyReturnStatement(ifElse.ifBlock) && isOnlyReturnStatement(ifElse.elseBlock)) {
            def ternaryExpression = "return ${ifElse.booleanExpression.text} ? ${getReturnValue(ifElse.ifBlock)} : ${getReturnValue(ifElse.elseBlock)}"
            addViolation(ifElse, "The if statement in class $currentClassName can be rewritten using the ternary operator: $ternaryExpression")
        }
        super.visitIfElse(ifElse)
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        def allStatements = block.statements
        if (allStatements.size() > 1 && rule.checkLastStatementImplicitElse) {
            def nextToLastStatement = allStatements[-2]
            if (nextToLastStatement instanceof IfStatement && hasNoElseBlock(nextToLastStatement)) {
                def lastStatement = allStatements[-1]
                IfStatement ifStatement = nextToLastStatement

                if (isOnlyReturnStatement(ifStatement.ifBlock) && isReturnStatementWithConstantOrLiteralValue(lastStatement)) {
                    def elseReturnExpression = AstUtil.createPrettyExpression(lastStatement.expression)
                    def ternaryExpression = "return ${ifStatement.booleanExpression.text} ? ${getReturnValue(ifStatement.ifBlock)} : ${elseReturnExpression}"
                    addViolation(ifStatement, "The if statement in class $currentClassName can be rewritten using the ternary operator: $ternaryExpression")
                }
            }
        }

        super.visitBlockStatement(block)
    }

    private boolean hasNoElseBlock(IfStatement ifStatement) {
        ifStatement.elseBlock.empty
    }

    private boolean isOnlyReturnStatement(ASTNode node) {
        isBlockWithSingleReturnStatement(node) || isReturnStatementWithConstantOrLiteralValue(node)
    }

    private boolean isBlockWithSingleReturnStatement(ASTNode node) {
        return node instanceof BlockStatement &&
            node.statements.size() == 1 &&
            isReturnStatementWithConstantOrLiteralValue(node.statements[0])
    }

    private boolean isReturnStatementWithConstantOrLiteralValue(ASTNode node) {
        return node instanceof ReturnStatement &&
            (AstUtil.isConstantOrLiteral(node.expression) || node.expression instanceof GStringExpression)
    }

    private String getReturnValue(ASTNode node) {
        def expr = node instanceof BlockStatement ? node.statements[0].expression : node.expression
        return AstUtil.createPrettyExpression(expr)
    }

}
