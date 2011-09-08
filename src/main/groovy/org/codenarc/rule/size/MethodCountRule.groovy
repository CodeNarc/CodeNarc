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
package org.codenarc.rule.size

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * A class with too many methods is probably a good suspect for refactoring, in order to reduce its complexity and find
 * a way to have more fine-grained objects.
 *
 * @author Tomasz Bujok
 * @author Hamlet D'Arcy
  */
class MethodCountRule extends AbstractAstVisitorRule {
    String name = 'MethodCount'
    int priority = 2
    Class astVisitorClass = MethodCountAstVisitor
    int maxMethods = 30
}

class MethodCountAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode node) {
        if (node.methods?.size() > rule.maxMethods) {
            addViolation(node, "Class $node.name has ${node.methods?.size()} methods")
        }
        super.visitClassEx(node)
    }

}
