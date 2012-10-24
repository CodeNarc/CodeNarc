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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ExplicitCallToPlusMethodRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitCallToPlusMethodRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitCallToPlusMethod'
    }

    @Test
    void testSuccessScenario() {
        rule.ignoreThisReference = true
        final SOURCE = '''
        	a + b
            a.plus()
            a.plus(a, b)
            plus(a)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testChainedCall() {
        rule.ignoreThisReference = false
        final SOURCE = '''
            value?.toBigDecimal()?.plus(100000)
            value?.plus(100000) 
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolation() {
        final SOURCE = '''
            a.plus(b)
        '''
        assertSingleViolation(SOURCE, 2, 'a.plus(b)', 'Explicit call to a.plus(b) method can be rewritten as a + (b)')
    }

    protected Rule createRule() {
        new ExplicitCallToPlusMethodRule()
    }
}
