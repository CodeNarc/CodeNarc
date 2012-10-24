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
package org.codenarc.ruleset

import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.assertContainsAllInOrder

/**
 * Tests for MovedRules
 *
 * @author Chris Mair
 */
class MovedRulesTest extends AbstractTestCase {

    @Test
    void testGetMovedOrRenamedMessageForRuleName_RuleRenamed() {
        def message = MovedRules.getMovedOrRenamedMessageForRuleName('HardcodedWindowsRootDirectory')
        log(message)
        assertContainsAllInOrder(message, ['HardcodedWindowsRootDirectory', 'renamed to', 'HardCodedWindowsRootDirectory'])
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_RuleMoved_Unnecessary() {
        def message = MovedRules.getMovedOrRenamedMessageForRuleName('AddEmptyString')
        log(message)
        assertContainsAllInOrder(message, ['AddEmptyString', 'moved to', 'unnecessary', 'ruleset'])
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_RuleMoved_Groovyism() {
        def message = MovedRules.getMovedOrRenamedMessageForRuleName('AssignCollectionSort')
        log(message)
        assertContainsAllInOrder(message, ['AssignCollectionSort', 'moved to', 'groovyism', 'ruleset'])
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_RuleMoved_Design() {
        def message = MovedRules.getMovedOrRenamedMessageForRuleName('BooleanMethodReturnsNull')
        log(message)
        assertContainsAllInOrder(message, ['BooleanMethodReturnsNull', 'moved to', 'design', 'ruleset'])
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_RuleMoved_Convention() {
        def message = MovedRules.getMovedOrRenamedMessageForRuleName('ConfusingTernary')
        log(message)
        assertContainsAllInOrder(message, ['ConfusingTernary', 'moved to', 'convention', 'ruleset'])
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_NoSuchRuleName_ReturnsEmptyString() {
        assert MovedRules.getMovedOrRenamedMessageForRuleName('xxx') == ''
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_NullRuleName_ReturnsEmptyString() {
        assert MovedRules.getMovedOrRenamedMessageForRuleName(null) == ''
    }

    @Test
    void testGetMovedOrRenamedMessageForRuleName_EmptyRuleName_ReturnsEmptyString() {
        assert MovedRules.getMovedOrRenamedMessageForRuleName('') == ''
    }

}
