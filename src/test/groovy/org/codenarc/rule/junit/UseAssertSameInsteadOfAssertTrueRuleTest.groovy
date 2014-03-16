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
import org.junit.Test

/**
 * Tests for UseAssertSameInsteadOfAssertTrueRule
 *
 * @author Hamlet D'Arcy
  */
class UseAssertSameInsteadOfAssertTrueRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UseAssertSameInsteadOfAssertTrue'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
    @Test
                void testMethod() {
                    assertEquals(1, foo())
                    assertTrue(foo())
                    assertTrue(x)
                    assertTrue('message', x)
                    assertTrue('message', foo())
                    assertSame(foo(), foo())
                    assertTrue(foo() > bar())
                    assertTrue(foo().is(bar(), baz()))
                    assertTrue(foo().is(bar()), x, y)
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAssertSame() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertTrue(foo().is(bar()))
                    assertTrue("message", foo().is(bar()))
                }
              }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertTrue(foo().is(bar()))',
                5, 'assertTrue("message", foo().is(bar()))')   
    }

    @Test
    void testNotAssertSame() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertFalse(foo().is(bar()))
                    assertFalse("message", foo().is(bar()))
                }
              }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertFalse(foo().is(bar()))',
                5, 'assertFalse("message", foo().is(bar()))')
    }

    protected Rule createRule() {
        new UseAssertSameInsteadOfAssertTrueRule()
    }
}
