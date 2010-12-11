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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for JUnit <code>assert()</code> method calls with constant arguments
 * such that the assertion always fails. This includes:
 * <ul>
 *   <li><code>assertTrue(false)</code>.</li>
 *   <li><code>assertFalse(true)</code>.</li>
 *   <li><code>assertNull(CONSTANT)</code>.</li>
 * </ul>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class JUnitAssertAlwaysFailsRule extends AbstractAstVisitorRule {
    String name = 'JUnitAssertAlwaysFails'
    int priority = 2
    Class astVisitorClass = JUnitAssertAlwaysFailsAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitAssertAlwaysFailsAstVisitor extends AbstractAstVisitor  {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (isFirstVisit(methodCall)) {
            def isMatch =
                JUnitUtil.isAssertConstantValueCall(methodCall, 'assertTrue', Boolean.FALSE) ||
                JUnitUtil.isAssertConstantValueCall(methodCall, 'assertFalse', Boolean.TRUE) ||
                isAssertConstantValueNotNullCall(methodCall, 'assertNull')
            if (isMatch) {
                addViolation(methodCall)
            }
        }
        super.visitMethodCallExpression(methodCall)
    }

    private boolean isAssertConstantValueNotNullCall(MethodCallExpression methodCall, String methodName) {
        def isMatch = false
        if (AstUtil.isMethodCall(methodCall, 'this', methodName)) {
            def args = methodCall.arguments.expressions
            def valueExpression = args.last()
            isMatch = args.size() in 1..2 &&
                valueExpression instanceof ConstantExpression && valueExpression.value != null
        }
        isMatch
    }

}