/*
 * Copyright 2010 the original author or authors.
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

/**
 * Tests for ExplicitCreationOfLinkedListRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class ExplicitLinkedListInstantiationRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ExplicitLinkedListInstantiation"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as Queue
            class MyClass {
                def x = [] as Queue
                def m(foo = [] as Queue) {
                    def x = [] as Queue
                    def y = new LinkedList() {   // anony inner class OK                    
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new LinkedList()
            class MyClass {
                def m() {
                    def x = new LinkedList()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new LinkedList()',
                5, 'def x = new LinkedList()')
    }

    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new LinkedList()
                def m(foo = new LinkedList()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new LinkedList()',
                4, 'def m(foo = new LinkedList())')
    }

    protected Rule createRule() {
        new ExplicitLinkedListInstantiationRule()
    }
}