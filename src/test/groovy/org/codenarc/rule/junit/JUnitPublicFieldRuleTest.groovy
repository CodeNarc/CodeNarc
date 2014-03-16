/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for JUnitPublicFieldRule
 *
 * @author Chris Mair
 */
class JUnitPublicFieldRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JUnitPublicField'
    }

    @Test
    void testTestClassWithNoPublicFields_NoViolations() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                private service
                protected count = 99
                def helper      // this is a property, not a public field
                String name     // this is a property, not a public field

                @Test
                void testMe() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonTestClassWithPublicFields_NoViolations() {
        final SOURCE = '''
            class MyClass {
                public int count
                public static final MAX_VALUE = 1000
            }
        '''
        sourceCodePath = 'src/MyClass.groovy'
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithPublicFieldsAnnotatedWithRule_NoViolations() {
        final SOURCE = '''
            class MyTestCase {
                @Rule public TestName testName = new TestName()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassWithPublicFields_Violations() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                public int count
                public static final MAX_VALUE = 1000
                @Test
                void testMe() { }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'public int count', messageText:'count'],
            [lineNumber:5, sourceLineText:'public static final MAX_VALUE = 1000', messageText:'MAX_VALUE'])
    }

    @Test
    void testInterfaceWithPublicFields_NoViolations() {
        final SOURCE = '''
            interface MyTest {
                public static final MAX_VALUE = 1000
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitPublicFieldRule()
    }
}
