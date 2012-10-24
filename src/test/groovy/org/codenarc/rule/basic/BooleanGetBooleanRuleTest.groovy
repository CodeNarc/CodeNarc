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
 * Tests for BooleanGetBooleanRule
 *
 * @author Hamlet D'Arcy
 */
class BooleanGetBooleanRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BooleanGetBoolean'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            Boolean.getBoolean(value, 1)
            Boolean.getBoolean()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            Boolean.getBoolean(value)
        '''
        assertSingleViolation(SOURCE, 2, 'Boolean.getBoolean(value)', 'Boolean.getBoolean(String) is a confusing API for reading System properties. Prefer the System.getProperty(String) API.')
    }

    protected Rule createRule() {
        new BooleanGetBooleanRule()
    }
}
