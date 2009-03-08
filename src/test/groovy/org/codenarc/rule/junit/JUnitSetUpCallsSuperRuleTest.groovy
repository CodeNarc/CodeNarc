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
 * Tests for JUnitSetUpCallsSuperRule
 *
 * @author Chris Mair
 * @version $Revision: 69 $ - $Date: 2009-02-25 22:03:41 -0500 (Wed, 25 Feb 2009) $
 */
class JUnitSetUpCallsSuperRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitSetUpCallsSuper'
    }

    void testApplyTo_SetUpCallsSuperSetUp() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
                super.setUp()
                println 'bad'
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SetUpDoesNotCallSuperSetUp() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
                println 'bad'
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void setUp() {')
    }

    void testApplyTo_SetUpDoesNotCallSuperSetUp_CallsSuperSetUpWithParameters() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
                println 'bad'
                super.setUp('But', 'has', 'parameters')
            }
          }
        '''
        sourceCodePath = 'src/MyTests.groovy'
        assertSingleViolation(SOURCE, 3, 'void setUp() {')
    }

    void testApplyTo_SetUpDoesNotCallSuperSetUp_CallsSuper() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
                println 'bad'
                super.someOtherMethod()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void setUp() {')
    }

    void testApplyTo_SetUpDoesNotCallSuperSetUp_CallsSetUp() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
                println 'bad'
                other.setUp()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void setUp() {')
    }

    void testApplyTo_NonTestFile() {
        final SOURCE = '''
          class MyClass extends TestCase {
            void setUp() {
            }
          }
        '''
        sourceCodePath = 'src/MyController.groovy'
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NonSetUpMethod() {
        final SOURCE = '''
            class MyClass {
                def otherMethod() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SetUpMethodHasParameters() {
        final SOURCE = '''
            class MyClass {
                void setUp(int count, String name) {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoMethodDefinition() {
        final SOURCE = '''
            class MyClass {
              int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    void setUp() {
        super.setUp()
        sourceCodePath = 'MyTest.groovy'
    }

    protected Rule createRule() {
        return new JUnitSetUpCallsSuperRule()
    }
}