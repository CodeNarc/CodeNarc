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
package org.codenarc.rule.dry

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for DuplicateNumberLiteralRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 * @author Nicolas Vuillamy
 */
class DuplicateNumberLiteralRuleTest extends AbstractRuleTestCase<DuplicateNumberLiteralRule> {

    @Test
    void RuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DuplicateNumberLiteral'
    }

    @Test
    void SuccessScenario() {
        final SOURCE = '''
            println 123
            println -17

            def y = 0
            def z = 9876543
            class MyClass {
                def static X = 'xyz'
                def static Y = 'xyz'
                def field = System.getProperty('file.seperator')
                def x = 'foo'
                def y = 11.783
                String a = 'a'
                String b = 'b'
                def method() {
                    method('c', 'd')
                    ('e' == 'f') ? 'g' : 'h'
                    'i' ?: 'j'
                    return 'return'
                }
            }

            println '123'
            println '123'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void AcrossManyMethodCalls() {
        final SOURCE = '''
            println 123
            println 123
            println 123
        '''
        assertTwoViolations(SOURCE, 3, 'println 123', 4, 'println 123')
    }

    @Test
    void MethodCall() {
        final SOURCE = '''
            println 123, 123, 123
        '''
        assertTwoViolations(SOURCE, 2, 'println 123, 123, 123', 2, 'println 123, 123, 123')
    }

    @Test
    void InAList() {
        final SOURCE = '''
            def x = [3, 11.783, 3]
        '''
        assertSingleViolation(SOURCE, 2, 'def x = [3, 11.783, 3]')
    }

    @Test
    void InAMap() {
        final SOURCE = '''
            def y = [x: -99, y: -99]
        '''
        assertSingleViolation(SOURCE, 2, 'def y = [x: -99, y: -99]')
    }

    @Test
    void DoublesAndFloatLiteralsCanBeIgnored() {
        final SOURCE = '''
            println 99.0d
            println 99.0d
            println 99.0f
            println 99.0f
            println 99.0G
            println 99.0G
            println 99G
            println 99G
            println 99.0
            println 99.0
        '''
        rule.ignoreNumbers = '99,99.0,99.0d,99.0f,99.0G'
        assertNoViolations(SOURCE)
    }

    @Test
    void InDeclarations() {
        final SOURCE = '''
            def x = 99
            def y = 99
            x = 11.783
            y = 11.783
        '''
        assertTwoViolations(SOURCE, 3, 'def y = 99', 5, 'y = 11.783')
    }

    @Test
    void InFields() {
        final SOURCE = '''
            class MyClass {
                def x = 67890
                def y = 67890
            }
        '''
        assertSingleViolation(SOURCE, 4, 'def y = 67890')
    }

    @Test
    void InTernary() {
        final SOURCE = '''
            (0.7 == 0.7) ? -5.13 : 'h'
            (0.7 == 12) ? -5.13 : -5.13
        '''
        assertTwoViolations(SOURCE, 2, "(0.7 == 0.7) ? -5.13 : 'h'", 3, '(0.7 == 12) ? -5.13 : -5.13')
    }

    @Test
    void InElvis() {
        final SOURCE = '''
            67890 ?: 67890
        '''
        assertSingleViolation(SOURCE, 2, '67890 ?: 67890')
    }

    @Test
    void InIf() {
        final SOURCE = '''
            if (x == 67890) return x
            else if (y == 67890) return y
            else if (z == 67890) return z
        '''
        assertTwoViolations(SOURCE, 3, 'else if (y == 67890) return y', 4, 'else if (z == 67890) return z')
    }

    @Test
    void InReturn() {
        final SOURCE = '''
            if (true) return 67890
            else return 67890
        '''
        assertSingleViolation(SOURCE, 3, 'else return 67890')
    }

    @Test
    void InInvocation() {
        final SOURCE = '''
            67890.equals(x)
            67890.equals(y)
        '''
        assertSingleViolation(SOURCE, 3, '67890.equals(y)')
    }

    @Test
    void InNamedArgumentList() {
        final SOURCE = '''
            x(b: 11.783)
            y(a: 11.783)
        '''
        assertSingleViolation(SOURCE, 3, 'y(a: 11.783)')
    }

    @Test
    void IgnoreNumbers_IgnoresSingleValue() {
        final SOURCE = '''
            def x = [23, -3.5, 23]
            def y = [37, -7, 37]
        '''
        rule.ignoreNumbers = 23
        assertSingleViolation(SOURCE, 3, 'def y = [37, -7, 37]')
    }

    @Test
    void IgnoreNumbers_IgnoresMultipleValues() {
        final SOURCE = '''
            def x = [0.725, 897.452, 0.725]
            def y = [-97, 11, -97]
        '''
        rule.ignoreNumbers = '0.725,7654, -97'
        assertNoViolations(SOURCE)
    }

    @Test
    void IgnoreNumbers_ByDefaultIgnoresZeroAndOne() {
        final SOURCE = '''
            def x = [0, 12, 1, 34.567, 99, 1, 78, 0, 12.345]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void IgnoreNumbers_InvalidNumber() {
        final SOURCE = '''
            def x = [0.725,0.725, 'xxx']
        '''
        rule.ignoreNumbers = '0.725,xxx, yyy'
        assertNoViolations(SOURCE)
    }

    @Test
    void DuplicateNumberMinimumValue_0_Violation() {
        final SOURCE = '''
            def x = [5, 5, 'xxx']
        '''
        rule.duplicateNumberMinimumValue = 0
        assertSingleViolation(SOURCE, 2, "def x = [5, 5, 'xxx']")
    }

    @Test
    void DuplicateNumberMinimumValue_10_Success() {
        final SOURCE = '''
            def x = [9, 9, 'xxx']
        '''
        rule.duplicateNumberMinimumValue = 10
        assertNoViolations(SOURCE)
    }

    @Test
    void DuplicateNumberMinimumValue_10_ViolationEquals() {
        final SOURCE = '''
            def x = [10, 10, 'xxx']
        '''
        rule.duplicateNumberMinimumValue = 10
        assertSingleViolation(SOURCE, 2, "def x = [10, 10, 'xxx']")
    }

    @Test
    void DuplicateNumberMinimumValue_10_ViolationUpper() {
        final SOURCE = '''
            def x = [11, 11, 'xxx']
        '''
        rule.duplicateNumberMinimumValue = 10
        assertSingleViolation(SOURCE, 2, "def x = [11, 11, 'xxx']")
    }

    @Test
    void DuplicateNumberMinimumValue_LongNumbers() {
        final SOURCE = '''
            class Main {
                public static final List DUPLICATE_NUMBERS = [3317862236, 3317862236]
                public static final List UNIQUE_NUMBERS = [9876543210, 1234567890]
                static void main(String[] args) {
                    println(DUPLICATE_NUMBERS)
                }
            }
        '''
        rule.duplicateNumberMinimumValue = 9
        assertSingleViolation(SOURCE, 3, 'List DUPLICATE_NUMBERS = [3317862236, 3317862236]', '3317862236')
    }

    @Test
    void DuplicateNumberMinimumValue_BigDecimalNumbers() {
        final SOURCE = '''
            class Main {
                private static final List NUMBERS = [12.34567, 999999999.12]
                private static final List TOO_SMALL = [1.35, 8.99]
                static void main(String[] args) {
                    BigDecimal someOtherNumber = 12.34567
                    println(NUMBERS)
                }
            }
        '''
        rule.duplicateNumberMinimumValue = 9
        assertSingleViolation(SOURCE, 6, 'BigDecimal someOtherNumber = 12.34567', '12.34567')
    }

    @Test
    void Enum() {
        final SOURCE = '''
            package com.example

            enum BasicEnum{
                AA(318L),
                AB(319L),
                AC(320L),

                AP(3L)  // There will also be a 3 used as an internal generated index

                final Long id

                private BasicEnum(Long id) {
                    this.id = id
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void InAnnotation_NoViolation() {
        final SOURCE = '''
            @MyAnnotation1NumberValue(11)
            @MyAnnotation2NumberValue(11)
            @MyAnnotation2ListValue(value = [11, 12])
            class MyClass1 {
            }

            @MyAnnotation1ListValue(value = [11])
            @MyAnnotation2NumberValue(11)
            class MyClass2 {
            }

            @MyAnnotation1ListValue(value = [11, 12])
            @MyAnnotation2ListValue(value = [12, 11])
            class MyClass3 {
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Override
    protected DuplicateNumberLiteralRule createRule() {
        new DuplicateNumberLiteralRule()
    }
}
