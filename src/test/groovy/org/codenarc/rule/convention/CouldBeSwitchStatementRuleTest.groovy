/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for CouldBeSwitchStatementRule
 *
 * @author Jenn Strater
 */
class CouldBeSwitchStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'CouldBeSwitchStatement'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            if (x == 1) {
                y = x
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithThreeIfsAndAnElse() {
        final SOURCE = '''
            if (x == 1) {
                y = x
            } else if (x == 2) {
                y = x * 2
            } else if (x == 3) {
                y = x * 3
            } else {
                y = 0
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (x == 1) {', rule.errorMessage)
    }

    @Test
    void testSingleViolationWithThreeIfsNoElse() {
        final SOURCE = '''
            if (x == 1) {
               y = x
            }
            if (x == 2) {
               y = x * 2
            }
            if (x == 3) {
               y = x * 3
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (x == 1) {', rule.errorMessage)
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            if (x == 1) {
               y = x
            } else if (x == 2) {
               y = x * 2
            } else if (x == 3) {
               y = x * 3
            } else {
               y = 0
            }

            if (y instanceof Integer) {
               x = y + 1
            }
            if (y instanceof String) {
               x = y + '1'
            } else if (y instanceof Boolean) {
               x = !y
            } else {
               x = null
            }

            if (p.value instanceof Integer) {
                x = p.value * 2
            } else if (p.value instanceof String) {
                x = p.value + '1'
            } else if (p.value instanceof Boolean) {
                x = !p.value
            }

            if (!x && y) {                      // OK
                doSomething()
            } else if (!x && z) {
                doSomethingElse()
            } else if (!x && i) {
                doAnotherThing()
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:2, sourceLineText: 'if (x == 1) {', messageText: rule.errorMessage],
            [lineNumber:12, sourceLineText: 'if (y instanceof Integer) {', messageText: rule.errorMessage],
            [lineNumber:23, sourceLineText: 'if (p.value instanceof Integer) {', messageText: rule.errorMessage])
    }

    protected Rule createRule() {
        new CouldBeSwitchStatementRule()
    }
}
