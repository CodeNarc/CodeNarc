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
 * Tests for SpaceAfterIfRule
 *
 * @author Chris Mair
  */
class SpaceAfterIfRuleTest extends AbstractRuleTestCase<SpaceAfterIfRule> {

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
            [line:2, source:'if(true) { }', message:MESSAGE],
            [line:3, source:'if  (true) { }', message:MESSAGE],
            [line:4, source:'if(', message:MESSAGE])
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

    @Override
    protected SpaceAfterIfRule createRule() {
        new SpaceAfterIfRule()
    }
}
