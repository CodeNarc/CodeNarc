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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

import java.lang.reflect.Modifier

/**
 * Synchronized Method Rule - This rule reports uses of the synchronized keyword on
 * methods. Synchronized methods are the same as synchronizing on 'this', which
 * effectively make your synchronization policy public and modifiable by other objects.
 * To avoid possibilities of deadlock, it is better to synchronize on internal objects. 
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedMethodRule extends AbstractAstVisitorRule {

    String name = 'SynchronizedMethod'
    int priority = 2
    Class astVisitorClass = SynchronizedMethodAstVisitor
}

class SynchronizedMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        if (Modifier.isSynchronized(node.getModifiers())) {
            addViolation(node, "The method $node.name is synchronized at the method level")
        }
    }
}
