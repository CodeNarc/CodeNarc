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
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class JUnitTestMethodWithoutAssertRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "JUnitTestMethodWithoutAssert"
    }

    void testAnnotatedMethods_SuccessScenario() {
        final SOURCE = '''
        	class MyTest {
                @Test
                public void someTestMethod1() {
                    assert 1 == 2
                }
                @Test
                public void someTestMethod2() {
                    assertEquals(1, 2)
                }
                @org.junit.Test
                public void someTestMethod1() {
                    assert 1 == 2
                }
                @org.junit.Test
                public void someTestMethod2() {
                    assertEquals(1, 2)
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
                public int testIntMethod() {
                    // ignored because method returns int
                }
                public void testMethod1() {
                    assert 1 == 2
                }
                public void testMethod2() {
                    assertEquals(1, 2)
                }
                public void testMethod3() {
                    shouldFail {
                        foo()
                    }
                }
                public void testMethod4() {
                    fail()
                }
                public void testMethod5() {
                    verify()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testViolationWithMethodNameConvention() {
        final SOURCE = '''
        	class MyTest {
                public void testMethod() {
                    doSomething()
                    doSomethingElse()
                    // where is assertion?
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void testMethod()', "Test method 'testMethod' makes no assertions")
    }

    void testViolationWithEmptyBody() {
        final SOURCE = '''
        	class MyTest {
                public void testMethod() { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'public void testMethod()', "Test method 'testMethod' makes no assertions")
    }

    void testViolationWithTestAnnotation() {
        final SOURCE = '''
        	class MyTest {
                @Test
                public void someMethod1() {
                    doSomething()
                    doSomethingElse()
                    // where is assertion?
                }
                @org.junit.Test
                @Unknown
                @YetAnotherAnnotation // these annotations ALSO test the line number fix
                public void someMethod2() {
                    doSomething()
                    doSomethingElse()
                    // where is assertion?
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'void someMethod1()', "Test method 'someMethod1' makes no assertions",
                12, 'void someMethod2()', "Test method 'someMethod2' makes no assertions")
    }

    protected Rule createRule() {
        new JUnitTestMethodWithoutAssertRule()
    }
}