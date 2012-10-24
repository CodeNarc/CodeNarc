/*
 * Copyright 2011 the original author or authors.
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
 * Tests for UnnecessaryBigDecimalInstantiationRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryBigDecimalInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryBigDecimalInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            assert 42G == foo()
            assert 42G == new BigDecimal([] as char[])
            assert 42.10 == new BigDecimal("42.10", 10)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStringConstructor_Decimal() {
        final SOURCE = '''
            new BigDecimal("42.10")
        '''
        assertSingleViolation(SOURCE, 2, 'new BigDecimal("42.10")', 'Can be rewritten as 42.10 or 42.10G')
    }

    @Test
    void testStringConstructor_Integer() {
        final SOURCE = '''
            new BigDecimal("42")
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoubleConstructor() {
        final SOURCE = '''
            new BigDecimal(42.10d)
        '''
        assertSingleViolation(SOURCE, 2, 'new BigDecimal(42.10d)', 'Can be rewritten as 42.1 or 42.1G')
    }

    @Test
    void testIntConstructor() {
        final SOURCE = '''
            new BigDecimal(42i)
            new BigDecimal(42)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testLongConstructor() {
        final SOURCE = '''
            new BigDecimal(42L)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testStaticField() {
        final SOURCE = '''
            class MyClass {
                static final BigDecimal ZERO = new BigDecimal('0.5')
            }
        '''
        assertSingleViolation(SOURCE, 3, "static final BigDecimal ZERO = new BigDecimal('0.5')")
    }

    protected Rule createRule() {
        new UnnecessaryBigDecimalInstantiationRule()
    }
}
