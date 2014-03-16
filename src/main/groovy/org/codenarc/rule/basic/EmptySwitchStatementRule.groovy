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

import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for empty switch statements
 *
 * @author Chris Mair
 */
class EmptySwitchStatementRule extends AbstractAstVisitorRule {
    String name = 'EmptySwitchStatement'
    int priority = 2
    Class astVisitorClass = EmptySwitchStatementAstVisitor
}

class EmptySwitchStatementAstVisitor extends AbstractAstVisitor  {
    void visitSwitch(SwitchStatement switchStatement) {
        if (isFirstVisit(switchStatement) && switchStatement.caseStatements.empty) {
            addViolation(switchStatement, 'The switch statement is empty')
        }
        super.visitSwitch(switchStatement)
    }

}
