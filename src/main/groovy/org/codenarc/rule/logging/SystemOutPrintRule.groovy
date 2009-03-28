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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression

/**
 * Rule that checks for calls to <code>System.out.print()</code>, <code>System.out.println()</code>
 * or <code>System.out.printf()</code>.
 *
 * @author Chris Mair
 * @version $Revision: 98 $ - $Date: 2009-03-26 22:00:27 -0400 (Thu, 26 Mar 2009) $
 */
class SystemOutPrintRule extends AbstractAstVisitorRule {
    String name = 'SystemOutPrint'
    int priority = 2
    Class astVisitorClass = SystemOutPrintAstVisitor
}

class SystemOutPrintAstVisitor extends AbstractAstVisitor  {

    void visitMethodCallExpression(MethodCallExpression methodCall) {
        if (methodCall.text.startsWith('System.out.print')) {
            addViolation(methodCall)
        }
        super.visitMethodCallExpression(methodCall)
    }
}