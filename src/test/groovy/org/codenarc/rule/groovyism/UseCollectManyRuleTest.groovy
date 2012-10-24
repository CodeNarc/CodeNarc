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
 * Tests for UseCollectManyRule
 *
 * @author Joachim Baumann
 */
class UseCollectManyRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UseCollectMany'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def l = [1, 2, 3, 4]
            l.collect{ [it, it*2] }
            l.flatten()

            def res = l.collect{ [it, it*2] }
            res.flatten()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            def l = [1, 2, 3, 4]
            l.collect{ [it, it*2] }.flatten()
        '''
        assertSingleViolation SOURCE, 3, 'l.collect{ [it, it*2] }.flatten()', UseCollectManyRule.MESSAGE
    }

    protected Rule createRule() {
        new UseCollectManyRule()
    }
}
