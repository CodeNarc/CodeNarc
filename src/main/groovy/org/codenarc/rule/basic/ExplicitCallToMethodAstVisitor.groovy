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

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

/**
 * Parent Visitor for "ExplicitCallToX" Rules.
 *
 * @author Hamlet D'Arcy
 */
abstract class ExplicitCallToMethodAstVisitor extends AbstractAstVisitor  {

    final String methodName

    /**
     * The method name to watch for. 
     * @param methodName
     * @return
     */
    def ExplicitCallToMethodAstVisitor(String methodName) {
        this.methodName = methodName
    }

    def void visitMethodCallExpression(MethodCallExpression call) {
        if (!AstUtil.isSafe(call) && !AstUtil.isSpreadSafe(call) && AstUtil.isMethodNamed(call, methodName, 1)) {
            if (!rule.ignoreThisReference || !AstUtil.isMethodCallOnObject(call, 'this')) {
                if (!AstUtil.isSafe(call.objectExpression)) {
                    safelyAddViolation(call)
                }
            }
        }
        super.visitMethodCallExpression call
    }

    @SuppressWarnings('CatchThrowable')
    private def safelyAddViolation(MethodCallExpression call) {
        try {
            addViolation call, getViolationMessage(call)
        } catch (Throwable t) {
            addViolation call, "Explicit call to $methodName can be simplified using Groovy operator overloading."
        }
    }

    abstract String getViolationMessage(MethodCallExpression exp)
}
