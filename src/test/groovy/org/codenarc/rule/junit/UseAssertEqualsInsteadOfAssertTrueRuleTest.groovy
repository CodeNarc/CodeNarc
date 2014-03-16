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
 * Tests for UseAssertEqualsInsteadOfAssertTrueRule
 *
 * @author Per Junel
 * @author Hamlet D'Arcy
  */
class UseAssertEqualsInsteadOfAssertTrueRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UseAssertEqualsInsteadOfAssertTrue'
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
                    assertTrue('message', obj.foo() != obj.bar())

                   assertFalse(foo() == bar())
                   assertFalse('message', obj.foo() == obj.bar())

                   assertTrue(foo() != bar())
                   assertTrue('message', obj.foo() != obj.bar())
                }
              }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEqualsInTrueTest() {
        final SOURCE = '''
                 class MyTestCase extends TestCase {
                    void testMethod() {
                        assertTrue(foo() == bar())
                        assertTrue('message', foo() == bar())
                    }
                  }
            '''
        assertTwoViolations(SOURCE,
                4, 'assertTrue(foo() == bar())',
                5, "assertTrue('message', foo() == bar())")
    }

    @Test
    void testNotEqualsInAssertFalseTest() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                   assertFalse(foo() != bar())
                   assertFalse('message', obj.foo() != obj.bar())
                }
              }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertFalse(foo() != bar())',
                5, "assertFalse('message', obj.foo() != obj.bar())")
    }

    protected Rule createRule() {
        new UseAssertEqualsInsteadOfAssertTrueRule()
    }
}
