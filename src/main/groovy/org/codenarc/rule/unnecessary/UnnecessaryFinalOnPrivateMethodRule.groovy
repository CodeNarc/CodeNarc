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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor

import java.lang.reflect.Modifier

/**
 * A private method is marked final. Private methods cannot be overridden, so marking it final is unnecessary. 
 *
 * @author 'Hamlet D'Arcy'
  */
class UnnecessaryFinalOnPrivateMethodRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryFinalOnPrivateMethod'
    int priority = 3
    Class astVisitorClass = UnnecessaryFinalOnPrivateMethodAstVisitor
}

class UnnecessaryFinalOnPrivateMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (Modifier.isFinal(node.modifiers) && Modifier.isPrivate(node.modifiers)) {
            addViolation(node, "The '$node.name' method is both private and final")
        }
    }
}
