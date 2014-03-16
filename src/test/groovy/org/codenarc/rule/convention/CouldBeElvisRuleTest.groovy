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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for CouldBeElvisRule
 *
 * @author GUM
 */
class CouldBeElvisRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'CouldBeElvis'
    }

    @Test
    void testIfStatement_AssignmentToDifferentVariable_NoViolation() {
        final SOURCE = '''
        	   def x
        	   def y 
        	   
        	   if(!x) {
        	     y = "something"
        	   }
        	   
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfStatement_NonAssignmentToSameVariable_NoViolation() {
        final SOURCE = '''
        	   def x
        	   def y 
        	   
        	   if(!x) {
        	     println x
        	   }
        	   
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfStatement_AssignmentToSameVariable_Violation() {
        final SOURCE = '''
              def x
              
              if (!x) {
                  x = "some value"
              }
            '''
        assertSingleViolation(SOURCE, 4, 'if (!x)', "Code could use elvis operator: x = x ?: 'some value'")
    }

    @Test
    void testIfStatement_NoBraces_AssignmentToSameVariable_Violation() {
        final SOURCE = '''
              def x

              if (!x)
                  x = "some value"
            '''
        assertSingleViolation(SOURCE, 4, 'if (!x)', "Code could use elvis operator: x = x ?: 'some value'")
    }

    @Test
    void testIfStatement_AssignmentToSameObjectProperty_Violation() {
        final SOURCE = '''
              def params

              if (!params.max) {
                  params.max = 10
              }
            '''
        assertSingleViolation(SOURCE, 4, 'if (!params.max)', 'Code could use elvis operator: params.max = params.max ?: 10')
    }

    @Test
    void testIfStatement_NoBraces_AssignmentToSameObjectProperty_Violation() {
        final SOURCE = '''
              def params
              if (!params.max) params.max = 10
            '''
        assertSingleViolation(SOURCE, 3, 'if (!params.max)', 'Code could use elvis operator: params.max = params.max ?: 10')
    }

    @Test
    void testThisReferenceCouldBeElvisViolation() {
        final SOURCE = '''
              if (!this.x) {
                  this.x = foo()
              }
            '''
        assertSingleViolation(SOURCE, 2, 'if (!this.x)', 'Code could use elvis operator: this.x = this.x ?: this.foo()')
    }

    @Test
    void testDoingWorkInIf() {
        final SOURCE = '''
              def x
              def y
              
              if (!x) {
                  def z = "do this"
                  x = z + "some value"
                  y = x+"bob"
              }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoingWorkInIfWithXFirst() {
        final SOURCE = '''
              def x
              def y
              
              if (!x) {
                  x = "some value"
                  def z = "do this"
                  y = x+"bob"
              }
            '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new CouldBeElvisRule()
    }
}
