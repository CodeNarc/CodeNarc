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
 * Tests for JUnitTearDownCallsSuperRule
 *
 * @author Chris Mair
  */
class JUnitTearDownCallsSuperRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitTearDownCallsSuper'
    }

    @Test
    void testApplyTo_TearDownCallsSuperTearDown() {
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
          class MyTestCase extends TestCase {
            void tearDown() {
                println 'bad'
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void tearDown() {')
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown_CallsSuperTearDownWithParameters() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                println 'bad'
                super.tearDown('But', 'has', 'parameters')
            }
          }
        '''
        sourceCodePath = 'src/MyTests.groovy'
        assertSingleViolation(SOURCE, 3, 'void tearDown() {')
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown_CallsSuper() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                println 'bad'
                super.someOtherMethod()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void tearDown() {')
    }

    @Test
    void testApplyTo_TearDownDoesNotCallSuperTearDown_CallsTearDown() {
        final SOURCE = '''
          class MyTest extends TestCase {
            void tearDown() {
                println 'bad'
                other.tearDown()
            }
          }
        '''
        assertSingleViolation(SOURCE, 3, 'void tearDown() {')
    }

    @Test
    void testApplyTo_NonTestClass() {
        final SOURCE = '''
          class MyClass {
            void tearDown() {
            }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonTestFile_TearDownMethodHasAfterAnnotation() {
        final SOURCE = '''
          class MyTest extends TestCase {
            @After void tearDown() {  }
          }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NonTearDownMethod() {
        final SOURCE = '''
            class MyTest extends TestCase {
                def otherMethod() {
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
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoMethodDefinition() {
        final SOURCE = '''
            class MyTest extends TestCase {
              int count
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitTearDownCallsSuperRule()
    }
}
