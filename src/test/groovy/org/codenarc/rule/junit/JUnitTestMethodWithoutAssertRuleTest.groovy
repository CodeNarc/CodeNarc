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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for JUnitTestMethodWithoutAssertRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class JUnitTestMethodWithoutAssertRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitTestMethodWithoutAssert'
    }

    void testAnnotatedMethods_SuccessScenario() {
        final SOURCE = '''
            class MyTest {
                @Test
                void someTestMethod1() {
                    assert 1 == 2
                }
                @Test
                void someTestMethod2() {
                    assertEquals(1, 2)
                }
                @org.junit.Test
                void someTestMethod3() {
                    assert 1 == 2
                }
                @org.junit.Test
                void someTestMethod4() {
                    assertEquals(1, 2)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testAnnotatedMethodsWithAnnotationParameters_SuccessScenario() {
        final SOURCE = '''
            class MyTest {
                @Test(expected = IllegalArgumentException)
                void someTestMethod1() {
                    doSomething()
                }
                @Test(timeout = 1000)
                void someTestMethod2() {
                    doSomething()
                }
                @org.junit.Test(expected = IllegalArgumentException)
                void someTestMethod3() {
                    doSomething()
                }
                @org.junit.Test(timeout = 1000)
                void someTestMethod4() {
                    doSomething()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testJUnitStyleConventions_SuccessScenario() {
        final SOURCE = '''
            class MyTest {
                private void testPrivate() {
                    // ignored because method is private
                }
                int testIntMethod() {
                    // ignored because method returns int
                }
                void testMethod1() {
                    assert 1 == 2
                }
                void testMethod2() {
                    assertEquals(1, 2)
                }
                void testMethod3() {
                    shouldFail {
                        foo()
                    }
                }
                void testMethod4() {
                    fail()
                }
                void testMethod5() {
                    verify()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testViolationWithMethodNameConvention() {
        final SOURCE = '''
            class MyTest {
                void testMethod() {
                    doSomething()
                    doSomethingElse()
                    // where is the assertion?
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void testMethod()', "Test method 'testMethod' makes no assertions")
    }

    void testViolationWithEmptyBody() {
        final SOURCE = '''
            class MyTest {
                void testMethod() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void testMethod()', "Test method 'testMethod' makes no assertions")
    }

    void testViolationWithTestAnnotation() {
        final SOURCE = '''
            class MyTest {
                @Test
                void someMethod1() {
                    doSomething()
                    doSomethingElse()
                    // where is the assertion?
                }
                @org.junit.Test
                @Unknown
                @YetAnotherAnnotation // these annotations ALSO test the line number fix
                void someMethod2() {
                    doSomething()
                    doSomethingElse()
                    // where is the assertion?
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4,  'void someMethod1()', "Test method 'someMethod1' makes no assertions",
                12, 'void someMethod2()', "Test method 'someMethod2' makes no assertions")
    }

    protected Rule createRule() {
        new JUnitTestMethodWithoutAssertRule()
    }
}