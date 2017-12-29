/*
 * Copyright 2017 the original author or authors.
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
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.syntax.Types
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * An inverted condition is one where a constant expression is used on the left hand side of the equals comparision.
 * Such conditions can be confusing especially when used in assertions where the expected value is by convention placed
 * on the right hand side of the comparision.
 *
 * @author Marcin Erdmann
 */
class InvertedConditionRule extends AbstractAstVisitorRule {

    String name = 'InvertedCondition'
    int priority = 3
    Class astVisitorClass = InvertedConditionAstVisitor

}

class InvertedConditionAstVisitor extends AbstractAstVisitor {

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.operation.type == Types.COMPARE_EQUAL && expression.leftExpression instanceof ConstantExpression) {
            addViolation(expression, "${expression.leftExpression.text} is a constant expression on the left side of a compare equals operation")
        }
        super.visitBinaryExpression(expression)
    }
}
