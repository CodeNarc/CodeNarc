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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for UnnecessaryCallToSubstringRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCallToSubstringRuleTest extends AbstractRuleTestCase<UnnecessaryCallToSubstringRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryCallToSubstring'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            prop.substring(1)
            prop.substring(0, 1)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            prop.substring(0)
        '''
        assertSingleViolation(SOURCE, 2, 'prop.substring(0)', 'Invoking the String method substring(0) always returns the original value. Method possibly missing 2nd parameter')
    }

    @Override
    protected UnnecessaryCallToSubstringRule createRule() {
        new UnnecessaryCallToSubstringRule()
    }
}
