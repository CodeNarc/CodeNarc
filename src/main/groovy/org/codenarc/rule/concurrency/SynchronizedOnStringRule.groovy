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
 * Synchronization on a String field can lead to deadlock because Strings are interned by the JVM and can be shared. 
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedOnStringRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedOnString'
    int priority = 2
    Class astVisitorClass = SynchronizedOnStringAstVisitor
}

class SynchronizedOnStringAstVisitor extends AbstractAstVisitor {

    @Override
    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (statement.expression instanceof VariableExpression) {
            if (AstUtil.getFieldType(currentClassNode, statement.expression.variable) == String) {
                addViolation(statement, "Synchronizing on the constant String field $statement.expression.variable is unsafe. Do not synchronize on interned strings")
            }
        }
        super.visitSynchronizedStatement(statement)
    }
}
