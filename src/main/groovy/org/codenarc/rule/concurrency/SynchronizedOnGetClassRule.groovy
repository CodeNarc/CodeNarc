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

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Synchronized on getClass rather than class literal. This instance method synchronizes on this.getClass(). If this
 * class is subclassed, subclasses will synchronize on the class object for the subclass, which isn't likely what was intended.
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedOnGetClassRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedOnGetClass'
    int priority = 2
    Class astVisitorClass = SynchronizedOnGetClassAstVisitor
}

class SynchronizedOnGetClassAstVisitor extends AbstractAstVisitor {

    void visitSynchronizedStatement(SynchronizedStatement statement) {
        if (!isFirstVisit(statement)) {
            return
        }
        
        if (statement.expression instanceof MethodCallExpression && AstUtil.isMethodNamed(statement.expression, 'getClass', 0)) {
            addViolation statement, 'Synchronizing on getClass() should be replaced with a class literal'
        }
        super.visitSynchronizedStatement(statement)
    }
}
