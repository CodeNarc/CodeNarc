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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ConfusingTernaryRule
 *
 * @author Hamlet D'Arcy
 */
class ConfusingTernaryRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ConfusingTernary'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            (x == y) ? same : diff
            (x) ? same : diff

            // because of GroovyTruth, there is no inverse of != null
            (x != null) ? diff : same
            (null != x) ? diff : same

            // because of GroovyTruth, there is no inverse of != true
            (x != true) ? diff : same
            (true != x) ? diff : same

            // because of GroovyTruth, there is no inverse of != true
            (x != false) ? diff : same
            (false != x) ? diff : same
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNotEquals() {
        final SOURCE = '''
            (x != y) ? diff : same
        '''
        assertSingleViolation(SOURCE, 2,
                '(x != y) ? diff : same',
                '(x != y) is a confusing negation in a ternary expression. Rewrite as (x == y) and invert the conditions.')
    }

    @Test
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
