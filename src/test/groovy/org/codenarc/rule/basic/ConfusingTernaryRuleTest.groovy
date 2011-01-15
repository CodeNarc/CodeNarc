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
 * Tests for ConfusingTernaryRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class ConfusingTernaryRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ConfusingTernary"
    }

    void testSuccessScenario() {
        final SOURCE = '''
            (x == y) ? same : diff
            (x) ? same : diff
        '''
        assertNoViolations(SOURCE)
    }

    void testNotEquals() {
        final SOURCE = '''
            (x != y) ? diff : same
        '''
        assertSingleViolation(SOURCE, 2,
                '(x != y) ? diff : same',
                '(x != y) is a confusing negation in a ternary expression. Rewrite as (x == y) and invert the conditions.')
    }

    void testNot() {
        final SOURCE = '''
            (!x) ? diff : same
        '''
        assertSingleViolation(SOURCE, 2,
                '(!x) ? diff : same',
                '(!x) is a confusing negation in a ternary expression. Rewrite as (x) and invert the conditions.')
    }

    protected Rule createRule() {
        new ConfusingTernaryRule()
    }
}