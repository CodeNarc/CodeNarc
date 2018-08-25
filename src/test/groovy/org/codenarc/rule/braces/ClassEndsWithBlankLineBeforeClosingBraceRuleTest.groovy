/*
 * Copyright 2018 the original author or authors.
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
package org.codenarc.rule.braces

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ClassEndsWithBlankLineBeforeClosingBraceRule
 *
 * @author David Ausín
 */
class ClassEndsWithBlankLineBeforeClosingBraceRuleTest extends AbstractRuleTestCase<ClassEndsWithBlankLineBeforeClosingBraceRule> {

    static skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassEndsWithBlankLineBeforeClosingBrace'
    }

    @Test
    void testNoViolationsWithSingleClass() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationWithSingleClassWhenThereIsALineBeforeClosingBraceButItIsNotBlank() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                
            }
        '''
        assertSingleViolation(SOURCE, 8, '            }')
    }

    @Test
    void testNoViolationsWithSeveralClasses() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }

            }
            class Bar {
                int a
                
                void hi() {
                }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationWithSeveralClasses() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }   
            }
            
            class Bar {
                int a
                
                void hi() {
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 7,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'],
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'])
    }


    @Test
    void testNoViolationsWithInnerClasses() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                class Bar {
                    int a
                
                    void hi() {
                    }

                }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithInnerClasses() {
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                
                class Bar {
                    int a
                
                    void hi() {
                    }
                }
            }
        '''
        assertViolations(SOURCE,
                [lineNumber: 13,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'],
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'])
    }

    @Override
    protected ClassEndsWithBlankLineBeforeClosingBraceRule createRule() {
        new ClassEndsWithBlankLineBeforeClosingBraceRule()
    }
}
