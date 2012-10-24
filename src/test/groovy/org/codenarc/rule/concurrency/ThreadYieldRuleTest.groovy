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
package org.codenarc.rule.concurrency

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
  * Tests for ThreadYieldRule
  *
  * @author Hamlet D'Arcy
  */
class ThreadYieldRuleTest extends AbstractRuleTestCase {

    @Test
     void testRuleProperties() {
         assert rule.priority == 2
         assert rule.name == 'ThreadYield'
     }

    @Test
     void testApplyTo_Violation_Initializers() {
         final SOURCE = '''
             class ThreadYieldClass1 {
                 static {
                     Thread.yield()
                 }
                 {
                     Thread.yield()
                 }
             }
         '''
         assertTwoViolations(SOURCE,
             4, 'Thread.yield()',
             7, 'Thread.yield()')
     }

    @Test
     void testApplyTo_Violation_Methods() {
         final SOURCE = '''
             class ThreadYieldClass2 {
                 static def method1() {
                     Thread.yield()
                 }
                 def method2() {
                     Thread.yield()
                 }
             }
         '''
         assertTwoViolations(SOURCE,
             4, 'Thread.yield()',
             7, 'Thread.yield()')
     }

    @Test
     void testApplyTo_Violation_Closures() {
         final SOURCE = '''
             Thread.yield()
             def method = {
                 Thread.yield()
             }
         '''
         assertTwoViolations(SOURCE,
             2, 'Thread.yield()',
             4, 'Thread.yield()')
     }

    @Test
     void testApplyTo_NoViolations() {
         final SOURCE = '''
             class ThreadYieldClass3 {
                 def myMethod() {
                     otherObject.yield()
                     System.out.println "1234"
                 }
             }'''
         assertNoViolations(SOURCE)
     }

     protected Rule createRule() {
         new ThreadYieldRule()
     }
}
