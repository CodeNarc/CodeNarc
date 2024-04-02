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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests for SerializationUtil
 *
 * @author Daniel Zänker
 */
class SerializationUtilTest extends AbstractTestCase {

    @Nested
    class isSerializableOrDynamicType {

        @Test
        void ShouldReturnTrueForDynamicType() {
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.OBJECT_TYPE)
        }

        @Test
        void ShouldReturnTrueForPrimitiveType() {
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.Integer_TYPE)
        }

        @Test
        void ShouldReturnTrueForEffectivelySerializableTypes() {
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(List))
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Map))
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Set))
        }

        @Test
        void ShouldReturnTrueForSerializableTypes() {
            assert SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(ArrayList<Integer>))
        }

        @Test
        void ShouldReturnTrueForSerializableGenericType() {
            ClassNode classNode = ClassHelper.make(ArrayList)
            GenericsType[] genericsTypes = [new GenericsType(ClassHelper.make(HashMap))]
            classNode.genericsTypes = genericsTypes
            assert SerializationUtil.isSerializableOrDynamicType(classNode)
        }

        @Test
        void ShouldReturnFalseForNonSerializableGenericType() {
            ClassNode classNode = ClassHelper.make(ArrayList)
            GenericsType[] genericsTypes = [new GenericsType(ClassHelper.make(Optional<String>))]
            classNode.genericsTypes = genericsTypes
            assert !SerializationUtil.isSerializableOrDynamicType(classNode)
        }

        @Test
        void ShouldReturnFalseForNonSerializableTypes() {
            assert !SerializationUtil.isSerializableOrDynamicType(ClassHelper.make(Optional<String>))
        }
    }

}
