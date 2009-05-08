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
 * Tests for ThrowExceptionFromFinallyBlockRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ThrowExceptionFromFinallyBlockRuleTest extends AbstractRuleTest {
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThrowExceptionFromFinallyBlock'
    }

    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                int myMethod() {
                    if (debug) {
                        throw new Exception()   // ok
                    }
                    try {
                        doSomething()
                        throw new Exception()   // ok
                    } catch(Exception e) {
                        println 'exception'
                        throw new Exception()   // ok
                    } finally {
                        println 'finally'
                        throw new Exception()   // BAD
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 15, 'throw new Exception()')
    }

    void testApplyTo_NestedTryFinally() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                        doSomething()
                        throw new Exception()           // ok
                    } finally {
                        try {
                            // clean up
                            throw new Exception('A')    // BAD
                        } finally {
                            println "ok"
                        }
                        println 'finally'
                        throw new Exception('B') }     // BAD
                        throw new Exception()          // ok
                }
            }
        '''
        assertTwoViolations(SOURCE, 10, "throw new Exception('A')", 15, "throw new Exception('B')")
    }

    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                        throw new Exception()      // ok
                    } finally {
                        println 'ok'
                    }
                    throw new Exception()          // ok
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new ThrowExceptionFromFinallyBlockRule()
    }

}