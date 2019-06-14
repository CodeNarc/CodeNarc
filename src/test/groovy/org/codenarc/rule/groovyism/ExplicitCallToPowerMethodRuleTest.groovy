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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for ExplicitCallToPowerMethodRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitCallToPowerMethodRuleTest extends AbstractRuleTestCase<ExplicitCallToPowerMethodRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitCallToPowerMethod'
    }

    @Test
    void testSuccessScenario() {
        rule.ignoreThisReference = true
        final SOURCE = '''
            a ** b
            a.power()
            a.power(a, b)
            power(a)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolation() {
        final SOURCE = '''
            a.power(b)
        '''
        assertSingleViolation(SOURCE, 2, 'a.power(b)', 'Explicit call to a.power(b) method can be rewritten as a ** (b)')
    }

    @Override
    protected ExplicitCallToPowerMethodRule createRule() {
        new ExplicitCallToPowerMethodRule()
    }
}
