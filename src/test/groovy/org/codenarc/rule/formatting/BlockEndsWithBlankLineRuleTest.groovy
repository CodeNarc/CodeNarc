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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for BlockEndsWithBlankLineRule
 *
 * @author Marcin Erdmann
 */
class BlockEndsWithBlankLineRuleTest extends AbstractRuleTestCase<BlockEndsWithBlankLineRule> {

    private static final String MESSAGE = 'Code block ends with a blank line.'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'BlockEndsWithBlankLine'
    }

    @Test
    void testMethodsNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void singleLineMethod() { }

                void emptyMethod() {
                }

                boolean oddlyFormattedMethod() {

                true }

                String endingWithNonEmptyLine() {
                    "value"
                }
            }
        '''
    }

    @Test
    void testIfElseNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void method() {
                    if (true) false
                    if (true) {
                        'true'
                    } else {

                    'false' }
                    if (false) {
                    }
                }
            }
        '''
    }

    @Test
    void testSingleMethodViolation() {
        final SOURCE = '''
            class InvalidClass {
                int endingWithBlankLine() {
                    return 1

                }
            }
        '''

        assertSingleViolation(SOURCE, 5, '', MESSAGE)
    }

    @Test
    void testSingleIfViolation() {
        final SOURCE = '''
            class InvalidClass {
                int method() {
                    if (true) {
                        1

                    }
                }
            }
        '''

        assertSingleViolation(SOURCE, 6, '', MESSAGE)
    }

    @Test
    void testSingleIfElseViolations() {
        final SOURCE = '''
            class InvalidClass {
                int method() {
                    if (true) {
                        1

                    } else {
                        2

                    }
                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 6, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 9, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testClosureViolation() {
        final SOURCE = '''
            class InvalidClass {
                def methodWithClosure() {
                    { ->
                        1

                    }
                }
            }
        '''

        assertSingleViolation(SOURCE, 6, '', MESSAGE)
    }

    @Test
    void testNestedViolations() {
        final SOURCE = '''
            class InvalidClass {
                int method() {
                    if (true) {
                        for (i = 0; i < 1; i++) {
                            for (item in [1]) {
                                while (false) {
                                    { ->
                                        try {
                                            1

                                        } catch (e) {
                                            2

                                        } finally {
                                            3

                                        }

                                    }

                                }

                            }

                        }

                    } else {
                        switch (false) {
                            default:
                                4

                        }

                    }

                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 11, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 14, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 17, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 19, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 21, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 23, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 25, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 27, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 32, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 34, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 36, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testMultipleMethodViolations() {
        final SOURCE = '''
            class InvalidClass {
                InvalidClass() {

                }

                void second() {

                }
            }
        '''

        assertViolations(SOURCE,
            [lineNumber: 4, sourceLineText: '', messageText: MESSAGE],
            [lineNumber: 8, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testClosures_Violations() {
        final SOURCE = '''
            def myService = [
                getCount:{ id, name ->
                    assert id == 99
                    assert name == 'Joe'
                    return 1

                },
                doStuff:{

                },
                doOtherStuff:{ id ->

                }] as MyService

            def closure1 = { id ->

            }

            def closure2 = { id ->

            };

            def list = [{ id ->

            },
            123]

            myList.each { item ->

            }
        '''

        assertViolations(SOURCE,
                [lineNumber: 7, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 10, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 13, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 17, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 21, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 25, sourceLineText: '', messageText: MESSAGE],
                [lineNumber: 30, sourceLineText: '', messageText: MESSAGE])
    }

    @Test
    void testKnownLimitations_ClosureExpressions() {
        final SOURCE = '''
            // If a Closure is within another expression and the closing brace is not followed by anything else on the same line

            // Closure within a Map
            def myService = [
                doOtherStuff:{ id ->

                }
            ] as MyService

            // Closure within a List
            def list = [
                123,
                { id ->

                }   // a comment does not matter
            ]
        '''

        if (GroovySystem.getVersion().startsWith('2')) {
            assertNoViolations(SOURCE)
        } else {
            assertViolations(SOURCE,
                    [lineNumber: 7, sourceLineText: '', messageText: MESSAGE],
                    [lineNumber: 15, sourceLineText: '', messageText: MESSAGE])
        }
    }

    @Override
    protected BlockEndsWithBlankLineRule createRule() {
        new BlockEndsWithBlankLineRule()
    }
}
