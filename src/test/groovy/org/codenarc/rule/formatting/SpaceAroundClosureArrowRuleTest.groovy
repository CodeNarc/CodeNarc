/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for SpaceAroundClosureArrowRule
 *
 * @author Chris Mair
 */
class SpaceAroundClosureArrowRuleTest extends AbstractRuleTestCase<SpaceAroundClosureArrowRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAroundClosureArrow'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            def closure1 = { -> }
            def closure2 = { count -> }
            def closure3 = { count        ->                 }
            def closure4 = { count\t->\t}
            def closure5 = { count ->
            }
            def closure6 = { count, name
             ->
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testKnownLimitation_ClosureErrorOnSeparateLine_NoViolations() {
        final SOURCE = '''
            def closure5 = { count, name
             ->}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            def closure1 = {->}
            def closure2 = { ->}
            def closure3 = {-> }
            def closure4 = { count-> println 123 }
            def closure5 = { count, name ->println 123 }
            def closure6 = { ->println 123
             }
        '''
        final MESSAGE = 'The closure arrow (->) within class None is not surrounded by a space or whitespace'
        assertViolations(SOURCE,
            [line:2, source:'def closure1 = {->}', message:MESSAGE],
            [line:3, source:'def closure2 = { ->}', message:MESSAGE],
            [line:4, source:'def closure3 = {-> }', message:MESSAGE],
            [line:5, source:'def closure4 = { count-> println 123 }', message:MESSAGE],
            [line:6, source:'def closure5 = { count, name ->println 123 }', message:MESSAGE],
            [line:7, source:'def closure6 = { ->println 123', message:MESSAGE])
    }

    @Override
    protected SpaceAroundClosureArrowRule createRule() {
        new SpaceAroundClosureArrowRule()
    }
}
