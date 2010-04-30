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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessaryTernaryExpressionRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnnecessaryTernaryExpressionRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryTernaryExpression'
    }

    void testApplyTo_TrueAndFalse_IsAViolation() {
        final SOURCE = '''
            def x = ready ? true : false
            def y = ready ? Boolean.TRUE : Boolean.FALSE
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? true : false', 3, 'def y = ready ? Boolean.TRUE : Boolean.FALSE')
    }

    void testApplyTo_TrueAndFalse_MixedUseOfBooleanClassConstants_IsAViolation() {
        final SOURCE = '''
            def x = ready ? Boolean.TRUE : false
            def y = ready ? true : Boolean.FALSE
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? Boolean.TRUE : false', 3, 'def y = ready ? true : Boolean.FALSE')
    }

    void testApplyTo_FalseAndTrue_IsAViolation() {
        final SOURCE = '''
            def x = ready ? false : true
            def y = ready ? Boolean.FALSE : Boolean.TRUE
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? false : true', 3, 'def y = ready ? Boolean.FALSE : Boolean.TRUE')
    }

    void testApplyTo_TrueAndFalseExpressionsAreTheSame_IsAViolation() {
        final SOURCE = '''
            def x = ready ? 123 : 123
            def y = ready ? increment(x) : increment(x)
        '''
        assertTwoViolations(SOURCE, 2, 'def x = ready ? 123 : 123', 3, 'def y = ready ? increment(x) : increment(x)')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            def x = ready ? 1 : 0
            x = ready ? true : 0
            x = ready ? 1 : false
            x = ready ? MY_TRUE : Boolean.FALSE
            x = ready ? Boolean.TRUE : x+1
            x = ready ? increment(y) : increment(z)
            x = ready ? 99 : 98+1
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new UnnecessaryTernaryExpressionRule()
    }

}