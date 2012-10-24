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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for ExplicitArrayListInstantiationRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitArrayListInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitArrayListInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = []
            class MyClass {
                def x = []
                def m(foo = []) {
                    def x = []
                    def y = new ArrayList() {   // anony inner class OK                    
                    }
                    def a1 = new ArrayList(x)    // constructor with parameter is OK
                    def a2 = new ArrayList(23)
                    def a3 = ArrayList([1, 2, 3])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new ArrayList()
            class MyClass {
                def m() {
                    def x = new ArrayList()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new ArrayList()', 'ArrayList objects are better instantiated using the form "[]"',
                5, 'def x = new ArrayList()', 'ArrayList objects are better instantiated using the form "[]"')
    }

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new ArrayList()
                def m(foo = new ArrayList()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new ArrayList()',  
                4, 'def m(foo = new ArrayList())')
    }

    protected Rule createRule() {
        new ExplicitArrayListInstantiationRule()
    }
}
