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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EmptyCatchBlockRule
 *
 * @author Chris Mair
 */
class EmptyCatchBlockRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyCatchBlock'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    try {
                    } catch(MyException e) {
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'catch(MyException e) {')
    }

    @Test
    void testApplyTo_Violation_CatchBlockContainsComment() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                    } catch(MyException e) {
                        // TODO Should do something here
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'catch(MyException e)')
    }

    @Test
    void testApplyTo_NoViolations_IfParameterNameContainsIgnore() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                    } catch(MyException ignore) {
                    }

                    try {
                    } catch(MyException ignored) {
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Violation_IfThereIsNoParameterName() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                    } catch(MyException) {
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 5, 'catch(MyException) {')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                    }
                    catch(Exception t) {
                        println "bad stuff happened"
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new EmptyCatchBlockRule()
    }

}
