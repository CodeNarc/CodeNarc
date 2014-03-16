/*
 * Copyright 2009 the original author or authors.
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
import org.junit.Test

/**
 * Tests for JUnitPublicNonTestMethodRule
 *
 * @author Chris Mair
  */
class JUnitPublicNonTestMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitPublicNonTestMethod'
    }

    @Test
    void testApplyTo_PublicNonTestMethod() {
        final SOURCE = '''
            class MyTestCase extends GroovyTestCase {
                void doSomething() {
                    // this does not need to be public
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void doSomething() {')
    }

    @Test
    void testApplyTo_TwoPublicMethods() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                boolean isThisNecessary() { false }
                void testSomething() {
                    assert count > 0
                }
                void doSomething() {
                    // this does not need to be public
                }
            }
        '''
        assertTwoViolations(SOURCE, 3, 'boolean isThisNecessary() { false }', 7, 'void doSomething() {')
    }

    @Test
    void testApplyTo_MethodNameStartsWithTestButHasParameters() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void testSomething(int count) {
                    // this is not a test method
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void testSomething(int count) {')
    }

    @Test
    void testApplyTo_TestMethodsOnly() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }

                void testOther() {
                    println 'ok'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoresConstructors() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public MyTest() { }
                public MyTest(int count) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpAndTearDown() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }

                void doSomething() {
                    // this does not need to be public
                }

                void setUp() {
                    super.setUp()
                }
                void tearDown() {
                    super.tearDown()
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'void doSomething() {')
    }

    @Test
    void testApplyTo_TestAnnotation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                @Test(expected = MyException.class)
                void shouldSendEmail() { assert count == 0 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_BeforeAnnotation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }
                @Before void init() { println 'ok' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AfterAnnotation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }
                @After void cleanUp() { println 'done' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_BeforeClassAnnotation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }
                @BeforeClass void initClass() { println 'ok' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AfterClassAnnotation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testSomething() { println 'ok' }
                @AfterClass void cleanUp() { println 'done' }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_StaticMethods() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                static int calculate() { 23 }
                public static boolean isReady() { true }
                private static String appendName(String n) { n }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoPublicNonTestMethods() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void testSomething() {
                    assert count > 0
                }
                protected boolean isReady() { true }
                private int calculate() { 23 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonTestClass() {
        final SOURCE = '''
            class MyClass {
                public doSomething() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testThatAtOverrideSuppressesViolation() {
        final SOURCE = '''
            class MyTest {
                @Override
                public doSomething() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitPublicNonTestMethodRule()
    }
}
