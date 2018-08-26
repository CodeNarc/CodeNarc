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
 * Tests for ClassEndsWithBlankLineBeforeClosingBraceRequiredRule
 *
 * @author David Ausín
 */
class RequireClassEndsWithBlankLineBeforeClosingBraceRuleTest extends AbstractRuleTestCase<ClassEndsWithBlankLineBeforeClosingBraceRequiredRule> {

    static skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassEndsWithBlankLineBeforeClosingBraceRequired'
    }

    @Test
    void testViolationsWithSingleClass() {
        //given:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }

            }
        '''

        rule.blankLineBeforeClosingBrace = true
        //expect:
        assertNoViolations(SOURCE)

        //and if:
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertSingleViolation(SOURCE, 8, '')
    }


    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLine() {
        //given:
        final String SOURCE = '''
        class Foo {
            int a
            
            void hi() {

            }        }
        '''

        rule.blankLineBeforeClosingBrace = true
        //expect:
        assertSingleViolation(SOURCE, 7, '            }        }')

        //and if:
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationWithSingleClassWhenThereIsALineBeforeClosingBraceButItIsNotBlank() {
        //given:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                
            }
        '''

        rule.blankLineBeforeClosingBrace = true
        //expect:
        assertSingleViolation(SOURCE, 8, '            }')

        //and if:
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertSingleViolation(SOURCE, 8, '            }')
    }

    @Test
    void testNoViolationsWithSeveralClassesWhenBlankLineIsRequiredBeforeClassClosingBrace() {
        //given:
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
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSeveralClassesWhenBlankLineIsNotRequiredBeforeClassClosingBrace() {
        //given:
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
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 8,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'],
                [lineNumber: 15,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'])
    }

    @Test
    void testViolationWithSeveralClasses() {
        //given:
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
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 7,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'],
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'])

        //and if:
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }


    @Test
    void testNoViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsRequired() {
        //given:
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
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //given:
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
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 13,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'],
                [lineNumber: 15,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'])
    }

    @Test
    void testViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsRequired() {
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
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 13,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'],
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'])
    }

    @Test
    void testNoViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsRequired() {
        //given:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                static class Bar {
                    int a
                
                    void hi() {
                    }

                }

            }
        '''
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertNoViolations(SOURCE)
    }

  @Test
    void testNoViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //given:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                static class Bar {
                    int a
                
                    void hi() {
                    }
                }
            }
        '''
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsRequired() {
        //setup:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                
                static class Bar {
                    int a
                
                    void hi() {
                    }
                }
            }
        '''
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 13,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'],
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class does not end with a blank line before the closing brace'])
    }

    @Test
    void testViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //setup:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }
                
                static class Bar {
                    int a
                
                    void hi() {
                    }
                    
                }
                
            }
        '''
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertViolations(SOURCE,
                [lineNumber: 14,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'],
                [lineNumber: 16,
                 sourceLineText: '            }',
                 messageText: 'Class ends with an empty line before the closing brace'])
    }

    @Test
    void testNoViolationsWithSingleLineClassesAllowed() {
        //given:
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }
            
            class Doe extends Bar<String> { }
        '''
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertNoViolations(SOURCE)

        //and if:
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSingleLineClassesNotAllowedWhenBlankLineBeforeClosingBraceIsRequired() {
        //given:
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
        '''

        rule.singleLineClassesAllowed = false
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertViolations(SOURCE,
                [lineNumber    : 3,
                 sourceLineText: 'class Foo extends Bar<String> { }',
                 messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 5,
                 sourceLineText: 'class Doe extends Bar<String> { }',
                 messageText   : 'Single line classes are not allowed'])
    }

    @Test
    void testNoViolationsWithSingleLineClassesNotAllowedWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //given:
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
        '''

        rule.singleLineClassesAllowed = false
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsRequired() {
        //given:
        final String SOURCE = '''
            class Foo { 
                Bar a = new Bar() {
                    
                    @Override
                    String toString() {
                        "Hello world"
                    }

                }

            }            
        '''
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //given:
        final String SOURCE = '''
            class Foo { 
                Bar a = new Bar() {
                    
                    @Override
                    String toString() {
                        "Hello world"
                    }
                }
            }            
        '''
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsRequired() {
        //given:
        final String SOURCE = '''
            class Foo { 
                Bar a = new Bar() {
                    
                    @Override
                    String toString() {
                        "Hello world"
                    }
                }

            }            
        '''
        rule.blankLineBeforeClosingBrace = true

        //expect:
        assertSingleViolation(SOURCE, 9, '                }')
    }

    @Test
    void testViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsNotRequired() {
        //given:
        final String SOURCE = '''
            class Foo { 
                Bar a = new Bar() {
                    
                    @Override
                    String toString() {
                        "Hello world"
                    }
                    
                }
            }            
        '''
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertSingleViolation(SOURCE, 10, '                }')
    }

    @Override
    protected ClassEndsWithBlankLineBeforeClosingBraceRequiredRule createRule() {
        new ClassEndsWithBlankLineBeforeClosingBraceRequiredRule()
    }
}
