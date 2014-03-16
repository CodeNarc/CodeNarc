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

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Avoid using ThreadGroup; although it is intended to be used in a threaded environment it contains methods that are not thread safe.
 *
 * @author 'Hamlet D'Arcy'
 */
class ThreadGroupRule extends AbstractAstVisitorRule {
    String name = 'ThreadGroup'
    int priority = 2
    Class astVisitorClass = ThreadGroupAstVisitor
}

class ThreadGroupAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {

        if (isConstructorNamed(call, ThreadGroup)) {
            addViolation(call, 'Avoid using java.lang.ThreadGroup; it is unsafe')
        }
        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'getThreadGroup', 0)) {
            addViolation(call, 'Avoid using java.lang.ThreadGroup; it is unsafe')
        }
        super.visitMethodCallExpression(call)
    }

    private static boolean isConstructorNamed(ConstructorCallExpression call, Class clazz) {
        call.type.name == clazz.name || call.type.name == clazz.simpleName
    }

}
