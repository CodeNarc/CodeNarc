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
 * Tests for ExplicitHashSetInstantiationRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitHashSetInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitHashSetInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [] as Set
            class MyClass {
                def x = [] as Set
                def m(foo = [] as Set) {
                    def x = [] as Set
                    def y = new HashSet() {   // anony inner class OK
                    }
                    def s1 = new HashSet(x)   // constructor with parameter is OK
                    def s2 = new HashSet(23)
                    def s3 = new HashSet([a:1, b:2])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new HashSet()
            class MyClass {
                def m() {
                    def x = new HashSet()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new HashSet()',
                5, 'def x = new HashSet()')
    }

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new HashSet()
                def m(foo = new HashSet()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new HashSet()', 'HashSet objects are better instantiated using the form "[] as Set"',
                4, 'def m(foo = new HashSet())', 'HashSet objects are better instantiated using the form "[] as Set"')
    }

    protected Rule createRule() {
        new ExplicitHashSetInstantiationRule()
    }
}
