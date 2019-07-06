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
import org.junit.Test

/**
 * Tests for ThrowNullPointerExceptionRule
 *
 * @author Chris Mair
  */
class ThrowNullPointerExceptionRuleTest extends AbstractRuleTestCase<ThrowNullPointerExceptionRule> {
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThrowNullPointerException'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                void myMethod() {
                    throw new NullPointerException()
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'throw new NullPointerException()')
    }

    @Test
    void testApplyTo_Violation_FullPackageName() {
        final SOURCE = '''
            class MyClass {
                void myMethod() {
                    if (error) {
                        throw new java.lang.NullPointerException('something bad')
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, "throw new java.lang.NullPointerException('something bad')")
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

    @Override
    protected ThrowNullPointerExceptionRule createRule() {
        new ThrowNullPointerExceptionRule()
    }

}
