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
package org.codenarc.rule.serialization

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * To use a Serializable object's serialPersistentFields correctly, it must be declared private, static, and final.
 *
 * @author 'Hamlet D'Arcy'
  */
class SerialPersistentFieldsRule extends AbstractAstVisitorRule {
    String name = 'SerialPersistentFields'
    int priority = 2
    Class astVisitorClass = SerialPersistentFieldsAstVisitor
}

class SerialPersistentFieldsAstVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {

        if (AstUtil.classNodeImplementsType(node.owner, Serializable)) {
            if (node.name == 'serialPersistentFields') {
                if (!AstUtil.classNodeImplementsType(node.type, ObjectStreamField[].class)) {
                    addViolation(node, "The class is Serializable and defines a field named serialPersistentFields of type $node.type.name. The field should be declared as a ObjectStreamField[] instead")
                } else if (!Modifier.isFinal(node.modifiers) || !Modifier.isStatic(node.modifiers) || !Modifier.isPrivate(node.modifiers)) {
                    addViolation(node, 'The class is Serializable and defines a field named serialPersistentFields which is not private, static, and final')
                }
            } else if (node.name?.toLowerCase() == 'serialpersistentfields') {
                addViolation(node, "The class is Serializable and defines a field named $node.name. This should be named serialPersistentFields instead")
            }
        }
    }
}
