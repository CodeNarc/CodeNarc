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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.lang.reflect.Modifier

/**
 * An abstract class cannot be instantiated, therefore a public constructor is useless and confusing.
 *
 * @author Chris Mair
 */
class AbstractClassWithPublicConstructorRule extends AbstractAstVisitorRule {
    String name = 'AbstractClassWithPublicConstructor'
    int priority = 2
    Class astVisitorClass = AbstractClassWithPublicConstructorAstVisitor
}

class AbstractClassWithPublicConstructorAstVisitor extends AbstractAstVisitor {

    @Override
    protected void visitClassEx(ClassNode node) {

        if (Modifier.isAbstract(node.modifiers)) {
            node.declaredConstructors.each { constructor ->
                if (Modifier.isPublic(constructor.modifiers)) {
                    addViolation(constructor, "The abstract class $node.name contains a public constructor")
                }
            }
        }
        super.visitClassEx(node)
    }
}
