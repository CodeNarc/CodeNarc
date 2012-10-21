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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * Creates a violation when the program violates secure coding principles by declaring a finalize() method public.
 *
 * @author Hamlet D'Arcy
  */
class PublicFinalizeMethodRule extends AbstractAstVisitorRule {
    String name = 'PublicFinalizeMethod'
    int priority = 2
    Class astVisitorClass = PublicFinalizeMethodAstVisitor
}

class PublicFinalizeMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {
        if (AstUtil.isMethodNode(node, 'finalize', 0) && !Modifier.isProtected(node.modifiers)) {
            addViolation(node, 'The finalize() method should only be declared with protected visibility')
        }
    }
}
