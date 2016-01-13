/*
 * Copyright 2015 the original author or authors.
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

import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.SourceCodeUtil
/**
 * Check whether list and map literals contain optional trailing comma.
 * <p>
 * By default, trailing comma is required.
 * Set the <code>checkList</code> and <code>checkMap</code> properties to false if needed.
 *
 * @author Yuriy Chulovskyy
 */
class TrailingCommaRule extends AbstractAstVisitorRule {

    String name = 'TrailingComma'
    int priority = 3
    Class astVisitorClass = TrailingCommaAstVisitor
    boolean checkList = true
    boolean checkMap = true
}

class TrailingCommaAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMapExpression(MapExpression expression) {
        if (isOneLiner(expression) || expression.mapEntryExpressions.isEmpty()) {
            return
        }
        if (rule.checkMap && !hasTrailingComma(expression.mapEntryExpressions[-1], expression)) {
            addViolation(expression, 'Map should contain trailing comma.')
        }
    }

    @Override
    void visitListExpression(ListExpression expression) {
        if (isOneLiner(expression) || expression.expressions.isEmpty()) {
            return
        }
        if (rule.checkList && !hasTrailingComma(expression.expressions[-1], expression)) {
            addViolation(expression, 'List should contain trailing comma.')
        }
    }

    private static boolean isOneLiner(Expression expression) {
        expression.lineNumber == expression.lastLineNumber
    }

    private boolean hasTrailingComma(Expression lastExpression, Expression outerExpression) {
        List<String> sourceLinesBetween = SourceCodeUtil.sourceLinesBetween(sourceCode,
                lastExpression.lastLineNumber,
                lastExpression.lastColumnNumber,
                outerExpression.lastLineNumber,
                outerExpression.lastColumnNumber
        )
        sourceLinesBetween.any { it.contains(',') }
    }
}
