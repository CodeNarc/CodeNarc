/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test
/**
 * Tests for NestedForLoopRule
 *
 * @author Maciej Ziarko
 */
class NestedForLoopRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'NestedForLoop'
    }

    @Test
    void testNoViolations() {
        assertNoViolations('''
            class TestClass {
                void methodWithoutNestedForLoops() {
                    for (int i = 0; i < 100; ++i) {
                        println i
                    }
                    for (int i = 0; i < 200; ++i) {
                        println i
                    }
                }
            }
        ''')
    }

    @Test
    void testSingleViolation() {
        assertInlineViolations("""
            class TestClass {
                void methodWithNestedForLoops() {
                    for (int i = 0; i < 100; ++i) {
                        for (int j = 0; j < 100; ++j) { ${violation()}
                            println i + j
                        }
                    }
                }
            }
        """)
    }

    @Test
    void testMultipleViolations_2ForLoopsInsideFor() {
        assertInlineViolations("""
            class TestClass {
                void methodWithNestedForLoops() {
                    for (int i = 0; i < 100; ++i) {
                        for (int j = 0; j < 100; ++j) { ${violation()}
                            println i + j
                        }
                        for (int j = 0; j < 100; ++j) { ${violation()}
                            println i + j
                        }
                    }
                }
            }
        """)
    }

    @Test
    void testMultipleViolations_ForInsideForInsideFor() {
        assertInlineViolations("""
            class TestClass {
                void methodWithNestedForLoops() {
                    for (int i = 0; i < 100; ++i) {
                        for (int j = 0; j < 100; ++j) { ${violation()}
                            for (int k = 0; k < 100; ++k) { ${violation()}
                                println i + j + k
                            }
                        }
                    }
                }
            }
        """)
    }

    protected Rule createRule() {
        new NestedForLoopRule()
    }

    private String violation() {
        return inlineViolation('Nested for loop')
    }
}
