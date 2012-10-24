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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ReturnFromFinallyBlockRule
 *
 * @author Chris Mair
 */
class ReturnFromFinallyBlockRuleTest extends AbstractRuleTestCase {
    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ReturnFromFinallyBlock'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                int myMethod() {
                    if (debug) {
                        return 0                // ok
                    }
                    try {
                        doSomething()
                        return 0                // ok
                    } catch(Exception e) {
                        println 'exception'
                        return -1               // ok
                    } finally {
                        println 'finally'
                        return 99               // BAD
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 15, 'return 99')
    }

    @Test
    void testApplyTo_NestedTryFinally() {
        final SOURCE = '''
            class MyClass {
                int myClosure = {
                    try {
                        doSomething()
                        return 0                // ok
                    } finally {
                        try {
                            // clean up
                            return 88           // BAD
                        } finally {
                            println "ok"
                        }
                        println 'finally'
                        return 99 }             // BAD
                    0                    // ok
                }
            }
        '''
        assertTwoViolations(SOURCE, 10, '88', 15, '99')
    }

    @Test
    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                        'abc'
                    } finally {
                        println 'ok'
                    }
                    'def'
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ReturnFromFinallyBlockRule()
    }

}
