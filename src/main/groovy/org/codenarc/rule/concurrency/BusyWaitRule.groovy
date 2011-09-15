/*
 * Copyright 2011 the original author or authors.
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

import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Busy waiting (forcing a Thread.sleep() while waiting on a condition) should be avoided. Prefer using the gate and barrier objects in the java.util.concurrent package. 
 *
 * @author Hamlet D'Arcy
 */
class BusyWaitRule extends AbstractAstVisitorRule {
    String name = 'BusyWait'
    int priority = 2
    Class astVisitorClass = BusyWaitAstVisitor
}

class BusyWaitAstVisitor extends AbstractAstVisitor {
    @Override
    void visitWhileLoop(WhileStatement node) {
        addViolationIfBusyWait(node)
        super.visitWhileLoop(node)
    }

    @Override
    void visitForLoop(ForStatement node) {
        // -1 is line number for a for-each loop
        if (node.variable.lineNumber == -1) {
            addViolationIfBusyWait(node)
        }
        super.visitForLoop(node)
    }

    private addViolationIfBusyWait(node) {
        if (node.loopBlock instanceof BlockStatement
                && node.loopBlock.statements?.size() == 1
                && node.loopBlock.statements[0] instanceof ExpressionStatement) {

            Expression expression = node.loopBlock.statements[0].expression
            if (AstUtil.isMethodCall(expression, 'sleep', 1) || AstUtil.isMethodCall(expression, 'sleep', 2)) {
                addViolation(expression, 'Busy wait detected. Switch the usage of Thread.sleep() to a lock or gate from java.util.concurrent')
            }
        }
    }
}
