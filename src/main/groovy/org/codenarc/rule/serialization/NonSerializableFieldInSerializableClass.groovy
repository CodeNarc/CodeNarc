/*
 * Copyright 2023 the original author or authors.
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

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * All fields of a class that implements Serializable should also implement it
 *
 * @author Daniel Zänker
 */
class NonSerializableFieldInSerializableClass extends AbstractAstVisitorRule {

    String name = 'NonSerializableFieldInSerializableClass'
    int priority = 2
    Class astVisitorClass = NonSerializableFieldInSerializableClassVisitor
    int compilerPhase = Phases.SEMANTIC_ANALYSIS
}

class NonSerializableFieldInSerializableClassVisitor extends AbstractAstVisitor {

    @Override
    void visitField(FieldNode field) {
        if (AstUtil.classNodeImplementsType(currentClassNode, Serializable)) {
            if (!SerializationUtil.isSerializableOrDynamicType(field.type)) {
                addViolation(field, "Field ${field.name} is not Serializable but the declaring class ${currentClassNode.name} is")
            }
        }
    }
}
