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
 * Tests for UseAssertNullInsteadOfAssertEqualsRule
 *
 * @author Hamlet D'Arcy
  */
class UseAssertNullInsteadOfAssertEqualsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UseAssertNullInsteadOfAssertEquals'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
    @Test
                void testMethod() {
                    assertEquals(1, foo())
                    assertTrue(foo())
                    assertTrue('message', foo())
                    assertSame(foo(), foo())
                    assertTrue(foo() > bar())
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNullInAssertEquals() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(null, foo())
                    assertEquals(foo(), null)
                }
              }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertEquals(null, foo())',
                5, 'assertEquals(foo(), null)')
    }

    @Test
    void testNullInAssertEqualsWithMessage() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals('message', null, foo())
                    assertEquals('message', foo(), null)
                }
              }
        '''
        assertTwoViolations(SOURCE,
                4, "assertEquals('message', null, foo())",
                5, "assertEquals('message', foo(), null)")   
    }

    protected Rule createRule() {
        new UseAssertNullInsteadOfAssertEqualsRule()
    }
}
