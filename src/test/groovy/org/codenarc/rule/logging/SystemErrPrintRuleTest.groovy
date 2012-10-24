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
package org.codenarc.rule.logging

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SystemErrPrintRule
 *
 * @author Chris Mair
  */
class SystemErrPrintRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SystemErrPrint'
    }

    @Test
    void testApplyTo_SystemErrPrintln_NoArgs() {
        final SOURCE = '''
            System.err.println()
        '''
        assertSingleViolation(SOURCE, 2, 'System.err.println()')
    }

    @Test
    void testApplyTo_SystemErrPrintln_String() {
        final SOURCE = '''
            System.err.println("yes")
        '''
        assertSingleViolation(SOURCE, 2, 'System.err.println("yes")')
    }

    @Test
    void testApplyTo_SystemErrPrintln_Int() {
        final SOURCE = '''
            System.err.println(1234)
        '''
        assertSingleViolation(SOURCE, 2, 'System.err.println(1234)')
    }

    @Test
    void testApplyTo_SystemErrPrint_Int() {
        final SOURCE = '''
            System.err.print(1234)
        '''
        assertSingleViolation(SOURCE, 2, 'System.err.print(1234)')
    }

    @Test
    void testApplyTo_SystemErrPrintf() {
        final SOURCE = '''
            System.err.printf("%d", 1234)
            System.err.printf("%d %d", 1234, -99)
        '''
        assertTwoViolations(SOURCE, 2, 'System.err.printf("%d", 1234)', 3, 'System.err.printf("%d %d", 1234, -99)')
    }

    @Test
    void testApplyTo_WithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    System.err.print(1234)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'System.err.print(1234)')
    }

    @Test
    void testApplyTo_PrintlnButNotSystemErr() {
        final SOURCE = '''
    @Test
            void testSomething() {
                println "123"
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SystemErrPrintRule()
    }
}
