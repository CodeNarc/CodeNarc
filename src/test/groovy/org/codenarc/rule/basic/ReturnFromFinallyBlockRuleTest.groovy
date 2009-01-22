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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for ReturnFromFinallyBlockRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ReturnFromFinallyBlockRuleTest extends AbstractRuleTest {
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.id == 'ReturnFromFinallyBlock'
    }

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

    void testApplyTo_NestedTryFinally() {
        final SOURCE = '''
            class MyClass {
                int myMethod() {
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
                    return 0                    // ok
                }
            }
        '''
        assertTwoViolations(SOURCE, 10, 'return 88', 15, 'return 99')
    }

    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                        return 'abc'
                    } finally {
                        println 'ok'
                    }
                    return 'def'
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new ReturnFromFinallyBlockRule()
    }

}