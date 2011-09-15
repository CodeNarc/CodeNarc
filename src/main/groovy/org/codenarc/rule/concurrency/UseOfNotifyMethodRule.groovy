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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * This code calls notify() rather than notifyAll(). Java monitors are often used for multiple conditions. Calling
 * notify() only wakes up one thread, meaning that the thread woken up might not be the one waiting for the condition
 * that the caller just satisfied.
 *
 * @author Hamlet D'Arcy
 */
class UseOfNotifyMethodRule extends AbstractAstVisitorRule {
    String name = 'UseOfNotifyMethod'
    int priority = 2
    Class astVisitorClass = UseOfNotifyMethodAstVisitor
}

class UseOfNotifyMethodAstVisitor extends AbstractMethodCallExpressionVisitor {

    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodNamed(call, 'notify', 0)) {
            addViolation call, "The method $call.text should be replaced with ${call.objectExpression.text}.notifyAll()"
        }
    }
}
