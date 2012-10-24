/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EmptySwitchStatementRule
 *
 * @author Chris Mair
 */
class EmptySwitchStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptySwitchStatement'
    }

    @Test
    void testApplyTo_EmptySwitchStatement() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    switch(myVariable) {
                        // empty
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'switch(myVariable) {')
    }

    @Test
    void testApplyTo_NonEmptySwitchStatement() {
        final SOURCE = '''
            def myVar = 123
            switch(myVariable) {
                case 123: println 'ok'; break
                default: println 'bad'
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new EmptySwitchStatementRule()
    }

}
