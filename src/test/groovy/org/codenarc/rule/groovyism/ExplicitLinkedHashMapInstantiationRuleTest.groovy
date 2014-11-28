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
 * Tests for ExplicitLinkedHashMapInstantiationRule
 *
 * @author René Scheibe
 */
class ExplicitLinkedHashMapInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitLinkedHashMapInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [:]
            class MyClass {
                def x = [:]
                def m(foo = [:]) {
                    def x = [:]
                    def y = new LinkedHashMap() {   // anonymous inner class OK
                    }
                    def m1 = new LinkedHashMap(x)   // constructor with parameter is OK
                    def m2 = new LinkedHashMap(23)
                    def m3 = new LinkedHashMap([a:1, b:2])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new LinkedHashMap()
            class MyClass {
                def m() {
                    def x = new LinkedHashMap()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new LinkedHashMap()', 'LinkedHashMap objects are better instantiated using the form "[:]"',
                5, 'def x = new LinkedHashMap()', 'LinkedHashMap objects are better instantiated using the form "[:]"')
    }

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new LinkedHashMap()
                def m(foo = new LinkedHashMap()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new LinkedHashMap()', 'LinkedHashMap objects are better instantiated using the form "[:]"',
                4, 'def m(foo = new LinkedHashMap())', 'LinkedHashMap objects are better instantiated using the form "[:]"')
    }

    protected Rule createRule() {
        new ExplicitLinkedHashMapInstantiationRule()
    }
}
