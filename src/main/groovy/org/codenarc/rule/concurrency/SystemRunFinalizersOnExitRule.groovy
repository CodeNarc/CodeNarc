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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * Method calls to System.runFinalizersOnExit() should not be allowed. This method is inherently
 * non-thread-safe, may result in data corruption, deadlock, and may effect parts of the program
 * far removed from it's call point. It is deprecated, and it's use strongly discouraged.
 *
 * @author Hamlet D'Arcy
 */
class SystemRunFinalizersOnExitRule extends AbstractAstVisitorRule {

    String name = 'SystemRunFinalizersOnExit'
    int priority = 2
    Class astVisitorClass = SystemRunFinalizersOnExitAstVisitor
}

class SystemRunFinalizersOnExitAstVisitor extends AbstractMethodCallExpressionVisitor {

    void visitMethodCallExpression(MethodCallExpression call) {
        if (call.objectExpression instanceof VariableExpression) {
            if (AstUtil.isMethodCall(call, 'System', 'runFinalizersOnExit', 1)) {
                 addViolation(call, 'System.runFinalizersOnExit() should not be invoked')
            }
        }
    }
}
