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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for GetterMethodCouldBePropertyRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class GetterMethodCouldBePropertyRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'GetterMethodCouldBeProperty'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                static VALUE = 'value'
                final String something = 'something'  // this is cleaner
                final String somethingElse = VALUE      // this is cleaner
                final String someClass = String      // this is cleaner

                @Override
                String getSomething(def parameter) {
                    //not a java getter
                }

                @Override
                String getSomethingElse() {
                    performSomeWork() // not a simple one liner
                    'something else'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreProtectedGetterMethods() {
        final SOURCE = '''
            class MyClass {
                @Override
                protected int getValue() { 123 }

                @Override
                protected getValue2() { return 'abc' }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstantReturn() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void testConstantReturnExplicit() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    return 'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void testClassReturn() {
        final SOURCE = '''
            class MyClass {
                @Override
                Class getSomething() {
                    String         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'Class getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal Class something = String")
    }

    @Test
    void testConstantExplicitReturn() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    return 'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void testStaticReturn() {
        final SOURCE = '''
            class MyClass {
                static VALUE = 'value'

                @Override
                String getSomethingElse() {
                    VALUE       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 6,
            'String getSomethingElse()',
            "The method 'getSomethingElse ' in class MyClass can be expressed more simply as the field declaration\nfinal String somethingElse = VALUE")
    }

    @Test
    void testStaticExplicitReturn() {
        final SOURCE = '''
            class MyClass {
                static VALUE = 'value'

                @Override
                String getSomethingElse() {
                    return VALUE       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 6,
            'String getSomethingElse()',
            "The method 'getSomethingElse ' in class MyClass can be expressed more simply as the field declaration\nfinal String somethingElse = VALUE")
    }

    @Test
    void testStaticGetterMethod() {
        final SOURCE = '''
            class MyClass {
                static VALUE = 'value'
                static String getValue() {
                    VALUE       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'static String getValue()',
            "The method 'getValue ' in class MyClass can be expressed more simply as the field declaration\nstatic final String value = VALUE")
    }

    protected Rule createRule() {
        new GetterMethodCouldBePropertyRule()
    }
}
