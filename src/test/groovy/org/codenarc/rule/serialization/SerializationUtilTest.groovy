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
import org.codehaus.groovy.ast.GenericsType
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Tests for SerializationUtil
 *
 * @author Daniel Zänker
 */
class SerializationUtilTest extends AbstractTestCase {

    @Test
    void testIsSerializableOrDynamicType_shouldReturnTrueForDynamicType() {
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.OBJECT_TYPE))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnTrueForPrimitiveType() {
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.Integer_TYPE))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnTrueForEffectivelySerializableTypes() {
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(List)))
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Map)))
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Set)))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnTrueForSerializableTypes() {
        assertTrue(SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(ArrayList<Integer>)))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnTrueForSerializableGenericType() {
        ClassNode classNode = ClassHelper.make(ArrayList)
        GenericsType[] genericsTypes = [new GenericsType(ClassHelper.make(HashMap))]
        classNode.genericsTypes = genericsTypes
        assertTrue(SerializationUtil.isSerializableOrDynamicType(classNode))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnFalseForNonSerializableGenericType() {
        ClassNode classNode = ClassHelper.make(ArrayList)
        GenericsType[] genericsTypes = [new GenericsType(ClassHelper.make(Optional<String>))]
        classNode.genericsTypes = genericsTypes
        assertFalse(SerializationUtil.isSerializableOrDynamicType(classNode))
    }

    @Test
    void testIsSerializableOrDynamicType_shouldReturnFalseForNonSerializableTypes() {
        assertFalse(SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Optional<String>)))
    }
}
