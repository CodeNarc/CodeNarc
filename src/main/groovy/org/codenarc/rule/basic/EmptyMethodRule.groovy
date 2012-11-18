/*AbstractMethodVisitor
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * A method was found without an implementation. If the method is overriding or implementing a parent method, then mark it with the @Override annotation. 
 *
 * @author Hamlet D'Arcy
 */
class EmptyMethodRule extends AbstractAstVisitorRule {
    String name = 'EmptyMethod'
    int priority = 2
    Class astVisitorClass = EmptyMethodAstVisitor
}

class EmptyMethodAstVisitor extends AbstractMethodVisitor {
    @Override
    void visitMethod(MethodNode node) {

        if (AstUtil.isEmptyBlock(node.code) && !Modifier.isAbstract(node.declaringClass.modifiers)) {
            if (!node.annotations.find { it?.classNode?.name == 'Override' }) {
                addViolation(node, "The method $node.name is both empty and not marked with @Override")
            }
        }
    }
}
