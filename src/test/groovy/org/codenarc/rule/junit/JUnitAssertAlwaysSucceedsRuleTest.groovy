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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for JUnitAssertAlwaysSucceedsRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class JUnitAssertAlwaysSucceedsRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitAssertAlwaysSucceeds'
    }

    void testApplyTo_AssertTrue_False() {
        final SOURCE = '''
            void testSomething() {
                assertTrue(false)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_AssertTrue_True() {
        final SOURCE = '''
            void testSomething() {
                assertTrue(true)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertTrue(true)')
    }

    void testApplyTo_AssertTrue_TrueWithMessage() {
        final SOURCE = '''
            void testSomething() {
                assertTrue("This passed!", true)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertTrue("This passed!", true)')
    }

    void testApplyTo_AssertFalse_True() {
        final SOURCE = '''
            void testSomething() {
                assertFalse(true)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_AssertFalse_False() {
        final SOURCE = '''
            void testSomething() {
                assertFalse(false)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertFalse(false)')
    }

    void testApplyTo_AssertFalse_FalseWithMessage() {
        final SOURCE = '''
            void testSomething() {
                assertFalse("This passed!", false)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertFalse("This passed!", false)')
    }

    void testApplyTo_AssertNull_NotNullConstant() {
        final SOURCE = '''
            void testSomething() {
                assertNull(123)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_AssertNull_Variable() {
        final SOURCE = '''
            void testSomething() {
                assertNull(myVariable)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_AssertNull_Null() {
        final SOURCE = '''
            void testSomething() {
                assertNull(null)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertNull(null)')
    }

    void testApplyTo_AssertNull_NullWithMessage() {
        final SOURCE = '''
            void testSomething() {
                assertNull("This passed!", null)
            }
        '''
        assertSingleViolation(SOURCE, 3, 'assertNull("This passed!", null)')
    }

    void testApplyTo_NonTestFile() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void testSomething() {
                assertTrue(true)
            }
          }
        '''
        sourceCodePath = 'src/MyController.groovy'
        assertNoViolations(SOURCE)
    }

    void setUp() {
        super.setUp()
        sourceCodePath = 'MyTest.groovy'
    }

    protected Rule createRule() {
        return new JUnitAssertAlwaysSucceedsRule()
    }
}