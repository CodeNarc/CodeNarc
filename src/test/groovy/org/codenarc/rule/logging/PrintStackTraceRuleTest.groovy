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

/**
 * Tests for PrintStackTraceRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class PrintStackTraceRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'PrintStackTrace'
    }

    void testApplyTo_PrintStackTrace() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                e.printStackTrace()
            }
        '''
        assertSingleViolation(SOURCE, 4, 'e.printStackTrace()')
    }

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

    void testApplyTo_PrintStackTrace_WithParameter() {
        final SOURCE = '''
            try {
            } catch(MyException e) {
                e.printStackTrace(System.err)
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NoViolation() {
        final SOURCE = '''
            void testSomething() {
                println "123"
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_GStringMethodName() {
        final SOURCE = ' "$myMethodName"(1234) '
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new PrintStackTraceRule()
    }
}