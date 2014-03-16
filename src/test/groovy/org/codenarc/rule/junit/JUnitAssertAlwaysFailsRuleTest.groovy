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
 * Tests for JUnitAssertAlwaysFailsRule
 *
 * @author Chris Mair
  */
class JUnitAssertAlwaysFailsRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitAssertAlwaysFails'
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
        assertSingleViolation(SOURCE, 4, 'assertTrue(false)')
    }

    @Test
    void testApplyTo_AssertTrue_True() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue(true)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertTrue_ConstantNumber() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue(1234)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertTrue_LiteralsThatEvaluateToFalse_Violations() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue(0)
                    assertTrue('error message', '')
                    assertTrue([])
                    assertTrue('error message', [:])

                    assertTrue(99)          // Not violations
                    assertTrue('abc')
                    assertTrue([123])
                    assertTrue([a:123])
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'assertTrue(0)'],
            [lineNumber:5, sourceLineText:"assertTrue('error message', '')"],
            [lineNumber:6, sourceLineText:'assertTrue([])'],
            [lineNumber:7, sourceLineText:"assertTrue('error message', [:])"])
    }

    @Test
    void testApplyTo_AssertTrue_Variable() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertTrue(myVariable)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertTrue_FalseWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                def myClosure = {
                    assertTrue("This passed!", false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertTrue("This passed!", false)')
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
        assertSingleViolation(SOURCE, 4, 'assertFalse(true)')
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
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertFalse_TrueWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse("This passed!", true)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertFalse("This passed!", true)')
    }

    @Test
    void testApplyTo_AssertFalse_LiteralsThatEvaluateToTrue_Violations() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertFalse(99)
                    assertFalse('error message', 'abc')
                    assertFalse([123])
                    assertFalse('error message', [a:123])

                    assertFalse(0)          // Not violations
                    assertFalse('')
                    assertFalse([])
                    assertFalse([:])
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'assertFalse(99)'],
            [lineNumber:5, sourceLineText:"assertFalse('error message', 'abc')"],
            [lineNumber:6, sourceLineText:'assertFalse([123])'],
            [lineNumber:7, sourceLineText:"assertFalse('error message', [a:123])"])
    }

    // Tests for assertNull

    @Test
    void testApplyTo_AssertNull_ConstantNumber() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(123)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNull(123)')
    }

    @Test
    void testApplyTo_AssertNull_ConstantString() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull('abc')
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, "assertNull('abc')")
    }

    @Test
    void testApplyTo_AssertNull_ConstantBoolean() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNull(false)')
    }

    @Test
    void testApplyTo_AssertNull_LiteralListOrMap() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull([])
                    assertNull([123])
                    assertNull([a:123])
                    assertNull([:])
                }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:4, sourceLineText:'assertNull([])'],
            [lineNumber:5, sourceLineText:'assertNull([123])'],
            [lineNumber:6, sourceLineText:'assertNull([a:123])'],
            [lineNumber:7, sourceLineText:'assertNull([:])'])
    }

    @Test
    void testApplyTo_AssertNull_NonConstant() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull(plugin)
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
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertNull_NullWithMessage() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNull("What?", false)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNull("What?", false)')
    }

    // Tests for assertNotNull

    @Test
    void testApplyTo_AssertNotNull_Null() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(null)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertNotNull(null)')
    }

    @Test
    void testApplyTo_AssertNotNull_NonNullValues() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void testSomething() {
                    assertNotNull(123)
                    assertNotNull('abc')
                    assertNotNull(true)
                    assertNotNull(false)
                    assertNotNull(Boolean.FALSE)
                    assertNotNull([])
                    assertNotNull([123])
                    assertNotNull([:])
                    assertNotNull([a:123])
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
                assertTrue(false)
            }
          }
        '''
        sourceCodePath = 'src/MyController.groovy'
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitAssertAlwaysFailsRule()
    }
}
