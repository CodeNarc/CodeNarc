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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for ternary expressions with a constant value for the boolean expression, such as:
 * <ul>
 *   <li><code>true ? x : y</code></ul>
 *   <li><code>false ? x : y</code></ul>
 *   <li><code>Boolean.TRUE ? x : y</code></ul>
 *   <li><code>Boolean.FALSE ? x : y</code></ul>
 *   <li><code>null ? x : y</code></ul>
 *   <li><code>0 ? x : y</code></ul>
 *   <li><code>99.7 ? x : y</code></ul>
 *   <li><code>"" ? x : y</code></ul>
 *   <li><code>"abc" ? x : y</code></ul>
 * </ul>
 *
 * Also checks for the same types of constant values for the boolean expressions within the "short"
 * ternary expressions, also known as the "Elvis" operator, e.g.:
 * <ul>
 *   <li><code>true ?: y</code></ul>
 *   <li><code>null ?: y</code></ul>
 *   <li><code>99.7 ?: y</code></ul>
 *   <li><code>"abc" ?: y</code></ul>
 * </ul>
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ConstantTernaryExpressionRule extends AbstractAstVisitorRule {
    String name = 'ConstantTernaryExpression'
    int priority = 2
    Class astVisitorClass = ConstantTernaryExpressionAstVisitor
}

class ConstantTernaryExpressionAstVisitor extends AbstractAstVisitor  {
    private static final BOOLEAN_CLASS = Boolean.name
    private static final CONSTANTS = ['Boolean.TRUE', 'Boolean.FALSE', 'null']

    void visitTernaryExpression(TernaryExpression ternaryExpression) {
        if (isFirstVisit(ternaryExpression)) {
            def booleanExpression = ternaryExpression.booleanExpression
            def expression = booleanExpression.expression
            def type = expression.type
            if (type.name == BOOLEAN_CLASS || expression instanceof ConstantExpression || booleanExpression.text in CONSTANTS) {
                addViolation(ternaryExpression)
            }
        }
        super.visitTernaryExpression(ternaryExpression)
    }

}