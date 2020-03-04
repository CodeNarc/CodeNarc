/*
 * Copyright 2017 the original author or authors.
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
import org.junit.Test

/**
 * Tests for MethodReturnTypeRequiredRule
 *
 * @author Marcin Erdmann
 */
class MethodReturnTypeRequiredRuleTest extends AbstractRuleTestCase<MethodReturnTypeRequiredRule> {

    @Test
    void testNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void voidReturningMethod() {
                }

                Object objectReturningMethod() {
                }

                int intReturningMethod() {
                }
            }
        '''
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class InvalidClass {
                def defReturningMethod() {
                }
            }
        '''

        assertSingleViolation(SOURCE, 3, 'def defReturningMethod() {', 'Method "defReturningMethod" has a dynamic return type')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class InvalidClass {
                def defReturningMethod() {
                }

                private noReturnTypeMethod() {
                }
            }
        '''

        assertViolations(SOURCE,
            [
                lineNumber: 3,
                sourceLineText: 'def defReturningMethod() {',
                messageText: 'Method "defReturningMethod" has a dynamic return type'
            ],
            [
                lineNumber: 6,
                sourceLineText: 'private noReturnTypeMethod() {',
                messageText: 'Method "noReturnTypeMethod" has a dynamic return type'
            ]
        )
    }

    @Test
    void testNoViolationsWhenMethodIgnored() {
        rule.ignoredMethods = 'beforeUpdate'

        assertNoViolations '''
            class ValidClass {
                def beforeUpdate() {
                }
            }
        '''
    }

    @Test
    void testNoViolationsWhenMultipleMethodsIgnored() {
        rule.ignoredMethods = 'beforeUpdate, beforeInsert'

        assertNoViolations '''
            class ValidClass {
                def beforeUpdate() {
                }

                Object beforeInsert() {
                }
            }
        '''
    }

    @Override
    protected MethodReturnTypeRequiredRule createRule() {
        new MethodReturnTypeRequiredRule()
    }
}
