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
class ClassEndsWithBlankLineBeforeClosingBraceRequiredRuleTest extends AbstractRuleTestCase<ClassEndsWithBlankLineBeforeClosingBraceRequiredRule> {

    static skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassEndsWithBlankLineBeforeClosingBraceRequired'
    }

    @Test
    void testViolationsWithSingleClassWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    }

    @Test
    void testViolationsWithInterfaceClassWhenBlankLineBeforeClosingBraceIsMandatory() {
        //given:
        final String SOURCE = '''
            interface Foo {
 
                void hi()

            }
        '''

        rule.blankLineBeforeClosingBrace = true
        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSingleClassWhenBlankLineBeforeClosingBraceIsForbidden() {
        //given:
        final String SOURCE = '''
            class Foo {
                int a
                
                void hi() {
                }

            }
        '''

        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertSingleViolation(SOURCE, 8, '')
    }

    @Test
    void testViolationsWithInterfaceWhenBlankLineBeforeClosingBraceIsForbidden() {
        //given:
        final String SOURCE = '''
            interface Foo {
                
                void hi() 

            }
        '''

        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertSingleViolation(SOURCE, 6, '')
    }

    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineBeforeClosingBraceIsMandatory() {
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
    }

    @Test
    void testViolationsWithInterfaceWhenBraceIsNotInANewLineAndBlankLineBeforeClosingBraceIsMandatory() {
        //given:
        final String SOURCE = '''
        interface Foo {
            
            void hi()
         }
        '''

        rule.blankLineBeforeClosingBrace = true
        //expect:
        assertSingleViolation(SOURCE, 5, '}')
    }
    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineBeforeClosingBraceIsForbidden() {
        //given:
        final String SOURCE = '''
        class Foo {
            int a
            
            void hi() {

            }        }
        '''

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
            interface Bar {
                
                void hi()
                        
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
    void testViolationWithSeveralClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
                [lineNumber    : 7,
                 sourceLineText: '            }',
                 messageText   : 'Class does not end with a blank line before the closing brace'],
                [lineNumber    : 14,
                 sourceLineText: '            }',
                 messageText   : 'Class does not end with a blank line before the closing brace'])
    }

    @Test
    void testNoViolationWithSeveralClassesWhenBlankLineBeforeClosingBraceIsForbidden() {
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
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsForbidden() {
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
    void testViolationsWithNonStaticInnerClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testNoViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testNoViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsNotForbidden() {
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
    void testViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testViolationsWithStaticInnerClassesWhenBlankLineBeforeClosingBraceIsForbidden() {
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
    void testViolationsWithSingleLineClassesNotAllowedWhenBlankLineBeforeClosingBraceIsMandatory() {
        //given:
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
            abstract class John  { abstract void a() }
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
                 messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 6,
                 sourceLineText: 'abstract class John  { abstract void a() }',
                 messageText   : 'Single line classes are not allowed'])
    }

    @Test
    void testNoViolationsWithSingleLineClassesNotAllowedWhenBlankLineBeforeClosingBraceIsForbidden() {
        //given:
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
            abstract class John  { abstract void a() }
        '''

        rule.singleLineClassesAllowed = false
        rule.blankLineBeforeClosingBrace = false

        //expect:
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testNoViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsForbidden() {
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
    void testViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsMandatory() {
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
    void testViolationsWithAnonymousClassesWhenBlankLineBeforeClosingBraceIsForbidden() {
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
