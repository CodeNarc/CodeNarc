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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * In unit tests, if a condition is expected to be false then there is no sense using assertTrue with the negation operator. For instance, assertTrue(!condition) can always be simplified to assertFalse(condition)
 *
 * @author Hamlet D'Arcy
  */
class UseAssertFalseInsteadOfNegationRule extends AbstractAstVisitorRule {
    String name = 'UseAssertFalseInsteadOfNegation'
    int priority = 2
    Class astVisitorClass = UseAssertFalseInsteadOfNegationAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class UseAssertFalseInsteadOfNegationAstVisitor extends AbstractMethodCallExpressionVisitor {

    @SuppressWarnings('DuplicateLiteral')
    void visitMethodCallExpression(MethodCallExpression call) {

        List args = AstUtil.getMethodArguments(call)
        if (AstUtil.isMethodCall(call, ['this', 'Assert'], ['assertTrue'])) {

            if (args.size() < 3 && args.size() > 0) {
                def arg = args.last()
                if (arg instanceof NotExpression) {
                    def left = call.objectExpression.text == 'this' ? '' : call.objectExpression.text + '.'
                    def right = arg.expression.text
                    addViolation call, "${left}assertTrue(!${right}) can be simplified to ${left}assertFalse($right)"
                }
            }
        }
    }
}
