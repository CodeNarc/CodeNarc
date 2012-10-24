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
 * Tests for UnnecessaryStringInstantiationRule
 *
 * @author Chris Mair
  */
class UnnecessaryStringInstantiationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryStringInstantiation'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def b1 = new String(xyz)
                def exception = new Exception('bad')
                def b2 = new String('abc')
                def b3 = new java.lang.String("abc")
                def b4 = new String('abc'.bytes)
                def b5 = new String( [1,2,3] as char[])
            }
        '''
        assertTwoViolations(SOURCE, 5, "new String('abc')", 6, 'new java.lang.String("abc")')
    }

    @Test
    void testApplyTo_WithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    def b2 = new String('abc')
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, "new String('abc')", 'There is typically no need to call the String constructor')
    }

    @Test
    void testApplyTo_Violation_NotWithinClass() {
        final SOURCE = '''
            def b1 = new java.lang.String('abc')
            def name2 = "abc"
            void calculate() {
                String name = 'defghi'
                def b1 = new String("""
                    xxx
                """)
                def str = new StringBuffer()
            }
        '''
        assertTwoViolations(SOURCE, 2, "new java.lang.String('abc')", 6, 'new String(')
    }

    @Test
    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    def b = new String(myBytes)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new UnnecessaryStringInstantiationRule()
    }

}
