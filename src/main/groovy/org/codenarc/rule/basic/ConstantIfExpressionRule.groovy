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

import org.codehaus.groovy.ast.stmt.IfStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for if statement with a constant value for the if expression, such as:
 * <ul>
 *   <li><code>if (true) { .. }</code></li>
 *   <li><code>if (false) { .. }</code></li>
 *   <li><code>if (Boolean.TRUE) { .. }</code></li>
 *   <li><code>if (Boolean.FALSE) { .. }</code></li>
 *   <li><code>if (null) { .. }</code></li>
 *   <li><code>if (0) { .. }</code></li>
 *   <li><code>if (99.7) { .. }</code></li>
 *   <li><code>if ("") { .. }</code></li>
 *   <li><code>if ("abc") { .. }</code></li>
 *   <li><code>if ([a:123, b:456]) { .. }</code></li>
 *   <li><code>if ([a, b]) { .. }</code></li>
 * </ul>
 *
 * @author Chris Mair
 */
class ConstantIfExpressionRule extends AbstractAstVisitorRule {
    String name = 'ConstantIfExpression'
    int priority = 2
    Class astVisitorClass = ConstantIfExpressionAstVisitor
}

class ConstantIfExpressionAstVisitor extends AbstractAstVisitor  {

    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement)) {
            def booleanExpression = ifStatement.booleanExpression
            if (AstUtil.isConstantOrLiteral(booleanExpression.expression)) {
                addViolation(ifStatement, "The if statement condition ($booleanExpression.text) contains a constant")
            }
        }
        super.visitIfElse(ifStatement)
    }

}
