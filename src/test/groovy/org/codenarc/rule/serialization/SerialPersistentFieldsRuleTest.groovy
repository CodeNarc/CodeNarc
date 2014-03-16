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
package org.codenarc.rule.serialization

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SerialPersistentFieldsRule
 *
 * @author 'Hamlet D'Arcy'
  */
class SerialPersistentFieldsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SerialPersistentFields'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass implements Serializable {
                private static final ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }

            // not serializable 
            class MyClass2 {
                ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }

            // wrong field name
            class MyClass3 implements Serializable {
                ObjectStreamField[] zz_serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }

        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCommonMisspelling() {
        final SOURCE = '''
            class MyClass implements Serializable {
                private static final serialPerSIStentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'private static final serialPerSIStentFields',
                'Violation in class MyClass. The class is Serializable and defines a field named serialPerSIStentFields. This should be named serialPersistentFields instead')
    }

    @Test
    void testWrongFieldType() {
        final SOURCE = '''
            // Wrong field type, JVM sees it as Object!
            class MyClass implements Serializable {
                private static final serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }
        '''
        assertSingleViolation(SOURCE, 4,
                'private static final serialPersistentFields',
                'Violation in class MyClass. The class is Serializable and defines a field named serialPersistentFields of type java.lang.Object. The field should be declared as a ObjectStreamField[] instead')
    }

    @Test
    void testNotFinal() {
        final SOURCE = '''
            // Wrong field type, JVM sees it as Object!
            class MyClass implements Serializable {
                private static ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }
        '''
        assertSingleViolation(SOURCE, 4,
                'private static ObjectStreamField[] serialPersistentFields',
                'Violation in class MyClass. The class is Serializable and defines a field named serialPersistentFields which is not private, static, and final')
    }

    @Test
    void testNotPrivate() {
        final SOURCE = '''
            // Wrong field type, JVM sees it as Object!
            class MyClass implements Serializable {
                static final public ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }
        '''
        assertSingleViolation(SOURCE, 4, 'static final public ObjectStreamField[] serialPersistentFields',
                'Violation in class MyClass. The class is Serializable and defines a field named serialPersistentFields which is not private, static, and final')
    }

    @Test
    void testNotStatic() {
        final SOURCE = '''
            // Wrong field type, JVM sees it as Object!
            class MyClass implements Serializable {
                private final ObjectStreamField[] serialPersistentFields = [ new ObjectStreamField("myField", List.class) ] as ObjectStreamField[]
            }
        '''
        assertSingleViolation(SOURCE, 4, 'private final ObjectStreamField[] serialPersistentFields',
                'Violation in class MyClass. The class is Serializable and defines a field named serialPersistentFields which is not private, static, and final')
    }

    protected Rule createRule() {
        new SerialPersistentFieldsRule()
    }
}
