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
 * Tests for BigDecimalInstantiationRule
 *
 * @author Chris Mair
 */
class BigDecimalInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BigDecimalInstantiation'
    }

    @Test
    void testApplyTo_double_Violation() {
        final SOURCE = '''
            class MyClass {
                def b1 = new BigDecimal(123.45)
                def b2 = new java.math.BigDecimal(0.26789d)
                def b3 = new BigDecimal(123.45, MathContext.UNLIMITED)
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:3, sourceLineText:'new BigDecimal(123.45)'],
            [lineNumber:4, sourceLineText:'new java.math.BigDecimal(0.26789d)'],
            [lineNumber:5, sourceLineText:'new BigDecimal(123.45, MathContext.UNLIMITED)'])
    }

    @Test
    void testApplyTo_WithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    def b2 = new BigDecimal(12.0001)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
                'def b2 = new BigDecimal(12.0001)',
                'Call to new BigDecimal(12.0001) uses the double constructor and should probably be replaced with new BigDecimal("12.0001")')
    }

    @Test
    void testApplyTo_Violation_NotWithinClass() {
        final SOURCE = '''
            def b1 = new java.math.BigDecimal(0.1)
            def name2 = "abc"
            void calculate() {
                def b2 = new BigDecimal(12345678.987654321d)
            }
        '''
        assertTwoViolations(SOURCE, 2, 'def b1 = new java.math.BigDecimal(0.1)', 5, 'def b2 = new BigDecimal(12345678.987654321d)')
    }

    @Test
    void testApplyTo_BigInteger_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def b1 = new BigDecimal(BigInteger.ONE)
                    def b2 = new BigDecimal(BigInteger.TEN, 3)
                    def b3 = new BigDecimal(BigInteger.TEN, 3, MathContext.UNLIMITED)
                    def b4 = new BigDecimal(BigInteger.TEN, MathContext.UNLIMITED)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_CharArray_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def b1 = new BigDecimal("123.45" as char[])
                    def b2 = new BigDecimal("123.45" as char[], 0, 6)
                    def b3 = new BigDecimal("123.45" as char[], 0, 3, MathContext.UNLIMITED)
                    def b4 = new BigDecimal("123.45" as char[], MathContext.UNLIMITED)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_int_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def b1 = new BigDecimal(99)
                    def b2 = new BigDecimal(12345, MathContext.UNLIMITED)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_long_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def b1 = new BigDecimal(99L)
                    final L = 12345 as long
                    def b2 = new BigDecimal(L, MathContext.UNLIMITED)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_String_NoViolation() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    def b1 = new BigDecimal("12345.67")
                    def b2 = new BigDecimal("0.45", MathContext.UNLIMITED)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_Variable_NoViolation() {
        final SOURCE = '''
            def myMethod() {
                def b1 = new BigDecimal(xyz)
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new BigDecimalInstantiationRule()
    }

}
