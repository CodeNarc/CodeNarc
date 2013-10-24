/*
 * Copyright 2011 the original author or authors.
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
 * Tests for CoupledTestCaseRule
 *
 * @author 'Hamlet D'Arcy'
  */
class CoupledTestCaseRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CoupledTestCase'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    MyOtherObject.helperMethod() // OK, not test
                    someTest.helperObject() // OK, not static
                    new MyOtherObject()
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticMethod() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    // violation, static method call to other test
                    MyOtherTest.helperMethod()
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'MyOtherTest.helperMethod()', 'MyOtherTest.helperMethod() invokes a method on another test case. Test cases should not be coupled. Move this method to a helper object')
    }

    @Test
    void testInstantiation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    // violation, instantiation of another test class
                    new MyOtherTest()
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'new MyOtherTest()', 'new MyOtherTest() creates an instance of a test case. Test cases should not be coupled. Move this method to a helper object')
    }

    @Test
    void testStaticReferenceToSameClass_DefaultPackage_NoViolation() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                void testMethod() {
                    def input = MyTest.getResourceAsStream('sample.txt')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticReferenceToSameClass_WithinPackage_NoViolation() {
        final SOURCE = '''
            package com.example

            class MyTest extends GroovyTestCase {
                void testMethod() {
                    def input = MyTest.getResourceAsStream('sample.txt')
                    def otherInput = com.example.MyTest.getResourceAsStream('other.txt')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new CoupledTestCaseRule()
    }
}
