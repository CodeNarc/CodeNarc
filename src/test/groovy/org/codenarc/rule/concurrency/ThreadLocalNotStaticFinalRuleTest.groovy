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
 * Tests for ThreadLocalNotStaticFinalRule.
 *
 * @author Hamlet D'Arcy
 */
class ThreadLocalNotStaticFinalRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThreadLocalNotStaticFinal'
    }

    @Test
    void testApplyTo_Violation_StaticOrFinalButNotBoth() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass1 {
                private static ThreadLocal local1 = new ThreadLocal()
                private final ThreadLocal local2 = new ThreadLocal()

            }
        '''
        assertTwoViolations(SOURCE,
                3, 'private static ThreadLocal local1 = new ThreadLocal()',
                4, 'private final ThreadLocal local2 = new ThreadLocal()')
    }

    @Test
    void testApplyTo_Violation_NotFinalOrStatic() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass1 {
                static ThreadLocal local1 = new ThreadLocal()
                final protected ThreadLocal local2 = new ThreadLocal()
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'ThreadLocal local1 = new ThreadLocal()', 'The ThreadLocal field local1 is not final',
                4, 'protected ThreadLocal local2 = new ThreadLocal()', 'The ThreadLocal field local2 is not static')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass3 {
                private static final ThreadLocal local1 = new ThreadLocal()
                private static final ThreadLocal local2 = new ThreadLocal()
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolationsInnerClass() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass4 {
                static class ThreadLocalNotStaticFinalInnerClass4 {
                    private static final ThreadLocal local1 = new ThreadLocal()
                    private static final ThreadLocal local2 = new ThreadLocal()
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ThreadLocalNotStaticFinalRule()
    }

}

