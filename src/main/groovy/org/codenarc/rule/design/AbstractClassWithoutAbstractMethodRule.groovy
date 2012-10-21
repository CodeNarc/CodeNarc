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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.lang.reflect.Modifier

/**
 * The abstract class does not contain any abstract methods. An abstract class suggests an incomplete implementation,
 * which is to be completed by subclasses implementing the abstract methods. If the class is intended to be used as a
 * base class only (not to be instantiated direcly) a protected constructor can be provided prevent direct instantiation.
 *
 * @author 'Hamlet D'Arcy'
 */
class AbstractClassWithoutAbstractMethodRule extends AbstractAstVisitorRule {
    String name = 'AbstractClassWithoutAbstractMethod'
    int priority = 2
    Class astVisitorClass = AbstractClassWithoutAbstractMethodAstVisitor
}

class AbstractClassWithoutAbstractMethodAstVisitor extends AbstractAstVisitor {
    @Override protected void visitClassEx(ClassNode node) {

        if (!node.isInterface() && Modifier.isAbstract(node.modifiers) && !node.superClass.name.startsWith('Abstract') && !node.superClass.name.startsWith('Base')) {
            if (!node.methods.any {  Modifier.isAbstract(it.modifiers)  }) {
                addViolation(node, "The abstract class $node.name contains no abstract methods")
            }
        }
        super.visitClassEx(node)
    }
}
