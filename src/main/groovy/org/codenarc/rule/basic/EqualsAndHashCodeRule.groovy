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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Rule that checks that if either the <code>boolean equals(Object)</code> or
 * the <code>int hashCode()</code> methods are overridden within a class, then both must be overridden.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class EqualsAndHashCodeRule extends AbstractAstVisitorRule {
    String name = 'EqualsAndHashCode'
    int priority = 2
    Class astVisitorClass = EqualsAndHashCodeAstVisitor
}

class EqualsAndHashCodeAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode classNode) {
        def methods = classNode.methods
        def equalsMethod = methods.find { m ->
            m.name == 'equals' &&
            m.parameters.size() == 1 &&
            m.parameters[0].type.name in ['Object', 'java.lang.Object'] 
        }
        def hashCodeMethod = methods.find { m -> m.name == 'hashCode' && m.parameters.size() == 0 }
        def oneButNotBoth = (equalsMethod || hashCodeMethod) && !(equalsMethod && hashCodeMethod)

        if (oneButNotBoth) {
            if (equalsMethod) {
                addViolation(classNode, "The class $classNode.name defines equals(Object) but not hashCode()")
            } else {
                addViolation(classNode, "The class $classNode.name defines hashCode() but not equals(Object)")
            }
        }
        super.visitClassEx(classNode)
    }
}

