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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractFieldVisitor
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * The field is marked as transient, but the class isn't Serializable, so marking it as transient should have no effect.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryTransientModifierRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryTransientModifier'
    int priority = 3
    Class astVisitorClass = UnnecessaryTransientModifierAstVisitor
}

class UnnecessaryTransientModifierAstVisitor extends AbstractFieldVisitor {
    @Override
    void visitField(FieldNode node) {

        if (Modifier.isTransient(node.modifiers)) {
            if (!AstUtil.classNodeImplementsType(node.owner, Serializable)) {
                addViolation(node, "The field '$node.name' is marked transient, but $node.owner.name does not implement Serializable")
            }

        }
    }
}
