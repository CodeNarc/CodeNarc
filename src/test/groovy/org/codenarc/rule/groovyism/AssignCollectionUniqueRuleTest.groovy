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
import org.junit.Test

/**
 * Tests for AssignCollectionUniqueRule
 *
 * @author Nick Larson
 * @author Juan Vazquez
 * @author Jon DeJong
 * @author Chris Mair
 */
class AssignCollectionUniqueRuleTest extends AbstractRuleTestCase<AssignCollectionUniqueRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignCollectionUnique'
    }

    @Test
    void test_unique_NotAssigned_NoViolations() {
        final SOURCE = '''
            myList.unique()
            myList.unique(true)
            myList.unique() { it }
            myList.unique(2) { it }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_unique_Assignment_NotTheFirstMethodCall_NoViolations() {
        final SOURCE = '''
            def allPaths = resultsMap.values().unique()
            myList.findAll{ it < 50 }.unique()
            def w = getMyList().unique().findAll { x < 1 }
            def x = myList.foo.unique().findAll { x < 1 }
            def y = myList.foo().unique().findAll { x < 1 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_unique_NoArgs_Assignment_Violation() {
        final SOURCE = '''
            def x = myList.unique()
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.unique()')
    }

    @Test
    void test_unique_OneArg_Closure_Assignment_Violation() {
        final SOURCE = '''
            def x = myList.unique() { it }
            def y = myList.unique { it % 2 }
        '''
        assertViolations(SOURCE,
                [line:2, source:'def x = myList.unique()'],
                [line:3, source:'def y = myList.unique { it % 2 }'])
    }

    @Test
    void test_unique_OneArg_Comparator_Assignment_Violation() {
        final SOURCE = '''
            def comparator = { o1, o2 -> o1 <=> o2 }
            def x = myList.unique(comparator)
        '''
        assertSingleViolation(SOURCE, 3, 'def x = myList.unique(comparator)')
    }

    @Test
    void test_unique_OneArg_false_Assignment_NoViolations() {
        final SOURCE = '''
            def x = myList.unique(false)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_unique_OneArg_true_Assignment_Violation() {
        final SOURCE = '''
            def x = myList.unique(true)
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.unique(true)')
    }

    @Test
    void test_unique_TwoArgs_FirstArgIsTrue_Assignment_Violation() {
        final SOURCE = '''
            def comparator = { o1, o2 -> o1 <=> o2 }
            def x = myList.unique(true, comparator)
            def y = myList.unique(true) { it }
        '''
        assertViolations(SOURCE,
                [line:3, source:'def x = myList.unique(true, comparator)'],
                [line:4, source:'def y = myList.unique(true) { it }'])
    }

    @Test
    void test_unique_TwoArgs_FirstArgIsFalse_Assignment_NoViolations() {
        final SOURCE = '''
            def comparator = { o1, o2 -> o1 <=> o2 }
            def x = myList.unique(false, comparator)
            def y = myList.unique(false) { it }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_unique_Chaining_Assignment_Violation() {
        final SOURCE = '''
            def x = myList.unique().findAll { x < 1 }
            def y = myList.unique(true).findAll { y < 1 }
        '''
        assertViolations(SOURCE,
                [line:2, source:'def x = myList.unique().findAll { x < 1 }'],
                [line:3, source:'def y = myList.unique(true).findAll { y < 1 }'])
    }

    @Override
    protected AssignCollectionUniqueRule createRule() {
        new AssignCollectionUniqueRule()
    }
}
