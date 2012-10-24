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
 * Tests for UnnecessaryBooleanExpressionRule
 *
 * @author Chris Mair
  */
class UnnecessaryBooleanExpressionRuleTest extends AbstractRuleTestCase {

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
            [lineNumber:2, sourceLineText:'def ready = value && true'],
            [lineNumber:3, sourceLineText:'if (value || true) {'],
            [lineNumber:6, sourceLineText:'def result = value && false'],
            [lineNumber:8, sourceLineText:'ready = true && value'],
            [lineNumber:9, sourceLineText:'if (true || value)'],
            [lineNumber:10, sourceLineText:'result = false && value'])
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
            [lineNumber:2, sourceLineText:'def ready = value && Boolean.TRUE'],
            [lineNumber:3, sourceLineText:'if (value || Boolean.TRUE)'],
            [lineNumber:6, sourceLineText:'def result = value && Boolean.FALSE'],
            [lineNumber:8, sourceLineText:'ready = Boolean.TRUE && value'],
            [lineNumber:9, sourceLineText:'if (Boolean.TRUE || value)'],
            [lineNumber:10, sourceLineText:'result = Boolean.FALSE && value'])
    }

    @Test
    void testApplyTo_AndOr_WithMapLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && [:]
            result = [a:123] || value
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'result = value && [:]'],
            [lineNumber:3, sourceLineText:'result = [a:123] || value'])
    }

    @Test
    void testApplyTo_AndOr_WithListLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && []
            result = [x, y, z] || value
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'result = value && []'],
            [lineNumber:3, sourceLineText:'result = [x, y, z] || value'])
    }

    @Test
    void testApplyTo_AndOr_WithNumberLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && 19
            result = 67.898 || value
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'result = value && 19'],
            [lineNumber:3, sourceLineText:'result = 67.898 || value'])
    }

    @Test
    void testApplyTo_AndOr_WithStringLiteral_IsAViolation() {
        final SOURCE = '''
            result = value && ""
            result = 'abcdef' || value
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'result = value && ""'],
            [lineNumber:3, sourceLineText:"result = 'abcdef' || value"])
    }

    @Test
    void testApplyTo_AndOr_WithNull_IsAViolation() {
        final SOURCE = '''
            result = value && null
            result = null || value
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'result = value && null'],
            [lineNumber:3, sourceLineText:'result = null || value'])
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
            [lineNumber:2, sourceLineText:'def ready = !true'],
            [lineNumber:3, sourceLineText:'if (!false)'],
            [lineNumber:4, sourceLineText:'def result = value && !Boolean.FALSE'],
            [lineNumber:5, sourceLineText:'println !Boolean.TRUE'])
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
            [lineNumber:2, sourceLineText:'def ready = !"abc"'],
            [lineNumber:3, sourceLineText:'if (![])'],
            [lineNumber:4, sourceLineText:'if (![23]) { doSomething() }'],
            [lineNumber:5, sourceLineText:'def result = value && ![a:123]'],
            [lineNumber:6, sourceLineText:'result = !null'])
    }

    @Test
    void testApplyTo_NegatingNonConstant_NoViolations() {
        final SOURCE = '''
            result = !abc
            result = !(x  || y)
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryBooleanExpressionRule()
    }

}
