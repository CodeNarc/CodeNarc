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
 * Tests for ClassEndsWithBlankLineRule
 *
 * @author David Ausín
 */
@SuppressWarnings('TrailingWhitespace')
class ClassEndsWithBlankLineRuleTest extends AbstractRuleTestCase<ClassEndsWithBlankLineRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClassEndsWithBlankLine'
        assert rule.ignoreSingleLineClasses == true
        assert rule.ignoreInnerClasses == false
        assert rule.blankLineRequired == true
    }

    @Test
    void testViolationsWithSingleClassWhenBlankLineIsRequired() {
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
    void testViolationsWithInterfaceClassWhenBlankLineIsRequired() {
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

        assertSingleViolation(SOURCE, 8, '')
    }

    @Test
    void testViolationsWithInterfaceWhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            interface Foo {
                
                void hi() 

            }
        '''

        rule.blankLineRequired = false

        assertSingleViolation(SOURCE, 6, '')
    }

    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineIsRequired() {
        final String SOURCE = '''
        class Foo {
            int a
            
            void hi() {

            }        }
        '''

        rule.blankLineRequired = true

        assertSingleViolation(SOURCE, 7, '            }        }')
    }

    @Test
    void testViolationsWithInterfaceWhenBraceIsNotInANewLineAndBlankLineIsRequired() {
        final String SOURCE = '''
        interface Foo {
            
            void hi()
         }
        '''

        rule.blankLineRequired = true

        assertSingleViolation(SOURCE, 5, '}')
    }

    @Test
    void testViolationsWithSingleClassWhenBraceIsNotInANewLineAndBlankLineIsNotRequired() {
        final String SOURCE = '''
        class Foo {
            int a
            
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

        assertSingleViolation(SOURCE, 8, '            }')
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
                [line: 8, source: '            }', message: 'Class ends with an empty line before the closing brace'],
                [line: 15, source: '            }', message: 'Class ends with an empty line before the closing brace'])
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
                [lineNumber    : 7, source: '            }', messageText   : 'Class does not end with a blank line before the closing brace'],
                [lineNumber    : 14, source: '            }', messageText   : 'Class does not end with a blank line before the closing brace'])
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
                [line: 13, source: '            }', message: 'Class ends with an empty line before the closing brace'],
                [line: 15, source: '            }', message: 'Class ends with an empty line before the closing brace'])
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
                [line: 13, source: '            }', message: 'Class does not end with a blank line before the closing brace'],
                [line: 14, source: '            }', message: 'Class does not end with a blank line before the closing brace'])
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
                [line: 13, source: '            }', message: 'Class does not end with a blank line before the closing brace'],
                [line: 14, source: '            }', message: 'Class does not end with a blank line before the closing brace'])
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
                [line: 14, source: '            }', message: 'Class ends with an empty line before the closing brace'],
                [line: 16, source: '            }', message: 'Class ends with an empty line before the closing brace'])
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
                [lineNumber    : 3, source: 'class Foo extends Bar<String> { }', messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 5, source: 'class Doe extends Bar<String> { }', messageText   : 'Single line classes are not allowed'],
                [lineNumber    : 6, source: 'abstract class John  { abstract void a() }', messageText   : 'Single line classes are not allowed'])
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

        assertSingleViolation(SOURCE, 9, '                }')
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
        assertSingleViolation(SOURCE, 10, '                }')
    }

    @Test
    void testNoViolationsForCodeThatOnlyContainsASemicolon() {
        final String SOURCE = ';'

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsForCommentedOutClass() {
        final String SOURCE = '''
            //class Foo {
            //    int count                
            //}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsForScriptClass() {
        final String SOURCE = '''
            job('test-job') {
                triggers { // fails here, no new line above
                    cron('* * * * *')
                }
            
                steps {
                    groovyScriptFile('my_script.groovy')
                }
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void test_InnerClasses_ignoreInnerClasses_True_NoViolations() {
        final String SOURCE = '''
            class Foo {
                // Static inner class
                private static class StaticInnerClass {
                }

                // Inner class
                private class InnerClass {
                }

                // Anonymous inner class            
                Bar a = new Bar() {
                }

            }            
        '''

        rule.ignoreInnerClasses = true
        assertNoViolations(SOURCE)
    }

    @Override
    protected ClassEndsWithBlankLineRule createRule() {
        new ClassEndsWithBlankLineRule()
    }
}
