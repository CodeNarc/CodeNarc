/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for assert statements where the assert condition expressions is a constant value or literal value, such as:
 * <ul>
 *   <li><code>assert true</code></li>
 *   <li><code>assert false</code></li>
 *   <li><code>assert Boolean.TRUE</code></li>
 *   <li><code>assert Boolean.FALSE</code></li>
 *   <li><code>assert null</code></li>
 *   <li><code>assert 0</code></li>
 *   <li><code>assert 99.7</code></li>
 *   <li><code>assert ""</code></li>
 *   <li><code>assert "abc"</code></li>
 *   <li><code>assert [:]</code></li>
 *   <li><code>assert [a:123, b:456]</code></li>
 *   <li><code>assert [a, b, c]</code></li>
 * </ul>
 *
 * @author Chris Mair
 */
class ConstantAssertExpressionRule extends AbstractAstVisitorRule {

    String name = 'ConstantAssertExpression'
    int priority = 3
    Class astVisitorClass = ConstantAssertExpressionAstVisitor
}

class ConstantAssertExpressionAstVisitor extends AbstractAstVisitor  {

    @Override
    void visitAssertStatement(AssertStatement statement) {
        if (isFirstVisit(statement)) {
            def booleanExpression = statement.booleanExpression
            if (AstUtil.isConstantOrLiteral(booleanExpression.expression)) {
                addViolation(statement, "The assert statement within class $currentClassName has a constant boolean expression [$booleanExpression.text]")
            }
        }
        super.visitAssertStatement(statement)
    }

}
