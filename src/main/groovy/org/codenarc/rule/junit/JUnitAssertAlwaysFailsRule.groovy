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

/**
 * Rule that checks for JUnit <code>assert()</code> method calls with constant arguments
 * such that the assertion always fails. This includes:
 * <ul>
 *   <li><code>assertTrue(false)</code>.</li>
 *   <li><code>assertTrue(0)</code>.</li>
 *   <li><code>assertTrue('')</code>.</li>
 *   <li><code>assertTrue([123])</code>.</li>
 *   <li><code>assertTrue([a:123])</code>.</li>
 *   <li><code>assertFalse(true)</code>.</li>
 *   <li><code>assertFalse(99)</code>.</li>
 *   <li><code>assertFalse([123])</code>.</li>
 *   <li><code>assertFalse([a:123])</code>.</li>
 *   <li><code>assertNull(CONSTANT)</code>.</li>
 *   <li><code>assertNull([])</code>.</li>
 *   <li><code>assertNull([123])</code>.</li>
 *   <li><code>assertNull([a:123])</code>.</li>
 *   <li><code>assertNull([:])</code>.</li>
 * </ul>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 */
class JUnitAssertAlwaysFailsRule extends AbstractAstVisitorRule {
    String name = 'JUnitAssertAlwaysFails'
    int priority = 2
    Class astVisitorClass = JUnitAssertAlwaysFailsAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitAssertAlwaysFailsAstVisitor extends AbstractMethodCallExpressionVisitor {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        def isMatch =
            JUnitUtil.isAssertCallWithLiteralValue(methodCall, 'assertTrue', false) ||
            JUnitUtil.isAssertCallWithLiteralValue(methodCall, 'assertFalse', true) ||
            JUnitUtil.isAssertCallWithNonNullConstantValue(methodCall, 'assertNull') ||
            JUnitUtil.isAssertCallWithLiteralValue(methodCall, 'assertNull', true) ||
            JUnitUtil.isAssertCallWithLiteralValue(methodCall, 'assertNull', false) ||
            JUnitUtil.isAssertCallWithConstantValue(methodCall, 'assertNotNull', null)
        if (isMatch) {
            addViolation(methodCall, "The assertion $methodCall.text will always fail. Replace with a call to the fail(String) method")
        }
    }

}
