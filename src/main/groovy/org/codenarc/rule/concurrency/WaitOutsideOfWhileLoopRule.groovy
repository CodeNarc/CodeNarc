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

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for calls to wait() that are not within a while loop.
 *
 * See Joshua Bloch's Effective Java: chapter 50 (1st edition) is entitled "Never invoke wait outside a loop."
 * See https://www.securecoding.cert.org/confluence/display/java/THI03-J.+Always+invoke+wait()+and+await()+methods+inside+a+loop
 *
 * @author Chris Mair
 */
class WaitOutsideOfWhileLoopRule extends AbstractAstVisitorRule {
    String name = 'WaitOutsideOfWhileLoop'
    int priority = 2
    Class astVisitorClass = WaitOutsideOfWhileLoopAstVisitor
}

class WaitOutsideOfWhileLoopAstVisitor extends AbstractAstVisitor {

    private boolean withinWhileLoop = false

    void visitWhileLoop(WhileStatement whileStatement) {
        withinWhileLoop = true
        super.visitWhileLoop(whileStatement)
        withinWhileLoop = false
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodNamed(call, 'wait', 0) && !withinWhileLoop) {
            addViolation call, 'Only call the wait() method within a while loop. Or better yet, prefer the Java concurrency utilities to wait() and notify()'
        }
        super.visitMethodCallExpression(call)
    }
}
