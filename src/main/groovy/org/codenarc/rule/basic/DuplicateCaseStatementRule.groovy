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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks for duplicate case statements in a switch block, such as two
 * equal integers or strings.
 *
 * @author Hamlet D'Arcy
 */
class DuplicateCaseStatementRule extends AbstractAstVisitorRule {
    String name = 'DuplicateCaseStatement'
    int priority = 2
    Class astVisitorClass = DuplicateCaseStatementAstVisitor
}

class DuplicateCaseStatementAstVisitor extends AbstractAstVisitor {

    void visitSwitch(SwitchStatement statement) {
        def allElements = statement?.caseStatements?.findAll { it?.expression instanceof ConstantExpression }
        allElements.inject([]) { list, CaseStatement item  ->
            if (list.contains(item?.expression?.value)) {
                addViolation(item, "Duplicate case statements found in switch. $item?.expression?.value appears twice")
            }
            else {
                list.add item.expression?.value
            }
            list
        }
    super.visitSwitch(statement)
  }

}
