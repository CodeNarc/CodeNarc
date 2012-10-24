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
 * Tests for SynchronizedMethodRule
 *
 * @author Hamlet D'Arcy
 */
class SynchronizedMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SynchronizedMethod'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class SynchronizedMethodRuleTestClass1 {
                synchronized def syncMethod1() {}
                def method2() {}
                synchronized def syncMethod3() {}
                def method4() {}
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'synchronized def syncMethod1() {}',
                5, 'synchronized def syncMethod3() {}')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class SynchronizedMethodRuleTestClass2 {
                def myMethod() {}
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SynchronizedMethodRule()
    }

}
