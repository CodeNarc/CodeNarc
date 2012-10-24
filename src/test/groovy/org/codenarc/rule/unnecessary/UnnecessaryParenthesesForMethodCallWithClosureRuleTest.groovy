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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryParenthesesForMethodCallWithClosureRule
 *
 * @author Marcin Erdmann
  */
class UnnecessaryParenthesesForMethodCallWithClosureRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryParenthesesForMethodCallWithClosure'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	[1, 2, 3].each { println it }
            [1, 2, 3].collect {
                it * 2
            }.any {
                it > 5
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario2() {
        final SOURCE = '''
            (1..10).inject(0) { acc, count ->
                if (delegate."msn$count"?.length() > 0) {
                    acc++
                }
                acc
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            [1,2,3].each() { println it }
        '''
        assertSingleViolation(SOURCE, 2, '[1,2,3].each() { println it }', "Parentheses in the 'each' method call are unnecessary and can be removed.")
    }

    @Test
    void testTwoViolations() {
        final SOURCE = '''
            [1, 2, 3].collect() {
                it * 2
            }.any (
            ) {
                it > 5
            }
        '''
        assertTwoViolations(SOURCE,
                2, '''[1, 2, 3].collect() {''',
                2, '''[1, 2, 3].collect() {''')   // todo: replace violation line number and message
    }

    @Test
    void testSyntheticMethodCall() {
        final SOURCE = '''
            dependencies {
                testCompile "org.springframework:spring-test:$springVersion",
                            ( 'org.spockframework:spock-core:0.5-groovy-1.8' ) {
                                exclude group: 'org.codehaus.groovy'
                            }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNonDetectableViolations() {
        final SOURCE = '''
        	[1, 2, 3].each(/*comment*/) { println it }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryParenthesesForMethodCallWithClosureRule()
    }
}
