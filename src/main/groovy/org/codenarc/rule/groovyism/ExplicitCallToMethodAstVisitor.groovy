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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * Parent Visitor for "ExplicitCallToX" Rules.
 *
 * @author Hamlet D'Arcy
 * @author René Scheibe
 */
abstract class ExplicitCallToMethodAstVisitor extends AbstractMethodCallExpressionVisitor  {

    final String methodName
    final int numArgs

    /**
     * @param methodName The method name to watch for.
     * @param numArgs The expected number of method parameters
     */
    protected ExplicitCallToMethodAstVisitor(String methodName, int numArgs) {
        this.methodName = methodName
        this.numArgs = numArgs
    }

    /**
     * @param methodName The method name to watch for.
     */
    protected ExplicitCallToMethodAstVisitor(String methodName) {
        this(methodName, 1)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (!AstUtil.isSafe(call) && !AstUtil.isSpreadSafe(call) && AstUtil.isMethodNamed(call, methodName, numArgs)) {
            boolean isAllowedCallOnThis = rule.ignoreThisReference && AstUtil.isMethodCallOnObject(call, 'this')
            boolean isAllowedCallOnSuper = AstUtil.isMethodCallOnObject(call, 'super')

            if (!isAllowedCallOnThis && !isAllowedCallOnSuper) {
                if (!AstUtil.isSafe(call.objectExpression)) {
                    safelyAddViolation(call)
                }
            }
        }
    }

    // Allow subclasses to insert additional checks
    protected boolean shouldIgnoreViolationForMethodCall(MethodCallExpression ignore) {
        return false
    }

    @SuppressWarnings('CatchThrowable')
    private void safelyAddViolation(MethodCallExpression call) {
        if (shouldIgnoreViolationForMethodCall(call)) {
            return
        }
        try {
            addViolation call, getViolationMessage(call)
        } catch (Throwable t) {
            addViolation call, "Explicit call to $methodName can be simplified using Groovy operator overloading."
        }
    }

    abstract protected String getViolationMessage(MethodCallExpression exp)
}
