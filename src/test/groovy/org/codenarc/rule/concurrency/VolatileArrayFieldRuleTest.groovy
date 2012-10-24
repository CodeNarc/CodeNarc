/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for VolatileArrayFieldRule
 *
 * @author Hamlet D'Arcy
 */
class VolatileArrayFieldRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'VolatileArrayField'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                private Object[] field1 = value()
                def field2 = value as Object[]
                def field3 = (Object[])value
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDeclarationType() {
        final SOURCE = '''
            class MyClass {
                private volatile Object[] field1 = value()
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'private volatile Object[] field1',
                'The array field field1 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    @Test
    void testDeclarationType2() {
        final SOURCE = '''
            class MyClass {
                private volatile UnknownClass[] field1 = value()
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'private volatile UnknownClass[] field1',
                'The array field field1 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    @Test
    void testCastAsType() {
        final SOURCE = '''
            class MyClass {
                volatile field2 = value as Object[]
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'volatile field2 = value as Object[]',
                'The array field field2 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    @Test
    void testCastAsType2() {
        final SOURCE = '''
            class MyClass {
                volatile field2 = value as UnknownClass[]
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'volatile field2 = value as UnknownClass[]',
                'The array field field2 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    @Test
    void testCastType() {
        final SOURCE = '''
            class MyClass {
                volatile field3 = (Object[])value
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'volatile field3 = (Object[])value',
                'The array field field3 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    @Test
    void testCastType2() {
        final SOURCE = '''
            class MyClass {
                volatile field3 = (UnknownClass[])value
            }
        '''
        assertSingleViolation(SOURCE, 3,
                'volatile field3 = (UnknownClass[])value',
                'The array field field3 is marked volatile, but the contents of the array will not share the same volatile semantics. Use a different data type')
    }

    protected Rule createRule() {
        new VolatileArrayFieldRule()
    }
}
