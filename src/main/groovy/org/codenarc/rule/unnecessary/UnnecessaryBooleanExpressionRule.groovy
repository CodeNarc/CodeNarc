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

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks unnecessary boolean expressions, including ANDing (&&) or ORing (||) with
 * <code>true</code>, <code>false</code>, <code>null</code>, or a Map/List/String/Number literal.
 *
 * This rule also checks for negation (!) of <code>true</code>, <code>false</code>,
 * <code>null</code>, or a Map/List/String/Number literal.
 * Examples include:
 * <ul>
 *   <li><code>def result = value && true</code></li>
 *   <li><code>if (false || value) { .. }</code></li>
 *   <li><code>return value && Boolean.FALSE</code></li>
 *   <li><code>result = value && "abc"</code></li>
 *   <li><code>result = null && value</code></li>
 *   <li><code>result = value && 123</code></li>
 *   <li><code>result = 678.123 || true</code></li>
 *   <li><code>result = value && [x, y]</code></li>
 *   <li><code>def result = [a:123] && value</code></li>
 *
 *   <li><code>result = !true</code></li>
 *   <li><code>result = !false</code></li>
 *   <li><code>result = !Boolean.TRUE</code></li>
 *   <li><code>result = !null</code></li>
 *   <li><code>result = !"abc"</code></li>
 *   <li><code>result = ![a:123]</code></li>
 *   <li><code>result = ![a,b]</code></li>
 * </ul>
 *
 * @author Chris Mair
  */
class UnnecessaryBooleanExpressionRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryBooleanExpression'
    int priority = 3
    Class astVisitorClass = UnnecessaryBooleanExpressionAstVisitor
}

class UnnecessaryBooleanExpressionAstVisitor extends AbstractAstVisitor  {

    private static final BOOLEAN_LOGIC_OPERATIONS = ['&&', '||']

    void visitBinaryExpression(BinaryExpression expression) {
        def operationName = expression.operation.text
        if (operationName in BOOLEAN_LOGIC_OPERATIONS &&
                (AstUtil.isConstantOrLiteral(expression.rightExpression) ||
                 AstUtil.isConstantOrLiteral(expression.leftExpression))) {
            addViolation(expression, "The expression using $operationName is compared to a constant")
        }
        super.visitBinaryExpression(expression)
    }

    void visitNotExpression(NotExpression expression) {
        if (AstUtil.isConstantOrLiteral(expression.expression)) {
            addViolation(expression, 'The Not expression contains a literal or contant. ')
        }
         super.visitNotExpression(expression)
    }

}
