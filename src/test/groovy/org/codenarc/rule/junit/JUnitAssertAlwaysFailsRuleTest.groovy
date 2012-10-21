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

/**
 * Tests for JUnitAssertAlwaysFailsRule
 *
 * @author Chris Mair
  */
class JUnitAssertAlwaysFailsRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitAssertAlwaysFails'
    }

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
