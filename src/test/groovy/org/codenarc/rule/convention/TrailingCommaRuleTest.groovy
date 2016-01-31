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
 * Tests for TrailingCommaRule
 *
 * @author Yuriy Chulovskyy
 */
class TrailingCommaRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'TrailingComma'
        assert rule.checkList
        assert rule.checkMap
    }

    @Test
    void testNoListViolations() {
        final SOURCE = '''
            int[] array1 = []
            int[] array2 = [
                           ]
            int[] array3 = [1,2,3]
            int[] array4 = [1,
                           2,
                           3,
                          ]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoListViolationsOnCheckListFalse() {
        final SOURCE = '''
            int[] array = [1,
                           2
                          ]
        '''
        rule.checkList = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleListViolation() {
        final SOURCE = '''
            int[] array = [1,
                           2,
                           3
                          ]
        '''
        assertSingleViolation(SOURCE, 2, 'int[] array = [1,', 'List should contain trailing comma.')
    }

    @Test
    void testMultipleListViolations() {
        final SOURCE = '''
            int[] array1 = [1
                           ]
            int[] array2 = [1,
                            2
                           ]
        '''
        assertTwoViolations(SOURCE,
                2, 'int[] array1 = [1', 'List should contain trailing comma.',
                4, 'int[] array2 = [1,', 'List should contain trailing comma.')
    }

    @Test
    void testNoMapViolations() {
        final SOURCE = '''
            def map1 = [a:1]
            def map2 = [
                       ]
            def map3 = [a:1, b:2]
            def map4 = [a:1,
                        b:2,
                        ]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoMapViolationsOnCheckMapFalse() {
        final SOURCE = '''
            def map = [a:1
                      ]
        '''
        rule.checkMap = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleMapViolation() {
        final SOURCE = '''
            def map = [a:1,
                       b:2
                      ]
        '''
        assertSingleViolation(SOURCE, 2, 'def map = [a:1,', 'Map should contain trailing comma.')
    }

    @Test
    void testMultipleMapViolations() {
        final SOURCE = '''
            def map1 = [a:1
                           ]
            def map2 = [a:1,
                        b:2
                        ]
        '''
        assertTwoViolations(SOURCE,
                2, 'def map1 = [a:1', 'Map should contain trailing comma.',
                4, 'def map2 = [a:1,', 'Map should contain trailing comma.')
    }

    protected Rule createRule() {
        new TrailingCommaRule()
    }
}
