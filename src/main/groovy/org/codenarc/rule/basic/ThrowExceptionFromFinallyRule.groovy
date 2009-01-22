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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for throwing an exception from within a finally block
 *
 * @author Chris Mair
 * @version $Revision: 204 $ - $Date: 2009-01-16 21:45:13 -0500 (Fri, 16 Jan 2009) $
 */
class ThrowExceptionFromFinallyBlockRule extends AbstractAstVisitorRule {
    String id = 'ThrowExceptionFromFinallyBlock'
    int priority = 2
    Class astVisitorClass = ThrowExceptionFromFinallyBlockAstVisitor
}

class ThrowExceptionFromFinallyBlockAstVisitor extends AbstractFinallyAstVisitor  {
    void visitThrowStatement(ThrowStatement throwStatement) {
        if (isStatementWithinFinally(throwStatement)) {
            addViolation(throwStatement)
        }
        super.visitThrowStatement(throwStatement)
    }

}