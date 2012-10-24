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
package org.codenarc.rule.braces

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for IfStatementBracesRule
 *
 * @author Chris Mair
 */
class IfStatementBracesRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IfStatementBraces'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    if (x==23) println '23'
                    println 'ok'
                    if (alreadyInitialized())
                        println 'initialized'
                }
            }
        '''
        assertTwoViolations(SOURCE, 4, 'if (x==23)', 6, 'if (alreadyInitialized())')
    }

    @Test
    void testApplyTo_Violation_IfStatementWithCommentOnly() {
        final SOURCE = '''
            if (isReady) {
                // TODO Should do something here
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    if (isReady) {
                        println "ready"
                    }
                    if (x==23) { println '23' }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new IfStatementBracesRule()
    }

}
