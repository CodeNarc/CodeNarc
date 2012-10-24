/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule.exceptions

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ThrowErrorRule
 *
 * @author Chris Mair
  */
class ThrowErrorRuleTest extends AbstractRuleTestCase {
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThrowError'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                void myMethod() {
                    throw new Error()
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'throw new Error()')
    }

    @Test
    void testApplyTo_Violation_FullPackageName() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    if (error) {
                        throw new java.lang.Error('something bad')
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, "throw new java.lang.Error('something bad')")
    }

    @Test
    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                        throw new MyException()      // ok
                    } finally {
                        println 'ok'
                    }
                    throw new OtherException()          // ok
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ThrowErrorRule()
    }

}
