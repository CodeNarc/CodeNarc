/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for JUnitPublicPropertyRule
 *
 * @author Chris Mair
 */
class JUnitPublicPropertyRuleTest extends AbstractRuleTestCase<JUnitPublicPropertyRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitPublicProperty'
        assert rule.applyToClassNames == DEFAULT_TEST_CLASS_NAMES
    }

    @Test
    void testTestClassWithNoProperties_NoViolations() {
        final SOURCE = '''
            class MyTest {
                public static final MAX_VALUE = 1000    // field
                public int count                        // field
                private service                         // field
                @Test
                void testMe() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonTestClassWithProperty_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def id = 1234
                String name
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTestClassWithProperty_Violation() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                static String id    // static property
                def helper          // property
                String name         // property

                @Test
                void testMe() { }
            }
        '''
        def message = 'The test class %s contains a public property %s. There is usually no reason to have a public property (even a constant) on a test class.'
        assertViolations(SOURCE,
            [line:4, source:'static String id', message:String.format(message, 'MyTestCase', 'id')],
            [line:5, source:'def helper', message:String.format(message, 'MyTestCase', 'helper')],
            [line:6, source:'String name', message:String.format(message, 'MyTestCase', 'name')])
    }

    @Test
    void testTestClassWithProperty_IgnorePropertyNames_NoViolations() {
        final SOURCE = '''
            class MyTestCase {
                def helper      // property
                String name     // property
            }
        '''
        rule.ignorePropertyNames = 'h*lper, name'
        assertNoViolations(SOURCE)
    }

    @Override
    protected JUnitPublicPropertyRule createRule() {
        new JUnitPublicPropertyRule()
    }
}
