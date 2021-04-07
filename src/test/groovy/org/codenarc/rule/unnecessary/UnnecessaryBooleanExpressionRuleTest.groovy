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
import org.junit.Test

/**
 * Tests for UnnecessaryBooleanExpressionRule
 *
 * @author Chris Mair
  */
class UnnecessaryBooleanExpressionRuleTest extends AbstractRuleTestCase<UnnecessaryBooleanExpressionRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryBooleanExpression'
    }

    @Test
    void testApplyTo_AndOr_WithTrueOrFalse_IsAViolation() {
        final SOURCE = '''
            def ready = value && true
            if (value || true) {
                println 'ok'
            }
            def result = value && false

            ready = true && value
            if (true || value) { println 'ok' }
            result = false && value
        '''
        assertViolations(SOURCE,
            [line:2, source:'def ready = value && true'],
            [line:3, source:'if (value || true) {'],
            [line:6, source:'def result = value && false'],
            [line:8, source:'ready = true && value'],
            [line:9, source:'if (true || value)'],
            [line:10, source:'result = false && value'])
    }

    @Test
    void testApplyTo_AndOr_WithBooleanTrueOrFalse_IsAViolation() {
        final SOURCE = '''
            def ready = value && Boolean.TRUE
            if (value || Boolean.TRUE) {
                println 'ok'
            }
            def result = value && Boolean.FALSE

            ready = Boolean.TRUE && value
            if (Boolean.TRUE || value) { println 'ok' }
            result = Boolean.FALSE && value
        '''
        assertViolations(SOURCE,
            [line:2, source:'def ready = value && Boolean.TRUE'],
            [line:3, source:'if (value || Boolean.TRUE)'],
            [line:6, source:'def result = value && Boolean.FALSE'],
            [line:8, source:'ready = Boolean.TRUE && value'],
            [line:9, source:'if (Boolean.TRUE || value)'],
            [line:10, source:'result = Boolean.FALSE && value'])
    }

    @Test
    void testApplyTo_AndOr_WithMapLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && [:]
            result = [a:123] || value
        '''
        assertViolations(SOURCE,
            [line:2, source:'result = value && [:]'],
            [line:3, source:'result = [a:123] || value'])
    }

    @Test
    void testApplyTo_AndOr_WithListLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && []
            result = [x, y, z] || value
        '''
        assertViolations(SOURCE,
            [line:2, source:'result = value && []'],
            [line:3, source:'result = [x, y, z] || value'])
    }

    @Test
    void testApplyTo_AndOr_WithNumberLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && 19
            result = 67.898 || value
        '''
        assertViolations(SOURCE,
            [line:2, source:'result = value && 19'],
            [line:3, source:'result = 67.898 || value'])
    }

    @Test
    void testApplyTo_AndOr_WithStringLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && ""
            result = 'abcdef' || value
        '''
        assertViolations(SOURCE,
            [line:2, source:'result = value && ""'],
            [line:3, source:"result = 'abcdef' || value"])
    }

    @Test
    void testApplyTo_AndOr_WithNull_IsAViolation() {
        final SOURCE = '''
            result = value && null
            result = null || value
        '''
        assertViolations(SOURCE,
            [line:2, source:'result = value && null'],
            [line:3, source:'result = null || value'])
    }

    @Test
    void testApplyTo_AndOr_NotWithTrueOrFalse_NoViolations() {
        final SOURCE = '''
            def ready = value && other
            if (value || hasElements()) {
                println 'ok'
            }
            def result = value && count
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_OtherOperators_WithTrueOrFalse_NoViolations() {
        final SOURCE = '''
            def ready = value == true
            if (value != true) { println 'ok' }
            def result = value < false
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NegatingABoolean_IsAViolation() {
        final SOURCE = '''
            def ready = !true
            if (!false) { println 'ok' }
            def result = value && !Boolean.FALSE
            println !Boolean.TRUE
        '''
        assertViolations(SOURCE,
            [line:2, source:'def ready = !true'],
            [line:3, source:'if (!false)'],
            [line:4, source:'def result = value && !Boolean.FALSE'],
            [line:5, source:'println !Boolean.TRUE'])
    }

    @Test
    void testApplyTo_NegatingAConstantLiteral_IsAViolation() {
        final SOURCE = '''
            def ready = !"abc"
            if (![]) { println 'ok' }
            if (![23]) { doSomething() }
            def result = value && ![a:123]
            result = !null
        '''
        assertViolations(SOURCE,
            [line:2, source:'def ready = !"abc"'],
            [line:3, source:'if (![])'],
            [line:4, source:'if (![23]) { doSomething() }'],
            [line:5, source:'def result = value && ![a:123]'],
            [line:6, source:'result = !null'])
    }

    @Test
    void testApplyTo_NegatingNonConstant_NoViolations() {
        final SOURCE = '''
            result = !abc
            result = !(x  || y)
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessaryBooleanExpressionRule createRule() {
        new UnnecessaryBooleanExpressionRule()
    }

}
