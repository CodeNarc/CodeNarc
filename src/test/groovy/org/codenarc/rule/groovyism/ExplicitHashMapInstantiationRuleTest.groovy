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
 * Tests for ExplicitCreationOfHashMapRule
 *
 * @author Hamlet D'Arcy
 */
class ExplicitHashMapInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ExplicitHashMapInstantiation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def x = [:]
            class MyClass {
                def x = [:]
                def m(foo = [:]) {
                    def x = [:]
                    def y = new HashMap() {   // anony inner class OK
                    }
                    def m1 = new HashMap(x)    // constructor with parameter is OK
                    def m2 = new HashMap(23)
                    def m3 = new HashMap([a:1, b:2])
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testVariableDeclarations() {
        final SOURCE = '''
        	def x = new HashMap()
            class MyClass {
                def m() {
                    def x = new HashMap()
                }
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def x = new HashMap()',
                5, 'def x = new HashMap()')
    }

    @Test
    void testInClassUsage() {
        final SOURCE = '''
            class MyClass {
                def x = new HashMap()
                def m(foo = new HashMap()) {
                }
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'def x = new HashMap()',
                4, 'def m(foo = new HashMap())')
    }

    protected Rule createRule() {
        new ExplicitHashMapInstantiationRule()
    }
}
