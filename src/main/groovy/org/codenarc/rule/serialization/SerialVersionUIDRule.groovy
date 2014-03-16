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
package org.codenarc.rule.serialization

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.PropertyNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.lang.reflect.Modifier

/**
 * Rule that checks that serialVersionUID field is static final and type long, and is not a property.
 *
 * @author Hamlet D'Arcy
  */
class SerialVersionUIDRule extends AbstractAstVisitorRule {
    String name = 'SerialVersionUID'
    int priority = 2
    Class astVisitorClass = SerialVersionUIDAstVisitor
}

class SerialVersionUIDAstVisitor extends AbstractAstVisitor {

    private final static SERIAL_ID = 'serialVersionUID'
    void visitField(FieldNode node) {
        if (node?.name == SERIAL_ID) {
            if (!Modifier.isPrivate(node?.modifiers)) {
                addViolation node, 'serialVersionUID found that is not private.'
            }
            if (!Modifier.isStatic(node?.modifiers)) {
                addViolation node, 'serialVersionUID found that is not static.'
            }
            if (!Modifier.isFinal(node?.modifiers)) {
                addViolation node, 'serialVersionUID found that is not final.'
            }
            if (node?.type?.name != 'long') {
                addViolation node, 'serialVersionUID found that is not long. Found: ' + node?.type?.name
            }
        }
        super.visitField node
    }

    void visitProperty(PropertyNode node) {
        if (node?.name == SERIAL_ID) {
            addViolation node, 'serialVersionUID found that is a property. '
        }
        super.visitProperty(node)
    }

}
