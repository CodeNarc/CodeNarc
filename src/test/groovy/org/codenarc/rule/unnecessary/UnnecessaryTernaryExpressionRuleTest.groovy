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
 * Tests for UnnecessaryTernaryExpressionRule
 *
 * @author Chris Mair
  */
class UnnecessaryTernaryExpressionRuleTest extends AbstractRuleTestCase<UnnecessaryTernaryExpressionRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryTernaryExpression'
    }

    @Test
    void testApplyTo_TrueAndFalse_IsAViolation() {
        final SOURCE = '''
            def x = !ready ? true : false
            def y = !ready ? Boolean.TRUE : Boolean.FALSE
        '''
        assertViolations(SOURCE,
            [line:2, source:'def x = !ready ? true : false'],
            [line:3, source:'def y = !ready ? Boolean.TRUE : Boolean.FALSE'])
    }

    @Test
    void testApplyTo_TrueAndFalse_MixedUseOfBooleanClassConstants_IsAViolation() {
        final SOURCE = '''
            def x = !ready ? Boolean.TRUE : false
            def y = !ready ? true : Boolean.FALSE
        '''
        assertTwoViolations(SOURCE, 2, 'def x = !ready ? Boolean.TRUE : false', 3, 'def y = !ready ? true : Boolean.FALSE')
    }

    @Test
    void testApplyTo_FalseAndTrue_IsAViolation() {
        final SOURCE = '''
            def x = !ready ? false : true
            def y = !ready ? Boolean.FALSE : Boolean.TRUE
        '''
        assertTwoViolations(SOURCE, 2, 'def x = !ready ? false : true', 3, 'def y = !ready ? Boolean.FALSE : Boolean.TRUE')
    }

    @Test
    void testApplyTo_ConditionalExpressionIsABoolean_IsAViolation() {
        final SOURCE = '''
            def x
            x = !ready ? true : false
            x = !(y + z) ? Boolean.TRUE : Boolean.FALSE
            x = (y == 99) ? false : true
            x = (y < 99) ? false : Boolean.TRUE
            x = (y <= 99) ? false : true
            x = (y > 99) ? true : false
            x = (y >= 99) ? false : true
            x = (y != 99) ? true : false
            x = (y ==~ /../) ? true : false
            x = (y && z) ? true : false
            x = (y || z) ? true : false
            x = (y || calculate(99)) ? true : false
            x = (addTax(5) + 5 || calculate(99) && ready) ? true : false
        '''
        assertViolations(SOURCE,
            [line:3, source:'x = !ready ? true : false'],
            [line:4, source:'x = !(y + z) ? Boolean.TRUE : Boolean.FALSE'],
            [line:5, source:'x = (y == 99) ? false : true'],
            [line:6, source:'x = (y < 99) ? false : Boolean.TRUE'],
            [line:7, source:'x = (y <= 99) ? false : true'],
            [line:8, source:'x = (y > 99) ? true : false'],
            [line:9, source:'x = (y >= 99) ? false : true'],
            [line:10, source:'x = (y != 99) ? true : false'],
            [line:11, source:'x = (y ==~ /../) ? true : false'],
            [line:12, source:'x = (y && z) ? true : false'],
            [line:13, source:'x = (y || z) ? true : false'],
            [line:14, source:'x = (y || calculate(99)) ? true : false'],
            [line:15, source:'x = (addTax(5) + 5 || calculate(99) && ready) ? true : false']
        )
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreTheSameLiteral_IsAViolation() {
        final SOURCE = '''
            def x = ready ? 123 : 123
            def y = !ready ? "abc" : "abc"
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? 123 : 123', 3, 'def y = !ready ? "abc" : "abc"')
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreBothTrueOrBothFalse_IsAViolation() {
        final SOURCE = '''
            def x = ready ? true : true
            def y = ready ? false : false
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? true : true', 3, 'def y = ready ? false : false')
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreBothNull_IsAViolation() {
        final SOURCE = '''
            def x = ready ? null : null
        '''
        assertSingleViolation(SOURCE, 2, 'def x = ready ? null : null')
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreBothEmptyString_IsAViolation() {
        final SOURCE = '''
            def x = ready ? '' : ""
        '''
        assertSingleViolation(SOURCE, 2, /def x = ready ? '' : ""/)
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreTheSameVariable_IsAViolation() {
        final SOURCE = '''
            def x = ready ? MAX_VALUE : MAX_VALUE
            def y = !ready ? result : result
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? MAX_VALUE : MAX_VALUE', 3, 'def y = !ready ? result : result')
    }

    @Test
    void testApplyTo_TrueAndFalseExpressionsAreTheSameMethodCall_NotAViolation() {
        final SOURCE = '''
            def x = !ready ? process('abc') : process('abc')
            def y = !ready ? increment(x) : increment(x)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ComplexExpressions_NotAViolation() {
        final SOURCE = '''
            transactionTypes.equals('ALL') ?
                myService.findAllWidgetsByPlan{ plan -> valueOne } :
                myService.findAllWidgetsByPlan{ plan -> valueTwo }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ConditionalExpressionIsNotABoolean_NoViolations() {
        final SOURCE = '''
            def x
            x = ready ? true : false
            x = doSomething(23) ? true : false
            x = null ? true : false
            x = 23 + 7 ? true : false
            x = y + 'x' ? true : false
            x = 'abc' =~ /./ ? true : false
            x = y <=> 99 ? true : false
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NotTrueAndFalse_NoViolations() {
        final SOURCE = '''
            def x
            x = !ready ? 1 : 0
            x = !ready ? true : 0
            x = !ready ? 1 : false
            x = !ready ? MY_TRUE : Boolean.FALSE
            x = !ready ? Boolean.TRUE : x+1
            x = !ready ? increment(y) : increment(z)
            x = !ready ? 99 : 98+1
            x = !ready ? MIN_VALUE : MAX_VALUE
            x = !ready ? MAX_VALUE + 1 : MAX_VALUE
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UnnecessaryTernaryExpressionRule createRule() {
        new UnnecessaryTernaryExpressionRule()
    }

}
