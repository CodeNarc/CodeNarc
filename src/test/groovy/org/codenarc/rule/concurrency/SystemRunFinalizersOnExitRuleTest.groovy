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
 * Tests for SystemRunFinalizersOnExitRule
 *
 * @author Hamlet D'Arcy
 */
class SystemRunFinalizersOnExitRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SystemRunFinalizersOnExit'
    }

    @Test
    void testApplyTo_Violation_Initializers() {
        final SOURCE = '''
            class SystemRunFinalizersOnExitClass1 {
                static {
                    System.runFinalizersOnExit(true)
                }
                {
                    System.runFinalizersOnExit(true)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'System.runFinalizersOnExit(true)',
                7, 'System.runFinalizersOnExit(true)')
    }

    @Test
    void testApplyTo_Violation_Methods() {
        final SOURCE = '''
            class SystemRunFinalizersOnExitClass2 {
                static def method1() {
                    System.runFinalizersOnExit(true)
                }
                def method2() {
                    System.runFinalizersOnExit(true)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'System.runFinalizersOnExit(true)',
                7, 'System.runFinalizersOnExit(true)')
    }

    @Test
    void testApplyTo_Violation_Closures() {
        final SOURCE = '''
            System.runFinalizersOnExit(true)
            def method = {
                System.runFinalizersOnExit(false)
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'System.runFinalizersOnExit(true)',
                4, 'System.runFinalizersOnExit(false)')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class SystemRunFinalizersOnExitClass3 {
                def myMethod() {
                    otherObject.runFinalizersOnExit(true)
                    System.out.println "1234"
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SystemRunFinalizersOnExitRule()
    }

}
