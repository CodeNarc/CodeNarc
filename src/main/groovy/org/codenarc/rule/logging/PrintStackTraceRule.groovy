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
package org.codenarc.rule.logging

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * Rule that checks for calls to <code>printStackTrace()</code>.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class PrintStackTraceRule extends AbstractAstVisitorRule {
    String name = 'PrintStackTrace'
    int priority = 2
    Class astVisitorClass = PrintStackTraceAstVisitor
}

class PrintStackTraceAstVisitor extends AbstractMethodCallExpressionVisitor {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (AstUtil.isMethodNamed(methodCall, 'printStackTrace', 0)) {
            addViolation(methodCall, 'printStackTrace() should be replaced with something more robust')
        }

        if (AstUtil.isMethodNamed(methodCall, 'printSanitizedStackTrace', 1)) {
            addViolation(methodCall, 'printSanitizedStackTrace() should be replaced with something more robust')
        }
    }
}
