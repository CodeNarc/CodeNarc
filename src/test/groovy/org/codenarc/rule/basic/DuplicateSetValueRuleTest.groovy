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
 * Tests for DuplicateSetValueRule
 *
 * @author Hamlet D'Arcy
 */
class DuplicateSetValueRuleTest extends AbstractRuleTestCase<DuplicateSetValueRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DuplicateSetValue'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def a = [1, 2, 3, 4, null] as Set
            def b = ['1', '2', '3', null, '4'] as Set
            def c = [1, '1', null] as Set
            def d = [1, 1, 1] as ArrayList
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDuplicateIntegers() {
        final SOURCE = '''
            def a = [1, 2, null, 2, 4] as Set
            def b = [1, 2, 2, 4] as HashSet
            def c = [1, 3, null, 3, 4] as SortedSet
            def d = [1, null, null, 3, 4] as SortedSet
        '''
        assertViolations(SOURCE,
                [line: 2, source: 'def a = [1, 2, null, 2, 4] as Set', message: 'The constant value 2 is duplicated in the Set literal'],
                [line: 3, source: 'def b = [1, 2, 2, 4] as HashSet', message: 'The constant value 2 is duplicated in the Set literal'],
                [line: 4, source: 'def c = [1, 3, null, 3, 4] as SortedSet', message: 'The constant value 3 is duplicated in the Set literal'],
                [line: 5, source: 'def d = [1, null, null, 3, 4] as SortedSet', message: 'The constant value null is duplicated in the Set literal'])
    }

    @Test
    void testDuplicateStrings() {
        final SOURCE = '''
            def e = ['1', '2', '2', null, '4'] as Set
            def f = ['1', '2', '2', null, '4'] as java.util.HashSet
            def g = ['1', '3', '3', null, '4'] as SortedSet
        '''
        assertViolations(SOURCE,
                [line: 2, source: "def e = ['1', '2', '2', null, '4'] as Set", message: "The constant value '2' is duplicated in the Set literal"],
                [line: 3, source: "def f = ['1', '2', '2', null, '4'] as java.util.HashSet", message: "The constant value '2' is duplicated in the Set literal"],
                [line: 4, source: "def g = ['1', '3', '3', null, '4'] as SortedSet", message: "The constant value '3' is duplicated in the Set literal"])
    }

    @Test
    void testDuplicateIntsInCustomType() {
        final SOURCE = '''
            def d = [1, 2, 2, 4] as FooSet
            def h = ['1', '2', '2', '4'] as FooSet
        '''
        assertViolations(SOURCE,
                [line: 2, source: 'def d = [1, 2, 2, 4] as FooSet', message: 'The constant value 2 is duplicated in the Set literal'],
                [line: 3, source: "def h = ['1', '2', '2', '4'] as FooSet", message: "The constant value '2' is duplicated in the Set literal"])
    }

    @Override
    protected DuplicateSetValueRule createRule() {
        new DuplicateSetValueRule()
    }
}
