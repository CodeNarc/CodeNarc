/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for MethodCallNameTrailingWhitespaceRule
 */
class MethodCallNameTrailingWhitespaceRuleTest extends AbstractRuleTestCase<MethodCallNameTrailingWhitespaceRule> {

    @Test
    void ruleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MethodCallNameTrailingWhitespace'
    }

    @Test
    void noViolations() {
        assertNoViolations '''
            class Valid {
                Valid() {
                    super()
                }

                void valid() {
                    aMethod("arg")
                    aMethod( "arg")
                    aMethod "arg"
                    new String(
                        "valid"
                    )
                }

                void aMethod(String argument) {
                }

                LinkedHashSet<String> set() {
                    new LinkedHashSet<Class<?>>()
                }
            }
        '''
    }

    @Test
    void trailingWhitespaceInMethodCallWithParenthesesCausesViolation() {
        final SOURCE = '''
            class TrailingWhitespaceInMethodCallWithParentheses {
                void invalid() {
                    aMethod ("arg")
                }

                void aMethod(String argument) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, 'aMethod ("arg")', 'There is whitespace between method name and parenthesis in a method call.')
    }

    @Test
    void trailingWhitespaceInConstructorCallCausesViolation() {
        final SOURCE = '''
            class TrailingWhitespaceInConstructorCall {
                TrailingWhitespaceInConstructorCall() {
                    throw new Exception ()
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, 'throw new Exception ()', 'There is whitespace between class name and parenthesis in a constructor call.')
    }

    @Test
    void trailingWhitespaceInSuperConstructorCallCausesAViolation() {
        final SOURCE = '''
            class TrailingWhitespaceInSuperConstructorCall {
                TrailingWhitespaceInSuperConstructorCall() {
                    super ()
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, 'super ()', 'There is whitespace between super and parenthesis in a constructor call.')
    }

    @Test
    void excessiveTrailingWhitespaceInMethodCallWithoutParenthesesCausesAViolation() {
        final SOURCE = '''
            class ExcessiveTrailingWhitespaceInMethodCallWithoutParentheses {
                void invalid() {
                    aMethod  "arg"
                }

                void aMethod(String argument) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, 'aMethod  "arg"', 'There is more than one space between method name and arguments in a method call.')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class Invalid {
                void invalid() {
                    aMethod ("arg")
                    aMethod  "arg"
                }

                void aMethod(String argument) {
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 4, sourceLineText: 'aMethod ("arg")', messageText: 'There is whitespace between method name and parenthesis in a method call.'],
            [lineNumber: 5, sourceLineText: 'aMethod  "arg"', messageText: 'There is more than one space between method name and arguments in a method call.']
        )
    }

    @Override
    protected MethodCallNameTrailingWhitespaceRule createRule() {
        new MethodCallNameTrailingWhitespaceRule()
    }
}
