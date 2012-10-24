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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnusedArrayRule
 *
 * @author Your Name Here
  */
class UnusedArrayRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UnusedArray'
    }

    @Test
    void testApplyTo_ArrayAssigned_NoViolations() {
        final SOURCE = '''
        	def array1 = new String[3]
            Object[] array2 = []
            println new Integer[3]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ArrayNotAssigned_ButLastStatementWithinAMethod_NoViolations() {
        final SOURCE = '''
            println new BigDecimal("23.45")
        	new String[3]
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ArrayNotAssigned_ButLastStatementWithinAClosure_NoViolations() {
        final SOURCE = '''
            def closure = { new String[3] }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_ArrayNotAssigned_Violations() {
        final SOURCE = '''
        	new String[3]
            println "ok"
        '''
        assertViolations(SOURCE, [lineNumber: 2, sourceLineText: 'new String[3]'])
    }

    @Test
    void testApplyTo_ArrayNotAssigned_WithinClosure_Violations() {
        final SOURCE = '''
            def myClosure = { ->
        	    new Object[2]
                doStuff()
            }
        '''
        assertViolations(SOURCE, [lineNumber: 3, sourceLineText: 'new Object[2]'])
    }

    protected Rule createRule() {
        new UnusedArrayRule()
    }

}
