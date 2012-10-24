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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryReturnKeywordRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryReturnKeywordRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryReturnKeyword'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = { y++; it }
        	def x = { it }
            def method1(it) {
                y++
                it
            }
            def method2(it) {
                it
            }
            def method3() {
                return { 5 }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testInClosures() {
        final SOURCE = '''
        	def x = { y++; return it }
        	def x = { return it }
        '''
        assertTwoViolations(SOURCE,
                2, 'return it',
                3, 'return it')
    }

    @Test
    void testInMethods() {
        final SOURCE = '''
            def method1(it) {
                y++
                return it
            }
            def method2(it) {
                return it
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'return it',
                7, 'return it')
    }

    protected Rule createRule() {
        new UnnecessaryReturnKeywordRule()
    }
}
