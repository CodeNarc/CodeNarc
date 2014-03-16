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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for RandomDoubleCoercedToZeroRule
 *
 * @author Hamlet D'Arcy
 */
class RandomDoubleCoercedToZeroRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'RandomDoubleCoercedToZero'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyClass {
                def a = (double) Math.random()
                def b = (Double) Math.random()
                double c = Math.random()
                Double d = Math.random()
                def e = (Math.random()) as double
                def f = (Math.random()) as Double
                def g = Math.random()
            }

            Double intMethod() {
                if (foo) return Math.random()
            }
            double integerMethod() {
                if (foo) return Math.random()
            }
            def defMethod() {
                if (foo) return Math.random()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
        void testFieldsCastToLong() {
            final SOURCE = '''
                class MyClass {
                    def a = (long) Math.random()
                    def b = (Long) Math.random()
                }
            '''
            assertTwoViolations(SOURCE,
                    3, '(long) Math.random()', 'Casting the result of Math.random() to a long always results in 0',
                    4, '(Long) Math.random()', 'Casting the result of Math.random() to a Long always results in 0')
        }

    @Test
        void testLongFields() {
            final SOURCE = '''
                class MyClass {
                    long c = Math.random()
                    Long d = Math.random()
                }
            '''
            assertTwoViolations(SOURCE,
                    3, 'long c = Math.random()', 'Assigning the result of Math.random() to a long always results in 0',
                    4, 'Long d = Math.random()', 'Assigning the result of Math.random() to a Long always results in 0')
        }

    @Test
        void testFieldsAsLong() {
            final SOURCE = '''
                class MyClass {
                    def e = (Math.random()) as long
                    def f = (Math.random()) as Long
                }
            '''
            assertTwoViolations(SOURCE,
                    3, '(Math.random()) as long', 'Casting the result of Math.random() to a long always results in 0',
                    4, '(Math.random()) as Long', 'Casting the result of Math.random() to a Long always results in 0')
        }

    @Test
        void testLongReturningMethods() {
            final SOURCE = '''
                long longMethod() {
                    if (foo) return Math.random()
                }
                Long longMethod2() {
                    return ((foo) ?: Math.random())
                }
            '''
            assertTwoViolations(SOURCE,
                    3, 'Math.random()', 'Returning the result of Math.random() from a long-returning method always returns 0',
                    6, 'Math.random()', 'Returning the result of Math.random() from a Long-returning method always returns 0')
        }

    @Test
        void testFieldsCastToInt() {
            final SOURCE = '''
                class MyClass {
                    def a = (int) Math.random()
                    def b = (Integer) Math.random()
                }
            '''
            assertTwoViolations(SOURCE,
                    3, '(int) Math.random()', 'Casting the result of Math.random() to an int always results in 0',
                    4, '(Integer) Math.random()', 'Casting the result of Math.random() to an Integer always results in 0')
        }

    @Test
        void testIntFields() {
            final SOURCE = '''
                class MyClass {
                    int c = Math.random()
                    Integer d = Math.random()
                }
            '''
            assertTwoViolations(SOURCE,
                    3, 'int c = Math.random()', 'Assigning the result of Math.random() to an int always results in 0',
                    4, 'Integer d = Math.random()', 'Assigning the result of Math.random() to an Integer always results in 0')
        }

    @Test
        void testFieldsAsInt() {
            final SOURCE = '''
                class MyClass {
                    def e = (Math.random()) as int
                    def f = (Math.random()) as Integer
                }
            '''
            assertTwoViolations(SOURCE,
                    3, '(Math.random()) as int', 'Casting the result of Math.random() to an int always results in 0',
                    4, '(Math.random()) as Integer', 'Casting the result of Math.random() to an Integer always results in 0')
        }

    @Test
        void testIntReturningMethods() {
            final SOURCE = '''
                int intMethod() {
                    if (foo) return Math.random()
                }
                Integer integerMethod() {
                    if (foo) return Math.random()
                }
            '''
            assertTwoViolations(SOURCE,
                    3, 'Math.random()', 'Returning the result of Math.random() from an int-returning method always returns 0',
                    6, 'Math.random()', 'Returning the result of Math.random() from an Integer-returning method always returns 0')
        }

    protected Rule createRule() {
        new RandomDoubleCoercedToZeroRule()
    }
}
