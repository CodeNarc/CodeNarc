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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for DoubleNegativeRule
 *
 * @author Hamlet D'Arcy
 */
class DoubleNegativeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DoubleNegative'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	!true
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleNegatives() {
        final SOURCE = '''
            def x = !!true
            def y = !(!true)
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = !!true', 'The expression (!!true) is a confusing double negative',
                3, 'def y = !(!true)', 'The expression (!!true) is a confusing double negative')
    }

    protected Rule createRule() {
        new DoubleNegativeRule()
    }
}
