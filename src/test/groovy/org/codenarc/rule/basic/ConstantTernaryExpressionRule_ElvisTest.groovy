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
import org.junit.Test

/**
 * Tests for ConstantTernaryExpressionRule handling of the "Elvis" operator.
 *
 * @see ConstantTernaryExpressionRuleTest
 *
 * @author Chris Mair
 */
class ConstantTernaryExpressionRule_ElvisTest extends AbstractRuleTestCase {

    @Test
    void testApplyTo_True_IsAViolation() {
        final SOURCE = '''
            def x = true ?: 0
            def y
            def z = Boolean.TRUE ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = true ?: 0', 4, 'def z = Boolean.TRUE ?: 0')
    }

    @Test
    void testApplyTo_False_IsAViolation() {
        final SOURCE = '''
            def x = false ?: 0
            println 'ok'
            def y = Boolean.FALSE ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = false ?: 0', 4, 'def y = Boolean.FALSE ?: 0')
    }

    @Test
    void testApplyTo_Null_IsAViolation() {
        final SOURCE = '''
            def x = null ?: 0
        '''
        assertSingleViolation(SOURCE, 2, 'def x = null ?: 0')
    }

    @Test
    void testApplyTo_StringLiteral_IsAViolation() {
        final SOURCE = '''
            def x = "abc" ?: 0
            def y = "" ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = "abc" ?: 0', 3, 'def y = "" ?: 0')
    }

    @Test
    void testApplyTo_NumberLiteral_IsAViolation() {
        final SOURCE = '''
            def x = 99.9 ?: 0
            def y = 0 ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = 99.9 ?: 0', 3, 'def y = 0 ?: 0')
    }

    @Test
    void testApplyTo_MapLiteral_IsAViolation() {
        final SOURCE = '''
            def x = [:] ?: 0
            def y = [a:123, b:456] ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = [:] ?: 0', 3, 'def y = [a:123, b:456] ?: 0')
    }

    @Test
    void testApplyTo_ListLiteral_IsAViolation() {
        final SOURCE = '''
            def x = [] ?: 0
            def y = [a, 456] ?: 0
        '''
        assertTwoViolations(SOURCE, 2, 'def x = [] ?: 0', 3, 'def y = [a, 456] ?: 0')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def x1 = z ?: 0
                def x2 = (z+2) ?: 0
                def x3 = "$abc" ?: 0
                def x4 = MAX_VALUE ?: 0
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ConstantTernaryExpressionRule()
    }

}
