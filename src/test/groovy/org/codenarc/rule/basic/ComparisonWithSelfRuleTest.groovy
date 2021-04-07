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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for ComparisonWithSelfRule
 *
 * @author Chris mair
 */
class ComparisonWithSelfRuleTest extends AbstractRuleTestCase<ComparisonWithSelfRule> {

    private static final MESSAGE = 'Comparing an object to itself'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ComparisonWithSelf'
    }

    @Test
    void testComparisonOperators_NoViolations() {
        final SOURCE = '''
            if (value == true) { }
            while (x <= y) { }
            def v1 = value == ready ? x : y
            def v2 = value <=> other
            def v3 = value >= 23
            def x = 23
            println x == 23
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEqualsMethod_NoViolations() {
        final SOURCE = '''
            if (value.equals(true)) { }
            while (x.equals(y)) { }
            value.equals(true ? x : y)
            value.equals(true ?: x)
            def x = 23
            println x.equals(23)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCompareToMethod_NoViolations() {
        final SOURCE = '''
            if (value.compareTo(other)) { }
            while (x.compareTo(y) < 0) { }
            def v1 = value.compareTo(ready ? x : y)
            def v2 = value.compareTo([1,2,3])
            def v3 = value.compareTo(0)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testComparisonOperators_Violations() {
        final SOURCE = '''
            println isReady = x == x
            if (x != x) { }
            while (x < x) { }
            if (x <= x) { }
            while (x > x) { }
            if (x >= x) { }
            def c = (x <=> x) { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'println isReady = x == x', message:MESSAGE],
            [line:3, source:'if (x != x) { }', message:MESSAGE],
            [line:4, source:'while (x < x) { }', message:MESSAGE],
            [line:5, source:'if (x <= x) { }', message:MESSAGE],
            [line:6, source:'while (x > x) { }', message:MESSAGE],
            [line:7, source:'if (x >= x) { }', message:MESSAGE],
            [line:8, source:'def c = (x <=> x) { }', message:MESSAGE]
        )
    }

    @Test
    void testEqualsMethod_Violation() {
        final SOURCE = '''
            println isReady = x.equals(x)
        '''
        assertSingleViolation(SOURCE, 2, 'println isReady = x.equals(x)', MESSAGE)
    }

    @Test
    void testCompareToMethod_Violation() {
        final SOURCE = '''
            println x.compareTo(x)
        '''
        assertSingleViolation(SOURCE, 2, 'println x.compareTo(x)', MESSAGE)
    }

    @Override
    protected ComparisonWithSelfRule createRule() {
        new ComparisonWithSelfRule()
    }
}
