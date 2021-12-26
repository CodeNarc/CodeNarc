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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UseCollectNestedRule
 *
 * @author Joachim Baumann
 * @author Chris Mair
 */
class UseCollectNestedRuleTest extends AbstractRuleTestCase<UseCollectNestedRule> {

    @Test
    void test_InnerCollectResultTransformedIntoNonCollection_NoViolation() {
        final SOURCE = '''
            // The collect() call is just one part of a larger expression; the result may be transformed into a different type
            [['a', 'b', 'c'], ['d', 'e']].collect { 'x' + it.collect { it + 'y' } }
            
            // The result from the collect() call is the object of another method call; the result may be a different type
            [['a', 'b', 'c'], ['d', 'e']].collect { it.collect { it + 'y' }.join(' ') }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UseCollectNested'
    }

    @Test
    void testProperUsageOfCollect_NoViolations() {
        final SOURCE = '''
            def list = [1, 2, [3, 4, 5, 6], [7]]

            println list.collect { elem ->
                if (elem instanceof List) [elem, elem * 2].collect {it * 2}
                else elem * 2
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
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

    @Test
    void testBugFix_CannotCastVariableExpression_NoViolations() {
        final SOURCE = '''
            class MyBuilder {
                void execute(Closure antClosure) {
                    Closure converter = {File file -> file.toURI().toURL() }
                    List<URL> classpathUrls = fullClasspath.collect(converter)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected UseCollectNestedRule createRule() {
        new UseCollectNestedRule()
    }
}
