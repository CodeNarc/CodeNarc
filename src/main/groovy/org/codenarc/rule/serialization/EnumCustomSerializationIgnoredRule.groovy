/*
 * Copyright 2013 the original author or authors.
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
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

import java.lang.reflect.Modifier

/**
 * Checks for enums that define writeObject() or writeReplace() methods, or declare
 * serialPersistentFields or serialVersionUID fields, all of which are ignored for enums.
 *
 * @author Chris Mair
 */
class EnumCustomSerializationIgnoredRule extends AbstractAstVisitorRule {

    String name = 'EnumCustomSerializationIgnored'
    int priority = 2
    Class astVisitorClass = EnumCustomSerializationIgnoredAstVisitor
}

class EnumCustomSerializationIgnoredAstVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode node) {
        if (currentClassNode.isEnum()) {
            if (isPrivateStaticFinalField(node, 'serialVersionUID') || isPrivateStaticFinalField(node, 'serialPersistentFields')) {
                addViolation(node, "Field ${node.name} in class $currentClassName is ignored for enums")
            }
        }
    }

    @Override
    protected void visitMethodComplete(MethodNode node) {
        if (currentClassNode.isEnum()) {
            if (isWriteReplaceMethod(node) || isWriteObjectMethod(node)) {
                addViolation(node, "Method ${node.name}() in class $currentClassName is ignored for enums")
            }
        }
    }

    private isWriteObjectMethod(MethodNode node) {
        node.name == 'writeObject' && node?.parameters.size() == 1 && node.parameters[0].type.name == 'ObjectOutputStream'
    }

    private isWriteReplaceMethod(MethodNode node) {
        node.name == 'writeReplace' && !node.parameters
    }

    private boolean isPrivateStaticFinalField(FieldNode node, String name) {
        return node?.name == name &&
            Modifier.isPrivate(node?.modifiers) &&
            Modifier.isStatic(node?.modifiers) &&
            Modifier.isFinal(node?.modifiers)
        }

}
