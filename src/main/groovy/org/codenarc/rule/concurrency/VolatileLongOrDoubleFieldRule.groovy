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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor

import java.lang.reflect.Modifier

/**
 * This rule reports long or double fields which are declared as volatile. Java
 * specifies that reads and writes from such fields are atomic, but many JVM's
 * have violated this specification. Unless you are certain of your JVM, it is
 * better to synchronize access to such fields rather than declare them volatile.
 *
 * @author Hamlet D'Arcy
 */
class VolatileLongOrDoubleFieldRule extends AbstractAstVisitorRule {

    String name = 'VolatileLongOrDoubleField'
    int priority = 2
    Class astVisitorClass = VolatileLongOrDoubleFieldVisitor
}

class VolatileLongOrDoubleFieldVisitor extends AbstractFieldVisitor {

    @Override
    void visitField(FieldNode node) {
        if (node?.type == ClassHelper.double_TYPE ||
                node?.type == ClassHelper.long_TYPE ||
                node?.type?.name == 'Long' ||
                node?.type?.name == 'Double') {
            if (Modifier.isVolatile(node.modifiers)) {
                addViolation(node, 'Operations on volatile long and double fields are not guaranteed atomic on all JVMs')
            }
        }
    }
}
