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
import org.junit.Test

/**
 * Tests for JUnitAssertAlwaysSucceedsRule
 *
 * @author Chris Mair
 */
class JUnitAssertAlwaysSucceedsRuleTest extends AbstractRuleTestCase<JUnitAssertAlwaysSucceedsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitAssertAlwaysSucceeds'
    }

    // Tests for assertTrue()

    @Test
    void testApplyTo_AssertTrue_False() {
        final SOURCE = '''
            class MyTestCase extends TestCase {
                void testSomething() {
                    assertTrue(false)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertTrue_True() {
        final SOURCE = '''
            class MyTestCase extends TestCase {
                void testSomething() {
                    assertTrue(true)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertTrue(true)')
    }

    @Test
    void testApplyTo_AssertTrue_TrueWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue("This passed!", true)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertTrue("This passed!", true)')
    }

    @Test
    void testApplyTo_AssertTrue_LiteralsThatEvaluateToTrue_Violations() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue(99)          // Not violations
                    assertTrue('error message', 'abc')
                    assertTrue([123])
                    assertTrue('error message', [a:123])

                    assertTrue(0)           // Not violations
                    assertTrue('error message', '')
                    assertTrue([])
                    assertTrue('error message', [:])
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'assertTrue(99)'],
            [line:5, source:"assertTrue('error message', 'abc')"],
            [line:6, source:'assertTrue([123])'],
            [line:7, source:"assertTrue('error message', [a:123])"])
    }

    // Tests for assertFalse()

    @Test
    void testApplyTo_AssertFalse_True() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse(true)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertFalse_False() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse(false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertFalse(false)')
    }

    @Test
    void testApplyTo_AssertFalse_FalseWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse("This passed!", false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertFalse("This passed!", false)')
    }

    @Test
    void testApplyTo_AssertFalse_LiteralsThatEvaluateToFalse_Violations() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse(0)
                    assertFalse('error message', '')
                    assertFalse([])
                    assertFalse('error message', [:])

                    assertFalse(99)          // Not violations
                    assertFalse('abc')
                    assertFalse([123])
                    assertFalse([a:123])
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'assertFalse(0)'],
            [line:5, source:"assertFalse('error message', '')"],
            [line:6, source:'assertFalse([])'],
            [line:7, source:"assertFalse('error message', [:])"])
    }

    // Tests for assertNull()

    @Test
    void testApplyTo_AssertNull_NotNullConstant() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(123)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertNull_Variable() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(myVariable)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertNull_Null() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(null)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNull(null)')
    }

    @Test
    void testApplyTo_AssertNull_NullWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull("This passed!", null)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNull("This passed!", null)')
    }

    // Tests for assertNotNull

    @Test
    void testApplyTo_AssertNotNull_ConstantNumber() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(123)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNotNull(123)')
    }

    @Test
    void testApplyTo_AssertNotNull_ConstantString() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull('abc')
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, "assertNotNull('abc')")
    }

    @Test
    void testApplyTo_AssertNotNull_ConstantBoolean() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNotNull(false)')
    }

    @Test
    void testApplyTo_AssertNotNull_LiteralListOrMap() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull([])
                    assertNotNull([123])
                    assertNotNull([a:123])
                    assertNotNull([:])
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'assertNotNull([])'],
            [line:5, source:'assertNotNull([123])'],
            [line:6, source:'assertNotNull([a:123])'],
            [line:7, source:'assertNotNull([:])'])
    }

    @Test
    void testApplyTo_AssertNotNull_NonConstant() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(plugin)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertNotNull_Null() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(null)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    // Tests for non-Test files

    @Test
    void testApplyTo_NonTestFile() {
        final SOURCE = '''
          class MyClass {
            void testSomething() {
                assertTrue(true)
            }
          }
        '''
        sourceCodePath = 'src/MyController.groovy'
        assertNoViolations(SOURCE)
    }

    @Override
    protected JUnitAssertAlwaysSucceedsRule createRule() {
        new JUnitAssertAlwaysSucceedsRule()
    }
}
