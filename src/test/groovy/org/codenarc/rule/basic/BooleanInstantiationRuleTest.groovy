/*
 * Copyright 2009 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for BooleanInstantiationRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class BooleanInstantiationRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.id == 'BooleanInstantiation'
    }

    void testApplyTo_Violation() {
        // If within class, the expression callback gets called twice!
        final SOURCE = '''
            class MyClass {
                def b1 = new Boolean(true)
                def exception = new Exception('bad')
                def b2 = new java.lang.Boolean(false)
            }
        '''
        assertTwoViolations(SOURCE, 3, 'new Boolean(true)', 5, 'new java.lang.Boolean(false)')
    }

    void testApplyTo_Violation_NotWithinClass() {
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

    void testApplyTo_NoViolation() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    def b = Boolean.valueOf(true)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new BooleanInstantiationRule()
    }

}