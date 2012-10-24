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
 * Tests for ExplicitLinkedListInstantiationRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class ExplicitLinkedListInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitLinkedListInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as Queue
            class MyClass {
                def x = [] as Queue
                def m(foo = [] as Queue) {
                    def x = [] as Queue
                    def y = new LinkedList() {   // anony inner class OK                    
                    }
                    def m1 = new LinkedList(x)    // constructor with parameter is OK
                    def m2 = new LinkedList(23)
                    def m3 = new LinkedList([1,2,3])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
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

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new LinkedList()
                def m(foo = new LinkedList()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new LinkedList()', 'LinkedList objects are better instantiated using the form "[] as Queue"',
                4, 'def m(foo = new LinkedList())', 'LinkedList objects are better instantiated using the form "[] as Queue"')
    }

    protected Rule createRule() {
        new ExplicitLinkedListInstantiationRule()
    }
}
