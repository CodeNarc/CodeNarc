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

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * If a class defines a "void close()" then that class should implement java.io.Closeable.
 *
 * @author Hamlet D'Arcy
 */
class CloseWithoutCloseableRule extends AbstractAstVisitorRule {
    String name = 'CloseWithoutCloseable'
    int priority = 2
    Class astVisitorClass = CloseWithoutCloseableAstVisitor
}

class CloseWithoutCloseableAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        if (AstUtil.isMethodNode(node, 'close', 0, Void.TYPE) && !Modifier.isPrivate(node.modifiers)) {
            if (!AstUtil.classNodeImplementsType(node.declaringClass, Closeable) && !AstUtil.classNodeImplementsType(node.declaringClass, Script)) {
                addViolation(node, 'void close() method defined without implementing Closeable')
            }
        }
    }
}
