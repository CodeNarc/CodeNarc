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
 * Tests for AssignCollectionSortRule
 *
 * @author Hamlet D'Arcy
 */
class AssignCollectionSortRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignCollectionSort'
    }

    void testSuccessScenario() {
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

    void testNoArgs() {
        final SOURCE = '''
            def x = myList.sort()
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort()')
    }

    void testOneArgs() {
        final SOURCE = '''
            def x = myList.sort() { it }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.sort()')
    }

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