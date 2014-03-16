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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.NotExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * There is no point in using a double negative, it is always positive. For instance !!x can always be simplified to x. And !(!x) can as well. 
 *
 * @author Hamlet D'Arcy
 */
class DoubleNegativeRule extends AbstractAstVisitorRule {
    String name = 'DoubleNegative'
    int priority = 2
    Class astVisitorClass = DoubleNegativeAstVisitor
}

class DoubleNegativeAstVisitor extends AbstractAstVisitor {

    void visitNotExpression(NotExpression expression) {

        if (expression.expression instanceof NotExpression) {
            addViolation expression, "The expression (!!$expression.text) is a confusing double negative"
        }
        super.visitNotExpression expression
    }

}
