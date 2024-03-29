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
import org.junit.jupiter.api.Test

/**
 * Tests for UseOfNotifyMethodRule
 *
 * @author Hamlet D'Arcy
 */
class UseOfNotifyMethodRuleTest extends AbstractRuleTestCase<UseOfNotifyMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UseOfNotifyMethod'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            notifyAll()
            notify(foo)
            notify foo
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testThisNotify() {
        final SOURCE = '''
            this.notify()
        '''
        assertSingleViolation(SOURCE, 2, 'this.notify()')
    }

    @Test
    void testNotify() {
        final SOURCE = '''
            notify()
        '''
        assertSingleViolation(SOURCE, 2, 'notify()')
    }

    @Test
    void testOther() {
        final SOURCE = '''
            other.notify()
        '''
        assertSingleViolation(SOURCE, 2, 'other.notify()')
    }

    @Override
    protected UseOfNotifyMethodRule createRule() {
        new UseOfNotifyMethodRule()
    }
}
