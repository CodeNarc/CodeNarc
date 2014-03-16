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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ReturnsNullInsteadOfEmptyArrayRule
 *
 * @author Hamlet D'Arcy
 */
class ReturnsNullInsteadOfEmptyArrayRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ReturnsNullInsteadOfEmptyArray'
    }

    @Test
    void testNoViolation() {
        final SOURCE = '''
        	String[] myMethod() {

                def c = {
                    return null // ignore returns from nested closure
                }
                def o = new Object() {
                    def foo() {
                        return null // ignore returns from nested class
                    }
                }
                return [] as String[]
            }

            def c =  {
                return [] as String[]
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringArrayMethod() {
        final SOURCE = '''
        	String[] myMethod() {
                if (x) return null
                return [] as String[]
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    @Test
    void testStringArrayMethodInClass() {
        final SOURCE = '''
            class MyClass {
                String[] myMethod() {
                    if (x) return null
                    return [] as String[]
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'null')
    }

    @Test
    void testDefMethod() {
        final SOURCE = '''
        	def myMethod() {
                if (x) return null
                return [] as String[]
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    @Test
    void testTernaryReturns() {
        final SOURCE = '''
                def a =  {
                    return foo ? null : [] as String[]
                }
                def b =  {
                    return foo ? [] as String[] : null
                }
            '''
        assertTwoViolations SOURCE,
                3, 'foo ? null : [] as String[]',
                6, 'foo ? [] as String[] : null'
    }

    @Test
    void testDefMethodInClass() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    if (x) return null
                    return [] as String[]
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'null')
    }

    @Test
    void testStringArrayMethodInInnerClass() {
        final SOURCE = '''
            def o = new Object() {
                String[] myMethod() {
                    if (x) return null
                    return [] as String[]
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'null')
    }

    @Test
    void testInClosure() {
        final SOURCE = '''
            def c = {
                if (x) return null      // bad
                return [] as String[]
            }
        '''
        assertSingleViolation(SOURCE, 3, 'null')
    }

    @Test
    void testInClosureWithinAClosure() {
        final SOURCE = '''
            def a = {
                def b = {
                    if (x) return null      // bad
                    return [] as String[]
                }
                def c = {
                    if (x) return null      // bad
                    return [] as String[]
                }
                return [] as String[] // ok
            }
        '''
        assertTwoViolations(SOURCE, 4, 'null', 8, 'null')
    }

    @Test
    void testInAnonymousClassWithinAnonymousClass() {
        final SOURCE = '''
            def a = new Object() {
                String[] m1() {
                    def b = new Object() {
                        String[] m1() {
                            return null
                        }
                        String[] m2() {
                            return null
                        }
                    }
                    return [] as String[]
                }
            }
        '''
        assertTwoViolations(SOURCE, 6, 'null', 9, 'null')
    }

    @Test
    void testClosureInAnonymousClassWithinAnonymousClass() {
        final SOURCE = '''
            def a = new Object() {
                String[] m1() {
                    def b = new Object() {
                        void m1() {
                            def z = {
                                if (q) {
                                    return null
                                } else {
                                    return [] as String[]
                                }
                            }
                        }
                    }
                    return [] as String[]
                }
            }
        '''
        assertSingleViolation(SOURCE, 8, 'null')
    }

    protected Rule createRule() {
        new ReturnsNullInsteadOfEmptyArrayRule()
    }

}
