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
import org.codenarc.util.GroovyVersion
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
            [line: 6, source: '', message: MESSAGE],
            [line: 9, source: '', message: MESSAGE])
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
            [line: 11, source: '', message: MESSAGE],
            [line: 14, source: '', message: MESSAGE],
            [line: 17, source: '', message: MESSAGE],
            [line: 19, source: '', message: MESSAGE],
            [line: 21, source: '', message: MESSAGE],
            [line: 23, source: '', message: MESSAGE],
            [line: 25, source: '', message: MESSAGE],
            [line: 27, source: '', message: MESSAGE],
            [line: 32, source: '', message: MESSAGE],
            [line: 34, source: '', message: MESSAGE],
            [line: 36, source: '', message: MESSAGE])
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
            [line: 4, source: '', message: MESSAGE],
            [line: 8, source: '', message: MESSAGE])
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
                [line: 7, source: '', message: MESSAGE],
                [line: 10, source: '', message: MESSAGE],
                [line: 13, source: '', message: MESSAGE],
                [line: 17, source: '', message: MESSAGE],
                [line: 21, source: '', message: MESSAGE],
                [line: 25, source: '', message: MESSAGE],
                [line: 30, source: '', message: MESSAGE])
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

        if (GroovyVersion.isGroovyVersion2()) {
            assertNoViolations(SOURCE)
        } else {
            assertViolations(SOURCE,
                    [line: 7, source: '', message: MESSAGE],
                    [line: 15, source: '', message: MESSAGE])
        }
    }

    @Override
    protected BlockEndsWithBlankLineRule createRule() {
        new BlockEndsWithBlankLineRule()
    }
}
