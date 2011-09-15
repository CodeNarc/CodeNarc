/*
 * Copyright 2011 the original author or authors.
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
 * A test method that invokes another test method is a chained test; the methods are dependent on one another. Tests should be atomic, and not be dependent on one another. 
 *
 * @author Hamlet D'Arcy
  */
class ChainedTestRule extends AbstractAstVisitorRule {
    String name = 'ChainedTest'
    int priority = 2
    Class astVisitorClass = ChainedTestAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class ChainedTestAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodCall(call, 'this', 'test.*', 0)) {
            addViolation(call, "The test method $call.methodAsString() is being invoked explicitly from within a unit test. Tests should be isolated and not dependent on one another")
        }
    }
}
