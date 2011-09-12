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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * This rule detects JUnit calling the fail() method without an argument. For better error reporting you should always provide a message. 
 *
 * @author Hamlet D'Arcy
  */
class JUnitFailWithoutMessageRule extends AbstractAstVisitorRule {
    String name = 'JUnitFailWithoutMessage'
    int priority = 2
    Class astVisitorClass = JUnitFailWithoutMessageRuleAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitFailWithoutMessageRuleAstVisitor extends AbstractMethodCallExpressionVisitor {

    @Override 
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCall(call, ['this', 'Assert'], ['fail'], 0)) {
            addViolation call, 'Pass a String parameter to the fail method'
        }
    }
}
