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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SpaceAfterForRule
 *
 * @author Chris Mair
  */
class SpaceAfterForRuleTest extends AbstractRuleTestCase {

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
            [lineNumber:2, sourceLineText:'for(name in names) { }', messageText:MESSAGE],
            [lineNumber:3, sourceLineText:'for  (int i=0; i < 10; i++) { }', messageText:MESSAGE],
            [lineNumber:4, sourceLineText:'for(', messageText:MESSAGE])
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

    protected Rule createRule() {
        new SpaceAfterForRule()
    }
}
