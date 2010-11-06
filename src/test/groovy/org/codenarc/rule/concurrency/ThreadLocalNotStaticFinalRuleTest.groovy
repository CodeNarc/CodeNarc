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

/**
 * Tests for ThreadLocalNotStaticFinalRule.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ThreadLocalNotStaticFinalRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ThreadLocalNotStaticFinal'
    }

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

    void testApplyTo_Violation_NotFinalOrStatic() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass1 {
                ThreadLocal local1 = new ThreadLocal()
                protected ThreadLocal local2 = new ThreadLocal()
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'ThreadLocal local1 = new ThreadLocal()',
                4, 'protected ThreadLocal local2 = new ThreadLocal()')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            class ThreadLocalNotStaticFinalClass3 {
                private static final ThreadLocal local1 = new ThreadLocal()
                private static final ThreadLocal local2 = new ThreadLocal()
            }'''
        assertNoViolations(SOURCE)
    }

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
        return new ThreadLocalNotStaticFinalRule()
    }

}

