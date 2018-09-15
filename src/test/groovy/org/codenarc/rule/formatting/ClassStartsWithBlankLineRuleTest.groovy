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
package org.codenarc.rule.formatting

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ClassStartsWithBlankLineRule
 *
 * @author David Ausín
 */
@SuppressWarnings('TrailingWhitespace')
class ClassStartsWithBlankLineRuleTest extends AbstractRuleTestCase<ClassStartsWithBlankLineRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ClassStartsWithBlankLine'
        assert rule.ignoreSingleLineClasses == true
        assert rule.blankLineRequired == true
    }

    @Test
    void testNoViolationsWithSingleClassWhenBlankLineIsRequired() {
        final String SOURCE = '''
            class Foo {
            
                int a
                
                void hi() {
                }

            }
        '''

        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithInterfaceClassWhenBlankLineIsRequired() {
        final String SOURCE = '''
            interface Foo {
            
                void hi()

            }
        '''

        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSingleClassWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            class Foo {
            
                int a
                void hi() {
                }

            }
        '''

        rule.blankLineRequired = false

        assertSingleViolation(SOURCE, 3, '')
    }

    @Test
    void testViolationsWithInterfaceWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            interface Foo {
                
                void hi() 

            }
        '''

        rule.blankLineRequired = false

        assertSingleViolation(SOURCE, 3, '')
    }

    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineIsRequired() {
        final String SOURCE = '''
        class Foo 
        {  int a
            
            void hi() {

            }        }
        '''

        rule.blankLineRequired = true

        assertSingleViolation(SOURCE, 3, '        {  int a')
    }

    @Test
    void testViolationsWithInterfaceWhenBlankLineIsRequired() {
        final String SOURCE = '''
        interface Foo {
            void hi()
         }
        '''

        rule.blankLineRequired = true

        assertSingleViolation(SOURCE, 3, 'void hi()')
    }

    @Test
    void testNoViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineIsNotRequired() {
        final String SOURCE = '''
        class Foo { int a
            
            void hi() {

            }        }
        '''

        rule.blankLineRequired = false

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationWithSingleClassWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            class Foo {
                
                int a
                
                void hi() {
                }

            }
        '''
        rule.blankLineRequired = false

        assertSingleViolation(SOURCE, 3, '')
    }

    @Test
    void testNoViolationsWithSeveralClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSeveralClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false

        assertViolations(SOURCE,
                [lineNumber: 3, sourceLineText: '', messageText: 'Class starts with a blank line after the opening brace'],
                [lineNumber: 9, sourceLineText: '', messageText: 'Class starts with a blank line after the opening brace'])
    }

    @Test
    void testViolationWithSeveralClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertViolations(SOURCE,
                [lineNumber    : 3, sourceLineText: '                int a', messageText   : 'Class does not start with a blank line after the opening brace'],
                [lineNumber    : 10, sourceLineText: '                int a', messageText   : 'Class does not start with a blank line after the opening brace'])
    }

    @Test
    void testNoViolationWithSeveralClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithNonStaticInnerClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithNonStaticInnerClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false

        assertViolations(SOURCE,
                [lineNumber: 3, sourceLineText: '', messageText: 'Class starts with a blank line after the opening brace'],
                [lineNumber: 8, sourceLineText: '', messageText: 'Class starts with a blank line after the opening brace'])
    }

    @Test
    void testViolationsWithNonStaticInnerClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertViolations(SOURCE,
                [lineNumber: 3, sourceLineText: 'int a', messageText: 'Class does not start with a blank line after the opening brace'],
                [lineNumber: 9, sourceLineText: 'int a', messageText: 'Class does not start with a blank line after the opening brace'])
    }

    @Test
    void testNoViolationsWithStaticInnerClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithStaticInnerClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithStaticInnerClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertViolations(SOURCE,
                [lineNumber: 3, sourceLineText: 'int a', messageText: 'Class does not start with a blank line after the opening brace'],
                [lineNumber: 9, sourceLineText: 'int a', messageText: 'Class does not start with a blank line after the opening brace'])
    }

    @Test
    void testViolationsWithStaticInnerClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false

        assertViolations(SOURCE,
                [lineNumber: 9, sourceLineText: '            ', messageText: 'Class starts with a blank line after the opening brace'])
    }

    @Test
    void testNoViolationsWithSingleLineClassesIgnoredWhenBlankLineIsRequired() {
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }
            
            class Doe extends Bar<String> { }
        '''
        rule.blankLineRequired = true
        rule.ignoreSingleLineClasses = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithSingleLineClassesIgnoredWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }
            
            class Doe extends Bar<String> { }
        '''
        rule.blankLineRequired = false
        rule.ignoreSingleLineClasses = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithSingleLineClassesNotAllowedWhenBlankLineIsRequired() {
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
            abstract class John  { abstract void a() }
        '''

        rule.ignoreSingleLineClasses = false
        rule.blankLineRequired = true

        assertViolations(SOURCE,
                [lineNumber    : 3, sourceLineText: 'class Foo extends Bar<String> { }', messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 5, sourceLineText: 'class Doe extends Bar<String> { }', messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 6, sourceLineText: 'abstract class John  { abstract void a() }', messageText   : 'Single line classes are not allowed'])
    }

    @Test
    void testNoViolationsWithSingleLineClassesNotAllowedWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
            abstract class John  { abstract void a() }
        '''

        rule.ignoreSingleLineClasses = false
        rule.blankLineRequired = false

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithAnonymousClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithAnonymousClassesWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            class Foo { Bar a = new Bar() {
                    @Override
                    String toString() {
                        "Hello world"
                    }
                }
            }            
        '''
        rule.blankLineRequired = false

        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithAnonymousClassesWhenBlankLineIsRequired() {
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
        rule.blankLineRequired = true

        assertSingleViolation(SOURCE, 5, '@Override')
    }

    @Test
    void testViolationsWithAnonymousClassesWhenBlankLineIsNotRequired() {
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
        rule.blankLineRequired = false
        assertSingleViolation(SOURCE, 4, '')
    }

    @Override
    protected ClassStartsWithBlankLineRule createRule() {
        new ClassStartsWithBlankLineRule()
    }
}
