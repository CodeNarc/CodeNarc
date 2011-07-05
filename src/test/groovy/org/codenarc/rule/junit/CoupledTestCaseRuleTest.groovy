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

/**
 * Tests for CoupledTestCaseRule
 *
 * @author 'Hamlet D'Arcy'
  */
class CoupledTestCaseRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CoupledTestCase'
    }

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

    protected Rule createRule() {
        new CoupledTestCaseRule()
    }
}