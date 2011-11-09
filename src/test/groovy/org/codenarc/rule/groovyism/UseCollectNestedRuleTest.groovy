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

/**
 * Tests for UseCollectNestedRule
 *
 * @author Joachim Baumann
 */
class UseCollectNestedRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UseCollectNested'
    }

    void testSuccessScenario() {
        final SOURCE = '''
            def list = [1, 2, [3, 4, 5, 6], [7]]

            println list.collect { elem ->
                if (elem instanceof List) [elem, elem * 2].collect {it * 2}
                else elem * 2
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testViolations() {
        final SOURCE = '''
            def list = [1, 2, [3, 4, 5, 6], [7]]

            println list.collect { elem ->
                if (elem instanceof List)
                    elem.collect {it *2} // violation
                else elem * 2
            }

            println list.collect([8]) {
                if (it instanceof List)
                    it.collect {it *2} // violation
                else it * 2
            }
        '''
        assertTwoViolations(SOURCE,
                 6, 'elem.collect {it *2} // violation', UseCollectNestedRule.MESSAGE,
                12, 'it.collect {it *2} // violation', UseCollectNestedRule.MESSAGE)
    }

    protected Rule createRule() {
        new UseCollectNestedRule()
    }
}
