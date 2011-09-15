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

import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The code synchronizes on a boxed primitive constant, such as an Integer. Since Integer objects can be cached and
 * shared, this code could be synchronizing on the same object as other, unrelated code, leading to unresponsiveness and
 * possible deadlock
 *
 * @author 'Hamlet D'Arcy'
 */
class SynchronizedOnBoxedPrimitiveRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedOnBoxedPrimitive'
    int priority = 2
    Class astVisitorClass = SynchronizedOnBoxedPrimitiveAstVisitor
}

class SynchronizedOnBoxedPrimitiveAstVisitor extends AbstractAstVisitor {

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (statement.expression instanceof VariableExpression) {
            Class type = AstUtil.getFieldType(currentClassNode, statement.expression.variable)
            if (type in [Byte, Short, Double, Integer, Long, Float, Character, Boolean]) {
                addViolation(statement, "Synchronizing on the $type.simpleName field $statement.expression.variable is unsafe. Do not synchronize on boxed types")
            }
        }
        super.visitSynchronizedStatement(statement)
    }
}
