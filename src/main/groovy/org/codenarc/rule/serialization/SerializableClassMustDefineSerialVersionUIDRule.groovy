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
package org.codenarc.rule.serialization

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Classes that implement Serializable should define a serialVersionUID. 
 *
 * @author 'Hamlet D'Arcy'
  */
class SerializableClassMustDefineSerialVersionUIDRule extends AbstractAstVisitorRule {
    String name = 'SerializableClassMustDefineSerialVersionUID'
    int priority = 2
    Class astVisitorClass = SerializableClassMustDefineSerialVersionUIDAstVisitor
}

class SerializableClassMustDefineSerialVersionUIDAstVisitor extends AbstractAstVisitor {
    @Override
    protected void visitClassEx(ClassNode node) {

        if (AstUtil.classNodeImplementsType(node, Serializable)) {
            if (!node.fields.find { it.name == 'serialVersionUID' }) {
                addViolation(node, "The class $node.name implements Serializable but does not define a serialVersionUID")
            }
        }
        super.visitClassEx(node)
    }

}
