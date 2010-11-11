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
 * Tests for UseAssertTrueInsteadOfAssertEqualsRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UseAssertTrueInsteadOfAssertEqualsRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == "UseAssertTrueInsteadOfAssertEquals"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(1, foo())
                    assertEquals('message'1, foo())
                    assertTrue(foo())
                    assertTrue('message', foo())
                    assertSame(foo(), foo())
                    assertTrue(foo() > bar())
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    void testAssertTrueViolation() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(true, foo())
                    assertEquals("message", true, foo())
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(true, foo())', 5, 'assertEquals("message", true, foo())')
    }

    void testAssertTrueViolation_Backwards() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(foo(), true)
                    assertEquals("message", foo(), true)
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(foo(), true)', 5, 'assertEquals("message", foo(), true)')
    }

    void testAssertFalseViolation() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(false, foo())
                    assertEquals("message", false, foo())
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(false, foo())', 5, 'assertEquals("message", false, foo())')
    }

    void testAssertFalseViolation_Backwards() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(foo(), false)
                    assertEquals("message", foo(), false)
                }
              }
        '''
        assertTwoViolations(SOURCE, 4, 'assertEquals(foo(), false)', 5, 'assertEquals("message", foo(), false)')
    }

    protected Rule createRule() {
        new UseAssertTrueInsteadOfAssertEqualsRule()
    }
}