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
 * Tests for JUnitUnnecessarySetUp
 *
 * @author Chris Mair
  */
class JUnitUnnecessarySetUpRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JUnitUnnecessarySetUp'
    }

    @Test
    void testApplyTo_SetUpOnlyCallsSuperSetUp_Violation() {
        final SOURCE = '''
          class MyTestCase extends TestCase {
            void setUp() {
                super.setUp()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void setUp() {')
    }

    @Test
    void testApplyTo_SetUpCallsSuperSetUpAndSomethingElse() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void setUp() {
                super.setUp()
                println 'bad'
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpDoesNotCallSuperSetUp() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void setUp() {
                println 'bad'
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpDoesNotCallSuperSetUp_CallsSuper() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void setUp() {
                super.someOtherMethod()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpDoesNotCallSuperSetUp_CallsSetUp() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void setUp() {
                other.setUp()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpMethodHasBeforeAnnotation() {
        final SOURCE = '''
          class MyTest extends TestCase {
            @Before void setUp() {
                super.setUp()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonSetUpMethod() {
        final SOURCE = '''
            class MyTest extends TestCase {
                def otherMethod() {
                    super.setUp()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SetUpMethodHasParameters() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void setUp(int count, String name) {
                    super.setUp()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonTestClass() {
        final SOURCE = '''
            class MyClass {
              int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitUnnecessarySetUpRule()
    }
}
