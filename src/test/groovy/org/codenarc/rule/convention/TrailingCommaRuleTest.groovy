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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for TrailingCommaRule
 *
 * @author Yuriy Chulovskyy
 */
class TrailingCommaRuleTest extends AbstractRuleTestCase<TrailingCommaRule> {

    private static final String LIST_ERROR = 'List should contain trailing comma.'
    private static final String MAP_ERROR = 'Map should contain trailing comma.'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'TrailingComma'
        assert rule.checkList
        assert rule.checkMap
        assert rule.ignoreSingleElementList
        assert rule.ignoreSingleElementMap
    }

    // Tests for Lists

    @Test
    void testList_NoViolations() {
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
    void testList_NoViolationsOnCheckListFalse() {
        final SOURCE = '''
            int[] array = [1,
                           2
                          ]
        '''
        rule.checkList = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testList_SingleViolation() {
        final SOURCE = '''
            int[] array = [1,
                           2,
                           3
                          ]
        '''
        assertSingleViolation(SOURCE, 2, 'int[] array = [1,', LIST_ERROR)
    }

    @Test
    void testList_MultipleViolations() {
        final SOURCE = '''
            int[] array1 = [1, 2
                           ]
            int[] array2 = [1,
                            2
                           ]
        '''
        assertTwoViolations(SOURCE,
                2, 'int[] array1 = [1, 2', LIST_ERROR,
                4, 'int[] array2 = [1,', LIST_ERROR)
    }

    @Test
    void testList_SingleElementList_ignoreSingleElementListTrue_NoViolations() {
        final SOURCE = '''
            int[] array1 = [[1,
                           2,
                           ]
                          ]
            String[] array2 = ["a" +
                    "b" +
                    "c"]
        '''
        rule.ignoreSingleElementList = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testList_SingleElementList_ignoreSingleElementListFalse_Violations() {
        final SOURCE = '''
            int[] array1 = [[1,
                           2,
                           ]
                          ]
            String[] array2 = [
                    "a" +
                    "b" +
                    "c"]
        '''
        rule.ignoreSingleElementList = false
        assertViolations(SOURCE,
                [line:2, source:'int[] array1 = [[1,', message:LIST_ERROR],
                [line:6, source:'String[] array2 = [', message:LIST_ERROR])
    }

    @Test
    void testList_NoViolationForInlineListInAnAnnotation() {
        final SOURCE = '''
         @ToString(
            ignoreNulls = false,
            excludes = ['bear', 'raccoon']
         )
         class TestClass{}
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testList_NoViolationForMultiLineListInAnAnnotation() {
        final SOURCE = '''
         @ToString(
            ignoreNulls = false,
            excludes = [
                'bear',
                'raccoon',
            ]
         )
         class TestClass{}
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testList_SingleViolationInAnAnnotation() {
        final SOURCE = '''
         @ToString(
            ignoreNulls = false,
            excludes = [
                'bear',
                'raccoon'
            ]
         )
         class TestClass{}
        '''

        assertSingleViolation(SOURCE, 4, 'excludes = [', LIST_ERROR)
    }

    // Tests for Maps

    @Test
    void testMap_NoViolations() {
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
    void testMap_NoViolationsOnCheckMapFalse() {
        final SOURCE = '''
            def map = [a:1
                      ]
        '''
        rule.checkMap = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testMap_SingleViolation() {
        final SOURCE = '''
            def map = [a:1,
                       b:2
                      ]
        '''
        assertSingleViolation(SOURCE, 2, 'def map = [a:1,', MAP_ERROR)
    }

    @Test
    void testMap_MultipleViolations() {
        final SOURCE = '''
            def map1 = [a:1, b:2
                           ]
            def map2 = [a:1,
                        b:2
                        ]
        '''
        assertTwoViolations(SOURCE,
                2, 'def map1 = [a:1', MAP_ERROR,
                4, 'def map2 = [a:1,', MAP_ERROR)
    }

    @Test
    void testMap_NoViolationForMapInConstructorOverMultipleLines() {
        final SOURCE = '''
        Person person = new Person(first: 'Jane', last: 'Doe', address: '123 Main Street', city: 'Anywhere',
           country: 'USA')
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testMap_SingleElementMap_ignoreSingleElementMapTrue_NoViolations() {
        final SOURCE = '''
            def map1 = [a:[1,
                           2,
                           ]
                          ]
            def map2 = [a:"a" +
                    "b" +
                    "c"]
            def map3 = [a:1

                ]
        '''
        rule.ignoreSingleElementMap = true
        assertNoViolations(SOURCE)
    }

    @Test
    void testMap_SingleElementMap_ignoreSingleElementMapFalse_Violations() {
        final SOURCE = '''
            def map1 = [a:1
                          ]
            def map2 = [
                    a:"a" +
                    "b" +
                    "c"]
        '''
        rule.ignoreSingleElementMap = false
        assertViolations(SOURCE,
                [line:2, source:'def map1', message:MAP_ERROR],
                [line:4, source:'def map2 = [', message:MAP_ERROR])
    }

    @Override
    protected TrailingCommaRule createRule() {
        new TrailingCommaRule()
    }
}
