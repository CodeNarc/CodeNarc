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
 * Tests for ComparisonOfTwoConstants
 *
 * @author Chris mair
 */
class ComparisonOfTwoConstantsRuleTest extends AbstractRuleTestCase<ComparisonOfTwoConstantsRule> {

    private static final MESSAGE = 'Comparing two constants or constant literals'

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ComparisonOfTwoConstants'
    }

    @Test
    void testComparisonOperators_NoViolations() {
        final SOURCE = '''
            if (value == true) { }
            while (x <= 23) { }
            def v1 = value == [1,2] ? x : y
            def v2 = value <=> 23.45
            def v3 = value >= 23
            println 23 < x
            println Boolean.FALSE == x
            def v4 = [a:1] == [a:x]
            def v5 = [(x):1] == [a:23]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testEqualsMethod_NoViolations() {
        final SOURCE = '''
            if (value.equals(true)) { }
            while (x.equals([1,2,3])) { }
            value.equals(Boolean.TRUE)
            value.equals(1.23)
            def x = 23
            println [a:1,b:2].equals(x)
            return [a,b].equals([1,2])
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCompareToMethod_NoViolations() {
        final SOURCE = '''
            if (value.compareTo(23)) { }
            while (x.compareTo([a]) < 0) { }
            def v1 = value.compareTo(ready ? x : y)
            def v2 = 0.compareTo(x)
            return [a:1].compareTo([a:x])
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testComparisonOperators_Violations() {
        final SOURCE = '''
            println isReady = 23 == 67
            if (Boolean.FALSE != false) { }
            while (23 < 88) { }
            if (0.17 <= 0.99) { }
            while ("abc" > "ddd") { }
            if ([Boolean.FALSE] >= [27]) { }
            def c = ([a:1] <=> [a:2]) { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'println isReady = 23 == 67', message:MESSAGE],
            [line:3, source:'if (Boolean.FALSE != false) { }', message:MESSAGE],
            [line:4, source:'while (23 < 88) { }', message:MESSAGE],
            [line:5, source:'if (0.17 <= 0.99) { }', message:MESSAGE],
            [line:6, source:'while ("abc" > "ddd") { }', message:MESSAGE],
            [line:7, source:'if ([Boolean.FALSE] >= [27]) { }', message:MESSAGE],
            [line:8, source:'def c = ([a:1] <=> [a:2]) { }', message:MESSAGE]
        )
    }

    @Test
    void testEqualsMethod_Violation() {
        final SOURCE = '''
            println isReady = [1,2].equals([3,4])
            return [a:123, b:true].equals(['a':222, b:Boolean.FALSE])
        '''
        assertViolations(SOURCE,
            [line:2, source:'println isReady = [1,2].equals([3,4])', message:MESSAGE],
            [line:3, source:"return [a:123, b:true].equals(['a':222, b:Boolean.FALSE])", message:MESSAGE]
        )
    }

    @Test
    void testCompareToMethod_Violation() {
        final SOURCE = '''
            println cmp = [a:123, b:456].compareTo([a:222, b:567])
            return [a:false, b:true].compareTo(['a':34.5, b:Boolean.TRUE])
        '''
        assertViolations(SOURCE,
            [line:2, source:'println cmp = [a:123, b:456].compareTo([a:222, b:567])', message:MESSAGE],
            [line:3, source:"return [a:false, b:true].compareTo(['a':34.5, b:Boolean.TRUE])", message:MESSAGE])
    }

    @Override
    protected ComparisonOfTwoConstantsRule createRule() {
        new ComparisonOfTwoConstantsRule()
    }
}
