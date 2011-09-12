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
 * This rule detects JUnit assertions in object equality. These assertions should be made by more specific
 * methods, like assertEquals.
 *
 * @author Per Junel
 * @author Hamlet D'Arcy
  */
class UseAssertEqualsInsteadOfAssertTrueRule extends AbstractAstVisitorRule {
    String name = 'UseAssertEqualsInsteadOfAssertTrue'
    int priority = 3
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
    Class astVisitorClass = UseAssertEqualsInsteadOfAssertTrueAstVisitor
}

class UseAssertEqualsInsteadOfAssertTrueAstVisitor extends AbstractMethodCallExpressionVisitor {

    @SuppressWarnings('DuplicateLiteral')
    void visitMethodCallExpression(MethodCallExpression call) {

        List args = AstUtil.getMethodArguments(call)
        if (args.size() < 3 && args.size() > 0) {
            def arg = args.last()
            if (AstUtil.isMethodCall(call, ['this', 'Assert'], ['assertTrue']) &&
                    AstUtil.isBinaryExpressionType(arg, '==')) {
                addViolation call, "Replace $call.methodAsString with a call to assertEquals()"
            }
            if (AstUtil.isMethodCall(call, ['this', 'Assert'], ['assertFalse']) &&
                    AstUtil.isBinaryExpressionType(arg, '!=') && call.methodAsString == 'assertFalse') {
                addViolation call, "Replace $call.methodAsString with a call to assertEquals()"
            }
        }
    }
}
