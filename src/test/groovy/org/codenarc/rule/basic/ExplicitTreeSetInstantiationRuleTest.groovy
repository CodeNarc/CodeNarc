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
 * Tests for ExplicitCreationOfTreeSetRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ExplicitTreeSetInstantiationRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == "ExplicitTreeSetInstantiation"
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as SortedSet
            class MyClass {
                def x = [] as SortedSet
                def m(foo = [] as SortedSet) {
                    def x = [] as SortedSet
                    def y = new TreeSet() {   // anony inner class OK
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

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

    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new TreeSet()
                def m(foo = new TreeSet()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new TreeSet()',
                4, 'def m(foo = new TreeSet())')
    }


    protected Rule createRule() {
        new ExplicitTreeSetInstantiationRule()
    }
}