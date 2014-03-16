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
 * Tests for ExplicitCreationOfTreeSetRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class ExplicitTreeSetInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitTreeSetInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as SortedSet
            class MyClass {
                def x = [] as SortedSet
                def m(foo = [] as SortedSet) {
                    def x = [] as SortedSet
                    def y = new TreeSet() {   // anony inner class OK
                    }
                    def m1 = new TreeSet(x)    // constructor with parameter is OK
                    def m2 = new TreeSet(23)
                    def m3 = new TreeSet([1,2,3])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new TreeSet()
            class MyClass {
                def m() {
                    def x = new TreeSet()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new TreeSet()',
                5, 'def x = new TreeSet()')
    }

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new TreeSet()
                def m(foo = new TreeSet()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new TreeSet()', 'TreeSet objects are better instantiated using the form "[] as SortedSet"',
                4, 'def m(foo = new TreeSet())', 'TreeSet objects are better instantiated using the form "[] as SortedSet"')
    }

    protected Rule createRule() {
        new ExplicitTreeSetInstantiationRule()
    }
}
