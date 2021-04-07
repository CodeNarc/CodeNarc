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
import org.junit.Test

/**
 * Tests for SpaceAfterForRule
 *
 * @author Chris Mair
  */
class SpaceAfterForRuleTest extends AbstractRuleTestCase<SpaceAfterForRule> {

    private static final MESSAGE = 'The for keyword within class None is not followed by a single space'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterFor'
    }

    @Test
    void testApplyTo_ProperSpacing_NoViolations() {
        final SOURCE = '''
            for (name in names) { }
            for (int i=0; i < 10; i++) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_WithoutSingleSpace_Violation() {
        final SOURCE = '''
            for(name in names) { }
            for  (int i=0; i < 10; i++) { }
            for(
                String name: names) { }
        '''
        assertViolations(SOURCE,
            [line:2, source:'for(name in names) { }', message:MESSAGE],
            [line:3, source:'for  (int i=0; i < 10; i++) { }', message:MESSAGE],
            [line:4, source:'for(', message:MESSAGE])
    }

    @Test
    void testApplyTo_KeywordAfterLabel_NoViolations() {
        final SOURCE = '''
            def "sample test"() {
                setup:
                for (name in names) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected SpaceAfterForRule createRule() {
        new SpaceAfterForRule()
    }
}
