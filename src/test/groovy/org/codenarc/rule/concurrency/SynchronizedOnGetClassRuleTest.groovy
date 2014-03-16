/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SynchronizedOnGetClassRule
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedOnGetClassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedOnGetClass'
    }

    @Test
    void testBadSynchronization() {
        final SOURCE = '''
                class MyClass {
                    def Method1() {
                        synchronized(getClass()) { ; }
                    }
                }
            '''
        assertSingleViolation(SOURCE, 4, 'synchronized(getClass()) { ; }')
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
                class MyClass {

                    def Method1() {
                        synchronized(getFoo()) { ; }
                    }
                    def Method2() {
                        synchronized(MyClass) { ; }
                    }
                    def Method3() {
                        synchronized(String.class) { ; }
                    }
                }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleMessagesAreNotCreated() {
        final  SOURCE = '''
            class C {
                def m = {
                    synchronized(getClass()) {
                        // ...
                    }
                }
            } '''
        assertSingleViolation(SOURCE, 4, 'synchronized(getClass())')
    }

    protected Rule createRule() {
        new SynchronizedOnGetClassRule()
    }
}
