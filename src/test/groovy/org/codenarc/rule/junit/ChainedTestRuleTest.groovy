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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ChainedTestRule
 *
 * @author Hamlet D'Arcy
  */
class ChainedTestRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ChainedTest'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testFoo() {
                    // OK, no violation: one arg method is not actually a test method
                    5.times { testBar(it) }
                }

                private static void assertSomething() {
                    foo.testBar() // not a self/this call
                    Other.testBar() // not a self/this call
                }

                public void testBar() {
                    // ...
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSelfCallImpliedThis() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testFoo() {
                    5.times { testBar() }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'testBar()', 'The test method testBar() is being invoked explicitly from within a unit test. Tests should be isolated and not dependent on one another')
    }

    @Test
    void testSelfCallExplicitThis() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testFoo() {
                    5.times { this.testBar() }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'this.testBar()', 'The test method testBar() is being invoked explicitly from within a unit test. Tests should be isolated and not dependent on one another')
    }

    @Test
    void testCallsInHelperMethods() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {

                private static void assertSomething() {
                    testBar() // violation, even if in helper method
                    this.testBar() // violation, even if in helper method
                }
            }
        '''
        assertTwoViolations(SOURCE,
                5, 'testBar()', 'The test method testBar() is being invoked explicitly from within a unit test. Tests should be isolated and not dependent on one another',
                6, 'this.testBar()', 'The test method testBar() is being invoked explicitly from within a unit test. Tests should be isolated and not dependent on one another')
    }

    protected Rule createRule() {
        new ChainedTestRule()
    }
}
