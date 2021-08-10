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
import org.codenarc.util.GroovyVersion
import org.junit.Test

/**
 * Tests for SpaceAfterMethodCallNameRule
 */
class SpaceAfterMethodCallNameRuleTest extends AbstractRuleTestCase<SpaceAfterMethodCallNameRule> {

    private static final String ERROR_MESSAGE = 'There is whitespace between method name and parenthesis in a method call.'

    @Test
    void ruleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterMethodCallName'
    }

    @Test
    void test_NoViolations() {
        assertNoViolations '''
            class Valid {
                Valid() {
                    super()
                }

                void valid() {
                    aMethod()
                    aMethod("arg")
                    aMethod("arg")
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
    void test_TrailingWhitespaceInMethodCallWithParentheses_Violation() {
        final SOURCE = '''
            class TrailingWhitespaceInMethodCallWithParentheses {
                void invalid() {
                    aMethod ("arg")
                }

                void aMethod(String argument) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, 'aMethod ("arg")', ERROR_MESSAGE)
    }

    @Test
    void test_TrailingWhitespaceInConstructorCall_Violation() {
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
    void test_TrailingWhitespaceInSuperConstructorCall_Violation() {
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
    void test_ExcessiveTrailingWhitespaceInMethodCallWithoutParentheses_Violation() {
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
    void test_MultipleViolations() {
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
            [line: 4, source: 'aMethod ("arg")', message: ERROR_MESSAGE],
            [line: 5, source: 'aMethod  "arg"', message: 'There is more than one space between method name and arguments in a method call.']
        )
    }

    @Test
    void test_Enums_NoViolations() {
        final SOURCE = '''
            enum Visibility {
                PUBLIC('public'), PROTECTED('protected'), PRIVATE('private')

                private final String name

                private Visibility(String name) {
                    this.name = name
                }

                String getName() {
                    return name
                }
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void test_GroovyScript_NoViolations() {
        final SOURCE = '''
            void foo() {
              echo 'hi'
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_GroovyScript2_NoViolations() {
        final SOURCE = '''
            package example

            int doubleIt(final int x) {
                x * 2
            }
            return this
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SpacesInsideParentheses_NoViolations() {
        final SOURCE = '''
            // with range in a method call
            someRepository.saveAll( ( 1..3 ).collect { new SomeObjectBuilder().build() } )
            // with calculation in a method call
            someMethod( ( a + b ) / c )
            // operation on result
            someMethod( ( collectionA + collectionB ).toSet() )
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Groovy3LambdaSyntax_NoViolations() {
        final SOURCE = '''
            [1, 2, 3].forEach (it) -> { println it}
            
            doStuff(99, (it) -> { println it})
            '''
        if (GroovyVersion.isNotGroovyVersion2()) {
            assertNoViolations(SOURCE)
        }
    }

    @Test
    void test_Groovy3LambdaSyntax_Violation() {
        final SOURCE = '''
            doStuff ((it) -> { println it}, 99)
            '''
        if (GroovyVersion.isNotGroovyVersion2()) {
            assertViolations(SOURCE,
                    [line: 2, source: 'doStuff ((it) -> { println it}, 99)', message: ERROR_MESSAGE])
        }
    }

    @Override
    protected SpaceAfterMethodCallNameRule createRule() {
        new SpaceAfterMethodCallNameRule()
    }
}
