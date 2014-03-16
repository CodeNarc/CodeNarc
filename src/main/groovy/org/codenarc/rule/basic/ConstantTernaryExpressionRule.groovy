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

import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for ternary expressions with a constant value for the boolean expression, such as:
 * <ul>
 *   <li><code>true ? x : y</code></li>
 *   <li><code>false ? x : y</code></li>
 *   <li><code>Boolean.TRUE ? x : y</code></li>
 *   <li><code>Boolean.FALSE ? x : y</code></li>
 *   <li><code>null ? x : y</code></li>
 *   <li><code>0 ? x : y</code></li>
 *   <li><code>99.7 ? x : y</code></li>
 *   <li><code>"" ? x : y</code></li>
 *   <li><code>"abc" ? x : y</code></li>
 *   <li><code>[:] ? x : y</code></li>
 *   <li><code>[a:123, b:456] ? x : y</code></li>
 *   <li><code>[a, b, c] ? x : y</code></li>
 * </ul>
 *
 * Also checks for the same types of constant values for the boolean expressions within the "short"
 * ternary expressions, also known as the "Elvis" operator, e.g.:
 * <ul>
 *   <li><code>true ?: y</code></li>
 *   <li><code>null ?: y</code></li>
 *   <li><code>99.7 ?: y</code></li>
 *   <li><code>"abc" ?: y</code></li>
 *   <li><code>[a:123] ?: y</code></li>
 * </ul>
 *
 * @author Chris Mair
 */
class ConstantTernaryExpressionRule extends AbstractAstVisitorRule {
    String name = 'ConstantTernaryExpression'
    int priority = 2
    Class astVisitorClass = ConstantTernaryExpressionAstVisitor
}

class ConstantTernaryExpressionAstVisitor extends AbstractAstVisitor  {

    void visitTernaryExpression(TernaryExpression ternaryExpression) {
        if (isFirstVisit(ternaryExpression)) {
            def booleanExpression = ternaryExpression.booleanExpression
            if (AstUtil.isConstantOrLiteral(booleanExpression.expression)) {
                addViolation(ternaryExpression, "The ternary expression contains the constant $booleanExpression.expression")
            }
        }
        super.visitTernaryExpression(ternaryExpression)
    }

}
