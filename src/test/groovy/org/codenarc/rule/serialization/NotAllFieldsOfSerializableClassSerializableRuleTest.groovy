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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for NotAllFieldsOfSerializableClassSerializableRule
 *
 * @author Daniel Zänker
 */
class NotAllFieldsOfSerializableClassSerializableRuleTest extends AbstractRuleTestCase<NotAllFieldsOfSerializableClassSerializableRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'NotAllFieldsOfSerializableClassSerializable'
    }

    @Test
    void testProperties_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                SomeClass prop
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFields_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                private SomeClass field
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testPrimitives_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                int i
                double d
                String s
                int[] iArray = [1,2,3]
            }
            
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonSerializableClass_NoViolation() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                int i
            }
            
            class OtherClass {
                OtherClass notSer
                SomeClass ser
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonSerializableFields_Violation() {
        final SOURCE = '''
            class SomeClass implements Serializable {
                SomeClass prop
                OtherClass notSerProp
                private OtherClass notSerField
            }
            
            class OtherClass {
            }
        '''
        assertViolations(SOURCE,
            [line: 4, source: 'OtherClass notSerProp', message: 'Field notSerProp is not Serializable but the declaring class SomeClass is'],
            [line: 5, source: 'private OtherClass notSerField', message: 'Field notSerField is not Serializable but the declaring class SomeClass is'])
    }

    @Override
    protected NotAllFieldsOfSerializableClassSerializableRule createRule() {
        new NotAllFieldsOfSerializableClassSerializableRule()
    }
}
