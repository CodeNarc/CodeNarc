/*
 * Copyright 2023 the original author or authors.
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
import org.junit.jupiter.api.Test

/**
 * Tests for NoScriptBindingsRule
 *
 * @author Josh Chorlton
 * @author Chris Mair
 */
class NoScriptBindingsRuleTest extends AbstractRuleTestCase<NoScriptBindingsRule> {

    @Test
    void RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'NoScriptBindings'
    }

    @Test
    void NoViolations() {
        final SOURCE = '''
            // these usages are OK
            Integer d = 5

            def myfun() {
                def a = "foo"
                Integer b = 6
            }

            class MyCorrectClass {
                private Integer b = 6
                public static final VALUE = 1234
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void MainScript() {
        final SOURCE = '''
            a = "foo"
        '''
        assertSingleViolation(SOURCE, 2, 'a = "foo"', 'The script variable [a] does not have a type declaration. It will be bound to the script which could cause concurrency issues.')
    }

    @Test
    void WithinFunction() {
        final SOURCE = '''
            def myfun() {
                a = "foo"
            }
        '''
        assertSingleViolation(SOURCE, 3, 'a = "foo"', 'The script variable [a] does not have a type declaration. It will be bound to the script which could cause concurrency issues.')
    }

    @Test
    void ReassignVariable() {
        final SOURCE = '''
            String getValue(boolean isActive) {
                String value = 'abc'
                if (isActive) {
                    value = 'def'
                }
                
                for (int i=0 ;i<3; i++) {
                    i = i + 3
                }

                // Multiple assignment
                def (userName, email) = ['abc', 'abc@email.com']
                def (String candyName, int count) = ['M&M', 13]

                userName = 'new_user'
                count = 99
                
                while (count < 1000) {
                    if (email) {
                        String alias
                        alias = 'joe'
                        count = count + 1
                    }
                }

                return value
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void ReassignParameter() {
        final SOURCE = '''
            void doStuff(boolean isActive) {
                isActive = false
            }
        '''
        //assertNoViolations(SOURCE)

        // TODO: Fix this known Issue
        assertSingleViolation(SOURCE, 3, 'isActive = false', 'The script variable [isActive]')
    }

    @Test
    void ReassignField_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int count = 99
                void doStuff() {
                    count = 22
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected NoScriptBindingsRule createRule() {
        new NoScriptBindingsRule()
    }
}
