/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.stmt.ForStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Reports classes with nested for loops.
 *
 * @author Maciej Ziarko
 */
class NestedForLoopRule extends AbstractAstVisitorRule {

    String name = 'NestedForLoop'
    int priority = 3
    Class astVisitorClass = NestedForLoopAstVisitor
}

class NestedForLoopAstVisitor extends AbstractAstVisitor {
    
    private final Stack forStatementsStack = [] as Stack
    
    @Override
    void visitForLoop(ForStatement forLoop) {
        forStatementsStack.push(forLoop)
        if (isForNested()) {
            addViolation(forLoop, 'Nested for loop')    
        }
        super.visitForLoop(forLoop)
        forStatementsStack.pop()
    }

    private boolean isForNested() {
        return forStatementsStack.size() >= 2
    }
}
