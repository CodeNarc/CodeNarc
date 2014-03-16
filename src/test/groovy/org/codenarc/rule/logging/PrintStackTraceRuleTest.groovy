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
 * Tests for PrintStackTraceRule
 *
 * @author Chris Mair
  */
class PrintStackTraceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PrintStackTrace'
    }

    @Test
    void testApplyTo_PrintStackTrace() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                e.printStackTrace()
            }
        '''
        assertSingleViolation(SOURCE, 4, 'e.printStackTrace()')
    }

    @Test
    void testApplyTo_PrintStackTrace_WithinClosure() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    try {
                    } catch(MyException e) {
                        e.printStackTrace()
                    }
                }
            }
        '''
        assertSingleViolation(SOURCE, 6, 'e.printStackTrace()')
    }

    @Test
    void testApplyTo_PrintStackTrace_WithParameter_NoViolation() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                e.printStackTrace(System.err)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PrintSanitizedStackTrace() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                StackTracUtils.printSanitizedStackTrace(e)
            }
        '''
        assertSingleViolation(SOURCE, 4, 'StackTracUtils.printSanitizedStackTrace(e)')
    }

    @Test
    void testApplyTo_PrintSanitizedStackTrace_WithPrintWriter_NoViolation() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                StackTracUtils.printSanitizedStackTrace(e, myPrintWriter)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolation() {
        final SOURCE = '''
    @Test
            void testSomething() {
                println "123"
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_GStringMethodName() {
        final SOURCE = ' "$myMethodName"(1234) '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new PrintStackTraceRule()
    }
}
