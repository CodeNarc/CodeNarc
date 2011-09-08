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

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.Violation

/**
 * Rule to detect nested synchronization blocks.
 *
 * @author Hamlet D'Arcy
 */
class NestedSynchronizationRule extends AbstractAstVisitorRule {

    String name = 'NestedSynchronization'
    int priority = 2
    Class astVisitorClass = NestedSynchronizationAstVisitor
}

class NestedSynchronizationAstVisitor extends AbstractAstVisitor  {

    private int visitCount = 0

    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (isFirstVisit(statement)) {
            if (visitCount > 0) {
                addViolation(statement, 'Nested synchronized statements are confusing and may lead to deadlock')
            }
            visitCount++
            super.visitSynchronizedStatement(statement)
            visitCount--
        } else {
            super.visitSynchronizedStatement(statement)
        }
    }

    void visitClosureExpression(ClosureExpression expression) {

        if (isFirstVisit(expression)) {
            // dispatch to a new instance b/c we have a new scope
            AbstractAstVisitor newVisitor = new NestedSynchronizationAstVisitor(sourceCode: this.sourceCode, rule: this.rule, visited: this.visited)
            expression.getCode().visit(newVisitor)
            newVisitor.getViolations().each { Violation it ->
                addViolation(it)
            }
        } else {
            super.visitClosureExpression(expression)
        }
    }
}
