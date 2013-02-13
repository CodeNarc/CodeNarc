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
 * Tests for AssignCollectionSortRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class AssignCollectionSortRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignCollectionSort'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            def allPaths = resultsMap.keySet().sort()
        	myList.sort()
        	myList.sort() { it }
        	myList.sort(2) { it }
            myList.findAll{ it < 50 }.sort()
            def w = getMyList().sort().findAll { x < 1 }
            def x = myList.foo.sort().findAll { x < 1 }
            def y = myList.foo().sort().findAll { x < 1 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoArgs() {
        final SOURCE = '''
            def x = myList.sort()
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort()')
    }

    @Test
    void testOneArg_Closure() {
        final SOURCE = '''
            def x = myList.sort { it }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort')
    }

    @Test
    void testOneArg_Comparator() {
        final SOURCE = '''
            def x = myList.sort(comparator)
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort')
    }

    @Test
    void testOneArg_True() {
        final SOURCE = '''
            def x = myList.sort(true)
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort')
    }

    @Test
    void testOneArg_False() {
        final SOURCE = '''
            def x = myList.sort(false)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoArgs_Closure_MutateTrue() {
        final SOURCE = '''
            def x = myList.sort(true) { it }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort')
    }

    @Test
    void testTwoArgs_Closure_MutateFalse() {
        final SOURCE = '''
            def x = myList.sort(false) { it }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testTwoArgs_Comparator_MutateTrue() {
        final SOURCE = '''
            def x = myList.sort(true, comparator)
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort')
    }

    @Test
    void testTwoArgs_Comparator_MutateFalse() {
        final SOURCE = '''
            def x = myList.sort(false, comparator)
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testChaining() {
        final SOURCE = '''
            def x = myList.sort().findAll { x < 1 }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort().findAll { x < 1 }')
    }

    protected Rule createRule() {
        new AssignCollectionSortRule()
    }
}
