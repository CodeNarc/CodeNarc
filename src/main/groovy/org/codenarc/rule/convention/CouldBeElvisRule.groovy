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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Catch an if block that could be written as an elvis expression.
 *
 * @author GUM
 * @author Chris Mair
 */
class CouldBeElvisRule extends AbstractAstVisitorRule {

    String name = 'CouldBeElvis'
    int priority = 3
    Class astVisitorClass = CouldBeElvisAstVisitor
}

class CouldBeElvisAstVisitor extends AbstractAstVisitor {
    @Override
    void visitIfElse(IfStatement node) {
        addViolationCouldBeElvis(node)
        super.visitIfElse(node)
    }

    private addViolationCouldBeElvis(IfStatement node) {
        def conditionalCheck = node.booleanExpression?.expression

        if (conditionalCheck instanceof NotExpression) {
            def bodyOfIfStatement = getSingleStatementExpressionOrNull(node.ifBlock)

            if (AstUtil.isBinaryExpressionType(bodyOfIfStatement, '=')) {
                def conditionalText = conditionalCheck.expression.text
                def leftAssignText = bodyOfIfStatement.leftExpression.text

                // space check is a safe guard against crazy ass AST getText() implementations
                if (conditionalText == leftAssignText && !conditionalText.contains(' ')) {
                    def rightText = AstUtil.createPrettyExpression(bodyOfIfStatement.rightExpression)
                    def rewrite = "$leftAssignText = $leftAssignText ?: $rightText "
                    addViolation(node, "Code could use elvis operator: $rewrite")
                }
            }
        }
    }

    private getSingleStatementExpressionOrNull(ifBlock) {
        if (AstUtil.isOneLiner(ifBlock) && AstUtil.respondsTo(ifBlock.statements[0], 'expression') ) {
            return ifBlock.statements[0].expression
        }
        if (ifBlock instanceof ExpressionStatement) {
            return ifBlock.expression
        }
        return null
    }

}
