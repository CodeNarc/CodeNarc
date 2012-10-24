/*
 * Copyright 2012 the original author or authors.
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
 * Tests for AssertWithinFinallyBlockRule
 *
 * @author Chris Mair
 */
class AssertWithinFinallyBlockRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssertWithinFinallyBlock'
    }

    @Test
    void testApplyTo_FinallyWithAssert_Violation() {
        final SOURCE = '''
            class MyClass {
                boolean ready = true
                int myMethod() {
                    try {
                        doSomething()
                    } finally {
                        assert ready   // BAD
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 8, 'assert ready', ['finally block', 'MyClass'])
    }

    @Test
    void testApplyTo_NestedTryFinallyWithAssert_Violation() {
        final SOURCE = '''
            class MyClass {
                boolean ready = false
                def myClosure = {
                    try {
                        doSomething()
                    } finally {
                        try {
                            assert ready    // BAD
                        } finally {
                            println "ok"
                        }
                        assert ready     // BAD
                    }
                }
            }
        '''
        assertTwoViolations(SOURCE, 9, 'assert ready', 13, 'assert ready')
    }

    @Test
    void testApplyTo_FinallyBlockWithoutAssert_NoViolation() {
        final SOURCE = '''
            class MyClass {
                boolean ready = true
                def myMethod() {
                    try {
                        assert ready      // ok
                    }
                    finally {
                        println 'ok'
                    }
                    assert ready          // ok
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertInMethod_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod(int count) {
                    assert count > 0
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertInTry_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod(int count) {
                    try {
                        assert count > 0
                    }
                    finally {
                        println count
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AssertInCatch_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod(int count) {
                    try {
                        doStuff(count)
                    }
                    catch(Exception e) {
                        assert count > 0
                    }
                    finally {
                        println count
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new AssertWithinFinallyBlockRule()
    }

}
