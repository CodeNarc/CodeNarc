/*
 * Copyright 2009 the original author or authors.
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

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * In an "if" expression with an "else" clause, avoid negation in the test. For example, rephrase:
 *     if (x != y) diff(); else same();
 * as:
 *     if (x == y) same(); else diff();
 * Most "if (x != y)" cases without an "else" are often return cases, so consistent use of this rule makes the code
 * easier to read. Also, this resolves trivial ordering problems, such as "does the error case go first?" or "does
 * the common case go first?".
 *
 * @author Hamlet D'Arcy
 */
class ConfusingTernaryRule extends AbstractAstVisitorRule {

    String name = 'ConfusingTernary'
    int priority = 3
    Class astVisitorClass = ConfusingTernaryAstVisitor
}

class ConfusingTernaryAstVisitor extends AbstractAstVisitor {
    @Override
    void visitTernaryExpression(TernaryExpression expression) {

        if (expression.booleanExpression.expression instanceof BinaryExpression) {
            addViolationForBinaryExpression(expression.booleanExpression.expression, expression)
        } else if (expression.booleanExpression.expression instanceof NotExpression) {
            addViolationForNotExpression(expression, expression.booleanExpression.expression)
        }
        super.visitTernaryExpression(expression)
    }

    private addViolationForNotExpression(TernaryExpression expression, NotExpression exp) {
        addViolation(expression, "(!$exp.text) is a confusing negation in a ternary expression. Rewrite as ($exp.expression.text) and invert the conditions.")
    }

    private addViolationForBinaryExpression(BinaryExpression exp, TernaryExpression expression) {
        if (exp.operation.text == '!=') {
            if (AstUtil.isNull(exp.leftExpression) || AstUtil.isNull(exp.rightExpression))  {
                return
            }
            if (AstUtil.isBoolean(exp.leftExpression) || AstUtil.isBoolean(exp.rightExpression))  {
                return
            }
            def suggestion = "($exp.leftExpression.text == $exp.rightExpression.text)"
            addViolation(expression, "$exp.text is a confusing negation in a ternary expression. Rewrite as $suggestion and invert the conditions.")
        }
    }

}
