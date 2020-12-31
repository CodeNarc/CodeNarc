/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codehaus.groovy.ast.expr.NotExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Check that there are no whitespace characters directly after the not (!) operator.
 */
class SpaceAfterNotOperatorRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterNotOperator'
    int priority = 3
    Class astVisitorClass = SpaceAfterNotOperatorRuleAstVisitor
}

class SpaceAfterNotOperatorRuleAstVisitor extends AbstractAstVisitor {

    @Override
    void visitNotExpression(NotExpression expression) {
        if (isFirstVisit(expression) && isFollowedByWhitespace(expression)) {
            addViolation(expression, 'There is whitespace after the not operator.')
        }
        super.visitNotExpression(expression)
    }

    private boolean isFollowedByWhitespace(NotExpression expression) {
        sourceLine(expression)[expression.columnNumber].trim().empty
    }
}
