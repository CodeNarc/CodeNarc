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
 * Tests for JUnitUnnecessaryTearDown
 *
 * @author Chris Mair
  */
class JUnitUnnecessaryTearDownRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'JUnitUnnecessaryTearDown'
    }

    @Test
    void testApplyTo_TearDownOnlyCallsSuperTearDown_Violation() {
        final SOURCE = '''
          class MyTestCase extends TestCase {
            void tearDown() {
                super.tearDown()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void tearDown() {')
    }

    @Test
    void testApplyTo_TearDownCallsSuperTearDownAndSomethingElse() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                super.tearDown()
                println 'bad'
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                println 'bad'
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown_CallsSuper() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                super.someOtherMethod()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown_CallsTearDown() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                other.tearDown()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TearDownMethodHasAfterAnnotation() {
        final SOURCE = '''
          class MyTest extends TestCase {
            @After void tearDown() {
                super.tearDown()
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonTearDownMethod() {
        final SOURCE = '''
            class MyTest extends TestCase {
                def otherMethod() {
                    super.tearDown()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_TearDownMethodHasParameters() {
        final SOURCE = '''
            class MyTest extends TestCase {
                void tearDown(int count, String name) {
                    super.tearDown()
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
        new JUnitUnnecessaryTearDownRule()
    }
}
