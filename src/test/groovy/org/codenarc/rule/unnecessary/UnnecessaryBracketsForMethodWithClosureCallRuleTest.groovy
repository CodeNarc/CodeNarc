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

/**
 * Tests for UnnecessaryBracketsForMethodWithClosureCallRule
 *
 * @author Marcin Erdmann
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UnnecessaryBracketsForMethodWithClosureCallRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryBracketsForMethodWithClosureCall'
    }

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

    void testSingleViolation() {
        final SOURCE = '''
            [1,2,3].each() { println it }
        '''
        assertSingleViolation(SOURCE, 2, '[1,2,3].each() { println it }', "Brackets in the 'each' method call are unnecessary and can be removed.")
    }

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
                2, '''[1, 2, 3].collect() {
                it * 2
            }''',
                2, '''[1, 2, 3].collect() {
                it * 2
            }.any (
            ) {
                it > 5
            }''')   // todo: replace violation line number and message
    }

    void testNonDetectableViolations() {
        final SOURCE = '''
        	[1, 2, 3].each(/*comment*/) { println it }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryBracketsForMethodWithClosureCallRule()
    }
}
