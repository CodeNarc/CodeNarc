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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for SpaceAfterWhileRule
 *
 * @author Chris Mair
  */
class SpaceAfterWhileRuleTest extends AbstractRuleTestCase<SpaceAfterWhileRule> {

    private static final MESSAGE = 'The while keyword within class None is not followed by a single space'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterWhile'
    }

    @Test
    void testApplyTo_ProperSpacing_NoViolations() {
        final SOURCE = '''
            while (true) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WithoutSingleSpace_Violation() {
        final SOURCE = '''
            while(true) { }
            while  (true) { }
            while(
                true) { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'while(true) { }', message:MESSAGE],
            [line:3, source:'while  (true) { }', message:MESSAGE],
            [line:4, source:'while(', message:MESSAGE])
    }

    @Test
    void testApplyTo_KeywordAfterLabel_NoViolations() {
        final SOURCE = '''
            def "sample test"() {
                when:
                stack.push(elem)

                then:
                while (true) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceAfterWhileRule createRule() {
        new SpaceAfterWhileRule()
    }
}
