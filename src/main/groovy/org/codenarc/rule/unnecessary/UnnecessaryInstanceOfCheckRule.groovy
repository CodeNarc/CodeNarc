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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codenarc.util.GroovyVersion

/**
 * This rule finds instanceof checks that cannot possibly evaluate to true. For instance, checking that (!variable instanceof String) will never be true because the result of a not expression is always a boolean.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class UnnecessaryInstanceOfCheckRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryInstanceOfCheck'
    int priority = 3
    Class astVisitorClass = UnnecessaryInstanceOfCheckAstVisitor
}

class UnnecessaryInstanceOfCheckAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression)) {
            if (AstUtil.isBinaryExpressionType(expression, 'instanceof') && expression.leftExpression instanceof NotExpression) {
                String expressionText = GroovyVersion.isGroovyVersion4() ? "!($expression.leftExpression.text)" : expression.leftExpression.text
                if (expression.rightExpression.text == 'Boolean') {
                    addViolation(expression, "The result of '${expressionText}' will always be a Boolean")
                } else {
                    addViolation(expression, "The result of '${expressionText}' will never be a $expression.rightExpression.text")
                }
            }
            super.visitBinaryExpression(expression)
        }
    }
}
