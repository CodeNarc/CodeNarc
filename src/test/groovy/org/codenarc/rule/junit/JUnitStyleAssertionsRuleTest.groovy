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
 * Tests for JUnitStyleAssertionsRule
 *
 * @author Hamlet D'Arcy
  */
class JUnitStyleAssertionsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JUnitStyleAssertions'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assert 1 == 2
                    assert 1 == 2 : 'message'

                    assertTrue()
                    foo.assertTrue(x)
                    foo.assertTrue('message', x)

                    assertFalse()
                    foo.assertFalse(x)
                    foo.assertFalse('message', x)

                    assertEquals()
                    assertEquals(x)
                    assertEquals(x, x, x, x)
                    foo.assertEquals('message', 1, 2)

                    assertNull()
                    assertNull(1, 2, 3)
                    foo.assertNull(x)
                    foo.assertNull('message', x)

                    assertNotNull()
                    assertNotNull(1, 2, 3)
                    foo.assertNotNull(x)
                    foo.assertNotNull('message', x)
                }
             }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTrueOnThis() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertTrue(x)
                    assertTrue('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertTrue(x)',
                5, "assertTrue('message', x)")
    }

    @Test
    void testTrueOnAssert() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    Assert.assertTrue(x)
                    Assert.assertTrue('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'Assert.assertTrue(x)',
                5, "Assert.assertTrue('message', x)")
    }

    @Test
    void testFalseOnThis() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertFalse(x)
                    assertFalse('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertFalse(x)',
                5, "assertFalse('message', x)")
    }

    @Test
    void testFalseOnAssert() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    Assert.assertFalse(x)
                    Assert.assertFalse('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'Assert.assertFalse(x)',
                5, "Assert.assertFalse('message', x)")
    }

    @Test
    void testNullOnThis() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertNull(x)
                    assertNull('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertNull(x)',
                5, "assertNull('message', x)")
    }

    @Test
    void testNullOnAssert() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    Assert.assertNull(x)
                    Assert.assertNull('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'Assert.assertNull(x)',
                5, "Assert.assertNull('message', x)")
    }

    @Test
    void testNotNullOnThis() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertNotNull(x)
                    assertNotNull('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertNotNull(x)',
                5, "assertNotNull('message', x)")
    }

    @Test
    void testNotNullOnAssert() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    Assert.assertNotNull(x)
                    Assert.assertNotNull('message', x)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'Assert.assertNotNull(x)',
                5, "Assert.assertNotNull('message', x)")
    }

    @Test
    void testAssertEqualsOnThis() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    assertEquals(x, y)
                    assertEquals('message', x, y)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'assertEquals(x, y)',
                5, "assertEquals('message', x, y)")
    }

    @Test
    void testAssertEqualsOnAssert() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    Assert.assertEquals(x, y)
                    Assert.assertEquals('message', x, y)
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'Assert.assertEquals(x, y)',
                5, "Assert.assertEquals('message', x, y)")
    }

    protected Rule createRule() {
        new JUnitStyleAssertionsRule()
    }
}
