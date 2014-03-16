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
 * Tests for SpaceAfterSwitchRule
 *
 * @author Chris Mair
  */
class SpaceAfterSwitchRuleTest extends AbstractRuleTestCase {

    private static final MESSAGE = 'The switch keyword within class None is not followed by a single space'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterSwitch'
    }

    @Test
    void testApplyTo_ProperSpacing_NoViolations() {
        final SOURCE = '''
            switch (x) {
                case 1: println 'one'
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SwitchWithoutSingleSpace_Violation() {
        final SOURCE = '''
            switch(x) {
                case 1: println 'one'
            }
            switch  (x) {
                case 1: println 'one'
            }
            switch(
                x) {
                case 1: println 'one'
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText:'switch(x) {', messageText:MESSAGE],
            [lineNumber:5, sourceLineText:'switch  (x) {', messageText:MESSAGE],
            [lineNumber:8, sourceLineText:'switch(', messageText:MESSAGE])
    }

    protected Rule createRule() {
        new SpaceAfterSwitchRule()
    }
}
