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
package org.codenarc.rule.security

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for SystemExitRule
 *
 * @author Hamlet D'Arcy
  */
class SystemExitRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'SystemExit'
    }

    @Test
    void testApplyTo_Violation_Initializers() {
        final SOURCE = '''
            class MyClass {
                static {
                    System.exit(1)
                }
                {
                    System.exit(0)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'System.exit(1)', 'Calling System.exit() is insecure and can expose a denial of service attack',
                7, 'System.exit(0)', 'Calling System.exit() is insecure and can expose a denial of service attack')
    }

    @Test
    void testApplyTo_Violation_Methods() {
        final SOURCE = '''
            class MyClass {
                static def method1() {
                    System.exit(0)
                }
                def method2() {
                    System.exit(0)
                }
            }
        '''
        assertTwoViolations(SOURCE,
                4, 'System.exit(0)', 'Calling System.exit() is insecure and can expose a denial of service attack',
                7, 'System.exit(0)', 'Calling System.exit() is insecure and can expose a denial of service attack')
    }

    @Test
    void testApplyTo_Violation_Closures() {
        final SOURCE = '''
            System.exit(0)
            def method = {
                System.exit(0)
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'System.exit(0)',
                4, 'System.exit(0)')
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    System2.exit(1)
                    System.exit2(1)
                    System.exit()
                    System.exit(1, 1)
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SystemExitRule()
    }
}
