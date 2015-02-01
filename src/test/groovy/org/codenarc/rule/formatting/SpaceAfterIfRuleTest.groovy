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
 * Tests for SpaceAfterIfRule
 *
 * @author Chris Mair
  */
class SpaceAfterIfRuleTest extends AbstractRuleTestCase {

    private static final MESSAGE = 'The if keyword within class None is not followed by a single space'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterIf'
    }

    @Test
    void testApplyTo_ProperSpacing_NoViolations() {
        final SOURCE = '''
            if (true) { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SyntheticIf_NoViolations() {
        final SOURCE = '''
            enum MavenScope {
                COMPILE,
                RUNTIME,
                TEST,
                PROVIDED,
                SYSTEM
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IfWithoutSingleSpace_Violation() {
        final SOURCE = '''
            if(true) { }
            if  (true) { }
            if(
                true) { }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'if(true) { }', messageText:MESSAGE],
            [lineNumber:3, sourceLineText:'if  (true) { }', messageText:MESSAGE],
            [lineNumber:4, sourceLineText:'if(', messageText:MESSAGE])
    }

    @Test
    void testApplyTo_KeywordAfterLabel_NoViolations() {
        final SOURCE = '''
            def "sample test"() {
                setup:
                if (true) { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SpaceAfterIfRule()
    }
}
