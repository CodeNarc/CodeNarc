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
import org.junit.Test

/**
 * Tests for EmptyTryBlockRule
 *
 * @author Chris Mair
 */
class EmptyTryBlockRuleTest extends AbstractRuleTestCase<EmptyTryBlockRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyTryBlock'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                    } catch(MyException e) {
                        e.printStackTrace()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'try {')
    }

    @Test
    void testApplyTo_Violation_TryBlockContainsComment() {
        final SOURCE = '''
            try {
                // TODO Should do something here
            } catch(MyException e) {
                e.printStackTrace()
            }
        '''
        assertSingleViolation(SOURCE, 2, 'try')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    try {
                        doSomething()
                    }
                    catch(Exception t) {
                        println "bad stuff happened"
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Override
    protected EmptyTryBlockRule createRule() {
        new EmptyTryBlockRule()
    }

}
