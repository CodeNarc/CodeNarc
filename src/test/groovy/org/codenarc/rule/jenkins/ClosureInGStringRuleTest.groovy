/*
 * Copyright 2023 the original author or authors.
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
package org.codenarc.rule.jenkins

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for ClosureInGStringRule
 *
 * @author Daniel Zänker
 */
class ClosureInGStringRuleTest extends AbstractRuleTestCase<ClosureInGStringRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 1
        assert rule.name == 'ClosureInGString'
    }

    @Test
    void testStringInterpolation_NoViolations() {
        final SOURCE = '''
            def x = 42
            def s = "some string ${x}"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNormalClosure_NoViolations() {
        final SOURCE = '''
            def c = { -> x}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClosureInMethodCallInGString_NoViolations() {
        final SOURCE = '''
            List<int> l = [1,2,3]
            def s = "some string ${l.collect { i -> i * i }.toString()}"
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClosureInGString_Violations() {
        final SOURCE = '''
            def x = 42
            def s = "some string ${-> x}"
            def s = "some string ${x} ${-> x}"
        '''
        assertViolations(SOURCE,
            [line: 3, source: 'def s = "some string ${-> x}"', message: 'GString contains a closure. Use variable interpolation instead'],
            [line: 4, source: 'def s = "some string ${x} ${-> x}"', message: 'GString contains a closure. Use variable interpolation instead'])
    }

    @Override
    protected ClosureInGStringRule createRule() {
        new ClosureInGStringRule()
    }
}
