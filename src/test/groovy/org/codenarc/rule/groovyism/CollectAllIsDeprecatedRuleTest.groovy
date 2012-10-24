/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for CollectAllIsDeprecatedRule
 *
 * @author Joachim Baumann
 */
class CollectAllIsDeprecatedRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CollectAllIsDeprecated'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def list = [1, 2, [3, 4, [5, 6]], 7]
            list.collectNested { it * 2 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            def list = [1, 2, [3, 4, [5, 6]], 7]
            list.collectAll { it * 2 }
            list.collectAll([8, 9]) { it * 2 }
        '''
        //assertSingleViolation(SOURCE, 3, 'list.collectAll { it * 2 }', CollectAllIsDeprecatedRule.MESSAGE)
        assertTwoViolations(SOURCE,
                3, 'list.collectAll', CollectAllIsDeprecatedRule.MESSAGE,
                4, 'list.collectAll', CollectAllIsDeprecatedRule.MESSAGE)
    }

    protected Rule createRule() {
        new CollectAllIsDeprecatedRule()
    }
}
