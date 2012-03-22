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

/**
 * Tests for CouldBeElvisRule
 *
 * @author GUM
 */
class CouldBeElvisRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'CouldBeElvis'
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	   def x
        	   def y 
        	   
        	   if(!x) {
        	     y = "something"
        	   }
        	   
        '''
        assertNoViolations(SOURCE)
    }


    void testSecondSuccessScenario() {
        final SOURCE = '''
        	   def x
        	   def y 
        	   
        	   if(!x) {
        	     println x
        	   }
        	   
        '''
        assertNoViolations(SOURCE)
    }

    void testCouldBeElvisViolation() {
        final SOURCE = '''
              def x
              
              if (!x) {
                  x = "some value"
              }
            '''
        assertSingleViolation(SOURCE, 4, 'if (!x)', "Code could use elvis operator: x = x ?: 'some value'")
    }

    void testThisReferenceCouldBeElvisViolation() {
        final SOURCE = '''
              if (!this.x) {
                  this.x = foo()
              }
            '''
        assertSingleViolation(SOURCE, 2, 'if (!this.x)', 'Code could use elvis operator: this.x = this.x ?: this.foo()')
    }

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
