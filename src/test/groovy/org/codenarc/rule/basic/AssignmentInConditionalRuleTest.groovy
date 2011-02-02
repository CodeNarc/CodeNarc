/*
 * Copyright 2010 the original author or authors.
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
import org.codenarc.rule.Rule

/**
 * Tests for AssignmentInConditionalRule
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class AssignmentInConditionalRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "AssignmentInConditional"
    }

    void testSuccessScenario() {
        final SOURCE = '''
            if (value == true) {
            }
            while (value == true) {
            }
            value == true ? x : y
            value == true ?: x
        '''
        assertNoViolations(SOURCE)
    }

    void testIfStatement() {
        final SOURCE = '''
            if ((value = true)) {
                // should be ==
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if ((value = true))', 'Assignment used as conditional value, which always results in true. Use the == operator instead')
    }

    void testWhileStatement() {
        final SOURCE = '''
            while (value = true) {
                // should be ==
            }
        '''
        assertSingleViolation(SOURCE, 2, 'while (value = true)', 'Assignment used as conditional value, which always results in true. Use the == operator instead')
    }

    void testElvis() {
        final SOURCE = '''
            (value = true) ?: x
        '''
        assertSingleViolation(SOURCE, 2, '(value = true) ?: x', 'Assignment used as conditional value, which always results in true. Use the == operator instead')
    }

    void testTernary() {
        final SOURCE = '''
            (value = true) ? x : y
        '''
        assertSingleViolation(SOURCE, 2, '(value = true) ? x : y', 'Assignment used as conditional value, which always results in true. Use the == operator instead')
    }

    protected Rule createRule() {
        new AssignmentInConditionalRule()
    }
}