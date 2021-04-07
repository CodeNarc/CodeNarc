/*
 * Copyright 2013 the original author or authors.
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
import org.junit.Test

/**
 * Tests for EnumCustomSerializationIgnoredRule
 *
 * @author Chris Mair
 */
class EnumCustomSerializationIgnoredRuleTest extends AbstractRuleTestCase<EnumCustomSerializationIgnoredRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EnumCustomSerializationIgnored'
    }

    @Test
    void testEnum_NoViolations() {
        final SOURCE = '''
            enum MyEnum {
                ONE, TWO, THREE
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testRegularClass_CustomSerialization_NoViolations() {
        final SOURCE = '''
            class MyClass {
                private static final long serialVersionUID = 1234567L
                private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("name", String.class) }
                String name;
                Object writeReplace() { }
                private void writeObject(ObjectOutputStream stream) throws IOException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEnum_IgnoredSerializationFields_Violations() {
        final SOURCE = '''
            enum MyEnum {
                ONE, TWO, THREE
                private static final long serialVersionUID = 1234567L
                private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("name", String.class) }
                String name;
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'private static final long serialVersionUID = 1234567L', message:'serialVersionUID'],
            [line:5, source:'private static final ObjectStreamField[] serialPersistentFields', message:'serialPersistentFields'])
    }

    @Test
    void testEnum_IgnoredSerializationMethods_Violations() {
        final SOURCE = '''
            enum MyEnum {
                ONE, TWO, THREE
                Object writeReplace() { }
                private void writeObject(ObjectOutputStream stream) throws IOException { }

            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'Object writeReplace()', message:'writeReplace'],
            [line:5, source:'private void writeObject(ObjectOutputStream stream)', message:'writeObject']
        )
    }

    @Test
    void testEnum_IgnoredSerializationMethodNames_ButDifferentSignatures_NoViolations() {
        final SOURCE = '''
            enum MyEnum {
                ONE, TWO, THREE
                Object writeReplace(String name) { }
                private Object writeReplace(int count) { }
                private void writeObject(String name) throws IOException { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected EnumCustomSerializationIgnoredRule createRule() {
        new EnumCustomSerializationIgnoredRule()
    }
}
