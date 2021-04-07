/*
 * Copyright 2019 the original author or authors.
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
package org.codenarc.rule.convention

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ImplicitClosureParameterRule
 *
 * @author Marcin Erdmann
 */
class ImplicitClosureParameterRuleTest extends AbstractRuleTestCase<ImplicitClosureParameterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ImplicitClosureParameter'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            def validClosure = { parameterName -> def it }
            def validClosureWithMultipleParameters = { first, second -> def it }
            def validClosureWithoutParameters = { -> def it }
            def closureWithNotReferencedImplicitParameter = { println 123 }
            def it
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            (1..10).collect {
                it * 2
            }
        '''
        assertSingleViolation(SOURCE, 3, 'it * 2')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            (1..10).collect {
                it * 2
            }.collect {
                it - 2
            }
        '''
        assertViolations(SOURCE,
            [line:3, source: 'it * 2', message: 'By convention closure parameters should be specified explicitly.'],
            [line:5, source: 'it - 2', message: 'By convention closure parameters should be specified explicitly.'])
    }

    @Test
    void violationsInNestedClosuresShouldBeOnlyReportedOnce() {
        final SOURCE = '''
            2.times {
                (1..10).collect {
                    it * 2
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'it * 2')
    }

    @Test
    void byDefaultUsingItAsParameterNameCausesAViolation() {
        final SOURCE = '''
            (1..10).collect { it -> it * 2 }
        '''
        assertSingleViolation(SOURCE, 2, '(1..10).collect { it -> it * 2 }', 'By convention "it" should not be used as a closure parameter name.')
    }

    @Test
    void canBeConfiguredToAllowItAsParameter() {
        rule.allowUsingItAsParameterName = true
        final SOURCE = '''
            (1..10).collect { it -> it * 2 }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ImplicitClosureParameterRule createRule() {
        new ImplicitClosureParameterRule()
    }
}
