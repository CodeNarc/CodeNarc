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
 * Tests for ConstantIfExpressionRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ConstantIfExpressionRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ConstantIfExpression'
    }

    void testApplyTo_True_IsAViolation() {
        final SOURCE = '''
            if (true) { }
            if (Boolean.TRUE) { }
        '''
        assertTwoViolations(SOURCE, 2, 'if (true) { }', 3, 'if (Boolean.TRUE) { }')
    }

    void testApplyTo_False_IsAViolation() {
        final SOURCE = '''
            if (false) { }
            if (Boolean.FALSE) { }
        '''
        assertTwoViolations(SOURCE, 2, 'if (false) { }', 3, 'if (Boolean.FALSE) { }')
    }

    void testApplyTo_Null_IsAViolation() {
        final SOURCE = '''
            if (null) { }
        '''
        assertSingleViolation(SOURCE, 2, 'if (null) { }')
    }

    void testApplyTo_StringLiteral_IsAViolation() {
        final SOURCE = '''
            if ("abc") { }
            if ("") { }
        '''
        assertTwoViolations(SOURCE, 2, 'if ("abc") { }', 3, 'if ("") { }')
    }

    void testApplyTo_NumberLiteral_IsAViolation() {
        final SOURCE = '''
            if (99.9) { }
            if (0) { }
        '''
        assertTwoViolations(SOURCE, 2, 'if (99.9) { }', 3, 'if (0) { }')
    }

    void testApplyTo_MapLiteral_IsAViolation() {
        final SOURCE = '''
            if ([:]) { }
            if ([a:123, b:234]) { }
        '''
        assertTwoViolations(SOURCE, 2, 'if ([:])', 3, 'if ([a:123, b:234])')
    }

    void testApplyTo_ListLiteral_IsAViolation() {
        final SOURCE = '''
            if ([]) { }
            if ([a, 123]) { }
        '''
        assertTwoViolations(SOURCE, 2, 'if ([])', 3, 'if ([a, 123])')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''
            if (z) { }
            if (z+2) { }
            if ("$abc") { }
            if (MAX_VALUE) { }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ConstantIfExpressionRule()
    }

}