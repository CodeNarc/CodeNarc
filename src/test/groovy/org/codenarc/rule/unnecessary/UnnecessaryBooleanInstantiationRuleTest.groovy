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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryBooleanInstantiationRule
 *
 * @author Chris Mair
  */
class UnnecessaryBooleanInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryBooleanInstantiation'
    }

    @Test
    void testApplyTo_NewBoolean() {
        final SOURCE = '''
            class MyClass {
                def b1 = new Boolean(true)
                def exception = new Exception('bad')
                def b2 = new java.lang.Boolean(false)
            }
        '''
        assertTwoViolations(SOURCE,
                3, 'new Boolean(true)', 'There is typically no need to instantiate Boolean instances.',
                5, 'new java.lang.Boolean(false)', 'There is typically no need to instantiate Boolean instances.')
    }

    @Test
    void testApplyTo_NewBoolean_NotWithinClass() {
        final SOURCE = '''
            def b1 = new java.lang.Boolean(true)
            def name2 = "abc"
            void calculate() {
                String name = 'defghi'
                def b1 = new Boolean(true)
                def str = new StringBuffer()        
            }
        '''
        assertTwoViolations(SOURCE, 2, 'new java.lang.Boolean(true)', 6, 'new Boolean(true)')
    }

    @Test
    void testApplyTo_BooleanValueOf() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    def b1 = Boolean.valueOf(true)
                    def b2 = Boolean.valueOf(otherVariable)
                    def b3 = Boolean.valueOf(false)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'Boolean.valueOf(true)', 'Call to Boolean.valueOf(true) is unnecessary and can probably be replaced with simply true',
                6, 'Boolean.valueOf(false)', 'Call to Boolean.valueOf(false) is unnecessary and can probably be replaced with simply false')
    }

    @Test
    void testApplyTo_WithinEnum() {
        final SOURCE = '''
            enum MyEnum {
                NONE, READ, WRITE
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    def b = Boolean.valueOf(myVariable)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryBooleanInstantiationRule()
    }

}
