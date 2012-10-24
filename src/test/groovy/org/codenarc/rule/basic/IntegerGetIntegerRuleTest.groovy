/*
 * Copyright 2011 the original author or authors.
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
 * Tests for IntegerGetIntegerRule
 *
 * @author Hamlet D'Arcy
 */
class IntegerGetIntegerRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IntegerGetInteger'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            Integer.getInteger()
            Integer.getInteger(value, radix, locale)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoViolations() {
        final SOURCE = '''
            Integer.getInteger(value)
            Integer.getInteger(value, radix)
        '''
        assertTwoViolations(SOURCE,
                2, 'Integer.getInteger(value)', 'Integer.getInteger(String) is a confusing API for reading System properties. Prefer the System.getProperty(String) API.',
                3, 'Integer.getInteger(value, radix)', 'Integer.getInteger(String, Integer) is a confusing API for reading System properties. Prefer the System.getProperty(String) API.')
    }

    protected Rule createRule() {
        new IntegerGetIntegerRule()
    }
}
