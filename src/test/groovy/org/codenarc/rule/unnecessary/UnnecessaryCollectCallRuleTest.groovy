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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryCollectCallRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCollectCallRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryCollectCall'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
                // OK, is not method call in closure
                [1, 2, 3].collect { it * it }

                // OK, is not collect call
                [1, 2, 3].mapMethod { it.multiply(5) }

                // OK, is not one argument/closure call
                [1, 2, 3].collect(5) 

                // OK is not single statement closure
                [1, 2, 3].collect { println it; it.multiply(5) }

                // OK is not single parameter closure
                [1, 2, 3].collect { a, b -> a.multiply(b) }

                // OK method call references closure parameters
                [1, 2, 3].collect { it.multiply(it) }

                // OK, chained methods, too complex to analyze (maybe implement later?)
                [1, 2, 3].collect { it.multiply(2).multiply(it   ) }
                ["1", "2", "3"].collect { it.bytes.foo(it) }
                [1, 2, 3].collect { it.multiply(2).multiply(4) }

                // should be written this way:
                [1, 2, 3]*.multiply(2)
                ["1", "2", "3"]*.bytes
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSimpleCaseImplicitItParameter() {
        final SOURCE = '''
            assert [1, 2, 3].collect {
                it.multiply(2)
            }
        '''
        assertSingleViolation(SOURCE, 2, '[1, 2, 3].collect', 'The call to collect could probably be rewritten as a spread expression: [1, 2, 3]*.multiply(2)')
    }

    @Test
    void testSimpleCaseNamedParameter() {
        final SOURCE = '''
            assert [1, 2, 3].collect { x -> 
                x.multiply(2)
            }
        '''
        assertSingleViolation(SOURCE, 2, '[1, 2, 3].collect', 'The call to collect could probably be rewritten as a spread expression: [1, 2, 3]*.multiply(2)')
    }

    @Test
    void testPropertyExpression() {
        final SOURCE = '''
            ["1", "2", "3"].collect {
                it.bytes
            }
        '''
        assertSingleViolation(SOURCE, 2, '["1", "2", "3"].collect', 'The call to collect could probably be rewritten as a spread expression: [1, 2, 3]*.bytes')
    }
    protected Rule createRule() {
        new UnnecessaryCollectCallRule()
    }
}
