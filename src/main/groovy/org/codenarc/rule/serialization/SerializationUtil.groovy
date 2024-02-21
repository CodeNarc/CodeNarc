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

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codenarc.util.AstUtil

/**
 * @author Daniel Zänker
 */
class SerializationUtil {
    static final List<ClassNode> EFFECTIVELY_SERIALIZABLE = [ClassHelper.make(List), ClassHelper.make(Map), ClassHelper.make(Set)]

    static boolean isSerializableOrDynamicType(ClassNode classNode) {
        if (classNode == ClassHelper.DYNAMIC_TYPE) {
            // for dynamic types we can't really check statically
            return true
        }
        if (ClassHelper.isPrimitiveType(classNode)) {
            // primitives are all serializable
            return true
        }
        if (EFFECTIVELY_SERIALIZABLE.contains(classNode)) {
            // some interfaces are effectively serializable because (nearly) every implementation is serializable
            // it would be bad codestyle to use the serializable implementation as type
            return true
        }
        if (AstUtil.classNodeImplementsType(classNode, Serializable)) {
            if (classNode.genericsTypes) {
                return classNode.genericsTypes.every { isSerializableOrDynamicType(it.type) }
            }
            return true
        }
        return false
    }
}
