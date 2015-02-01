/*
 * Copyright 2012 the original author or authors.
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
 * Tests for ConstantAssertExpressionRule
 *
 * @author Chris Mair
 */
class ConstantAssertExpressionRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ConstantAssertExpression'
    }

    @Test
    void testApplyTo_True_IsAViolation() {
        final SOURCE = '''
            assert true
            assert Boolean.TRUE, 'bad stuff'
        '''
        assertTwoViolations(SOURCE,
            2, 'assert true', 'true',
            3, 'assert Boolean.TRUE', 'Boolean.TRUE')
    }

    @Test
    void testApplyTo_False_IsAViolation() {
        final SOURCE = '''
            assert false
            assert Boolean.FALSE
        '''
        assertTwoViolations(SOURCE,
            2, 'assert false', 'false',
            3, 'assert Boolean.FALSE', 'Boolean.FALSE')
    }

    @Test
    void testApplyTo_Null_IsAViolation() {
        final SOURCE = '''
            assert null
        '''
        assertSingleViolation(SOURCE, 2, 'assert null', 'null')
    }

    @Test
    void testApplyTo_StringLiteral_IsAViolation() {
        final SOURCE = '''
            assert 'abc'
            assert ""
        '''
        assertTwoViolations(SOURCE,
            2, "assert 'abc'", 'abc',
            3, 'assert ""', '')
    }

    @Test
    void testApplyTo_NumberLiteral_IsAViolation() {
        final SOURCE = '''
            class MyClass {
                def doStuff() {
                    assert 99.9, 'bad stuff'
                    assert 0
                }
            }
        '''
        assertTwoViolations(SOURCE,
            4, "assert 99.9, 'bad stuff'", ['99.9', 'MyClass'],
            5, 'assert 0', ['0', 'MyClass'])
    }

    @Test
    void testApplyTo_MapLiteral_IsAViolation() {
        final SOURCE = '''
            assert [:]
            assert [a:123, b:456]
        '''
        assertTwoViolations(SOURCE,
            2, 'assert [:]', '[:]',
            3, 'assert [a:123, b:456]', '[a:123, b:456]')
    }

    @Test
    void testApplyTo_ListLiteral_IsAViolation() {
        final SOURCE = '''
            assert []
            assert [a, 456]
        '''
        assertTwoViolations(SOURCE,
            2, 'assert []', '[]',
            3, 'assert [a, 456]', '[a, 456]')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    assert x
                    assert y, 'bad stuff'
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ConstantAssertExpressionRule()
    }

}
