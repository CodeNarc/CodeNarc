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
package org.codenarc.rule.concurrency

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * Catches Serializable classes that define a synchronized readObject method. By definition, an object created by
 * deserialization is only reachable by one thread, and thus there is no need for readObject() to be synchronized.
 * If the readObject() method itself is causing the object to become visible to another thread, that is an example of
 * very dubious coding style.
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedReadObjectMethodRule extends AbstractAstVisitorRule {
    String name = 'SynchronizedReadObjectMethod'
    int priority = 2
    Class astVisitorClass = SynchronizedReadObjectMethodAstVisitor
}

class SynchronizedReadObjectMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        def classNode = getCurrentClassNode()
        if (isSerializable(classNode) && AstUtil.isMethodNode(node, 'readObject', 1)
            && Modifier.isPrivate(node.modifiers)) {
            Parameter parm = node.parameters[0]
            if (node?.returnType?.name == 'void' && AstUtil.classNodeImplementsType(parm.type, ObjectInputStream)) {

                if (Modifier.isSynchronized(node.modifiers)) {
                    addViolation(node, "The Serializable class $classNode.name has a synchronized readObject method. It is normally unnecesary to synchronize within deserializable")
                } else if (isSynchronizedBlock(node.code)) {
                    addViolation(node.code, "The Serializable class $classNode.name has a synchronized readObject method. It is normally unnecesary to synchronize within deserializable")
                }
            }
        }
    }

    private static isSerializable(ClassNode node) {
        boolean isSerializable = false
        node.interfaces?.each {
            if (AstUtil.classNodeImplementsType(it, Serializable)) {
                isSerializable = true
            }
        }
        isSerializable
    }

    private static boolean isSynchronizedBlock(Statement statement) {
        if (!(statement instanceof BlockStatement)) {
            return false
        }

        if (statement.statements.size() != 1) {
            return false
        }

        statement.statements[0] instanceof SynchronizedStatement
    }

}
