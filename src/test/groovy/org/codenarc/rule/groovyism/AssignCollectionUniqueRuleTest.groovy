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
 * Tests for AssignCollectionUniqueRule
 *
 * @author Nick Larson
 * @author Juan Vazquez
 * @author Jon DeJong
 * 
 */
class AssignCollectionUniqueRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AssignCollectionUnique'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def allPaths = resultsMap.values().unique()
        	myList.unique()
        	myList.unique() { it }
        	myList.unique(2) { it }
            myList.findAll{ it < 50 }.unique()
            def w = getMyList().unique().findAll { x < 1 }
            def x = myList.foo.unique().findAll { x < 1 }
            def y = myList.foo().unique().findAll { x < 1 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoArgs() {
        final SOURCE = '''
            def x = myList.unique()
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.unique()')
    }

    @Test
    void testOneArgs() {
        final SOURCE = '''
            def x = myList.unique() { it }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.unique()')
    }

    @Test
    void testChaining() {
        final SOURCE = '''
            def x = myList.unique().findAll { x < 1 }
        '''
        assertSingleViolation(SOURCE, 2, 'def x = myList.unique().findAll { x < 1 }')
    }

    protected Rule createRule() {
        new AssignCollectionUniqueRule()
    }
}
