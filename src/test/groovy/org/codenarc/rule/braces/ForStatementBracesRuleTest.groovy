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
 * Tests for ForStatementBracesRule
 *
 * @author Chris Mair
 */
class ForStatementBracesRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ForStatementBraces'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    for (int i=0; i < 10; i++) println '23'
                    println 'ok'
                    for (int j=0; j < 10; j++)
                        println 'loop'
                }
            }
        '''
        assertTwoViolations(SOURCE, 4, 'for (int i=0; i < 10; i++)', 6, 'for (int j=0; j < 10; j++)')
    }

    @Test
    void testApplyTo_Violation_ForStatementWithCommentOnly() {
        final SOURCE = '''
            for (int i=0; i < 10; i++) {
                // TODO Should do something here
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    for (int j=0; j < 10; j++) {
                        println "ready"
                    }
                    for (int j=0; j < 10; j++) { println '23' }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new ForStatementBracesRule()
    }

}
