/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for assert statements within a finally block.
 *
 * @author Chris Mair
 */
class AssertWithinFinallyBlockRule extends AbstractAstVisitorRule {

    String name = 'AssertWithinFinallyBlock'
    int priority = 2
    Class astVisitorClass = AssertWithinFinallyBlockAstVisitor
}

class AssertWithinFinallyBlockAstVisitor extends AbstractFinallyAstVisitor  {

    @Override
    void visitAssertStatement(AssertStatement statement) {
        if (isFirstVisit(statement) && isStatementWithinFinally(statement)) {
            addViolation(statement, "A finally block within class $currentClassName contains an assert statement, potentially hiding the original exception, if there is one")
        }
        super.visitAssertStatement(statement)
    }

}
