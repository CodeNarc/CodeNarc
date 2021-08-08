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

    private static final String VIOLATION_BLANK_LINE_NOT_ALLOWED = 'Class starts with a blank line after the opening brace'
    private static final String VIOLATION_MISSING_BLANK_LINE = 'Class does not start with a blank line after the opening brace'

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClassStartsWithBlankLine'
        assert rule.ignoreSingleLineClasses == true
        assert rule.ignoreInnerClasses == false
        assert rule.blankLineRequired == true
    }

    @Test
    void test_BlankLineIsRequired() {
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
    void test_InterfaceClass_BlankLineIsRequired() {
        final String SOURCE = '''
            interface Foo {
            
                void hi()

            }
        '''
        rule.blankLineRequired = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_BlankLineIsNotRequired() {
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
    void test_Interface_BlankLineIsNotRequired() {
        final String SOURCE = '''
            interface Foo {
                
                void hi() 

            }
        '''
        rule.blankLineRequired = false
        assertSingleViolation(SOURCE, 3, '')
    }

    @Test
    void test_BraceIsNotInANewLine_BlankLineIsRequired() {
        final String SOURCE = '''
        class Foo 
        {  int a
            
            void hi() {

            }        }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 3, '{  int a')
    }

    @Test
    void test_CommentLine_BlankLineIsRequired() {
        final String SOURCE = '''
        interface Foo {
            // some comment
            
            void hi()
         }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 3, '// some comment')
    }

    @Test
    void test_Interface_BlankLineIsRequired() {
        final String SOURCE = '''
        interface Foo {
            void hi()
         }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 3, 'void hi()')
    }

    @Test
    void test_BraceIsNotInANewLine_BlankLineIsNotRequired() {
        final String SOURCE = '''
        class Foo { int a
            
            void hi() {

            }        }
        '''
        rule.blankLineRequired = false
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SeveralClasses_BlankLineIsRequired_NoViolations() {
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
    void test_SeveralClasses_BlankLineIsNotRequired() {
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
                [line: 3, source: '', message: VIOLATION_BLANK_LINE_NOT_ALLOWED],
                [line: 9, source: '', message: VIOLATION_BLANK_LINE_NOT_ALLOWED])
    }

    @Test
    void test_SeveralClasses_BlankLineIsRequired() {
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
                [line: 3, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE],
                [line: 10, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE])
    }

    @Test
    void test_SeveralClasses_BlankLineIsNotRequired_NoViolations() {
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
    void test_NonStaticInnerClasses_BlankLineIsRequired_NoViolations() {
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
    void test_NonStaticInnerClasses_BlankLineIsNotRequired() {
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
                [line: 3, source: '', message: VIOLATION_BLANK_LINE_NOT_ALLOWED],
                [line: 8, source: '', message: VIOLATION_BLANK_LINE_NOT_ALLOWED])
    }

    @Test
    void test_NonStaticInnerClasses_BlankLineIsRequired() {
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
                [line: 3, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE],
                [line: 9, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE])
    }

    @Test
    void test_StaticInnerClasses_BlankLineIsRequired_NoViolations() {
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
    void test_StaticInnerClasses_BlankLineIsNotRequired_NoViolations() {
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
    void test_StaticInnerClasses_BlankLineIsRequired() {
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
                [line: 3, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE],
                [line: 9, source: 'int a', message: VIOLATION_MISSING_BLANK_LINE])
    }

    @Test
    void test_StaticInnerClasses_BlankLineIsNotRequired() {
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
                [line: 9, source: '', message: VIOLATION_BLANK_LINE_NOT_ALLOWED])
    }

    @Test
    void test_SingleLineClassesIgnored_BlankLineIsRequired() {
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
    void test_SingleLineClassesIgnored_BlankLineIsNotRequired() {
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
    void test_SingleLineClassesNotAllowed_BlankLineIsRequired() {
        final String SOURCE = '''
            import my.company.Bar
            class Foo extends Bar<String> { }

            class Doe extends Bar<String> { }
            abstract class John  { abstract void a() }
        '''
        rule.ignoreSingleLineClasses = false
        rule.blankLineRequired = true
        assertViolations(SOURCE,
                [line: 3, source: 'class Foo extends Bar<String> { }', message: 'Single line classes are not allowed'],
                [line: 5, source: 'class Doe extends Bar<String> { }', message: 'Single line classes are not allowed'],
                [line: 6, source: 'abstract class John  { abstract void a() }', message: 'Single line classes are not allowed'])
    }

    @Test
    void test_SingleLineClassesNotAllowed_BlankLineIsNotRequired() {
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
    void test_AnonymousClasses_BlankLineIsRequired_NoViolations() {
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
    void test_AnonymousClasses_BlankLineIsNotRequired_NoViolations() {
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
    void test_AnonymousClasses_BlankLineIsRequired() {
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
    void test_AnonymousClasses_BlankLineIsNotRequired() {
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

    @Test
    void test_OnlyContainsASemicolon() {
        final String SOURCE = ';'
        assertNoViolations(SOURCE)
    }

    @Test
    void test_ScriptClass_NoViolations() {
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

    @Test
    void test_AnnotationContainsOpeningBrace_NoViolations() {
        final String SOURCE = '''
            @Requires({ sys[test] == 'dummy' })
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
    void test_AnnotationContainsOpeningBrace() {
        final String SOURCE = '''
            @Requires({ sys[test] == 'dummy' })
            class Foo {
                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 4, '')
    }

    @Test
    void test_SingleClassWithSimpleAnnotation_NoViolations() {
        final String SOURCE = '''
            @ToString
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
    void test_SingleClassWithSimpleAnnotation() {
        final String SOURCE = '''
            @ToString
            class Foo {
                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 4, '')
    }

    @Test
    void test_AnnotationContainsOpeningBraceAndOnSameLineAsClass() {
        final String SOURCE = '''
            @Requires({ sys[test] == 'dummy' }) class Foo {

                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationContainsOpeningBraceAndOnSameLineAsClass_WithTwoAnnotations() {
        final String SOURCE = '''
            @Stuff @Requires({ sys[test] == 'dummy' }) class Foo {

                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationContainsOpeningBraceAndOnSameLineAsClass_WithTwoAnnotations_ReverseOrderingOfAnnotations_NoViolations() {
        final String SOURCE = '''
            @Requires({ sys[test] == 'dummy' }) @Stuff  class Foo {

                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AnnotationContainsOpeningBraceAndOnSameLineAsClass_WithTwoAnnotations_WhenBlankLineIsNotRequired() {
        final String SOURCE = '''
            @Stuff @Requires({ sys[test] == 'dummy' }) class Foo {

                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = false
        assertSingleViolation(SOURCE, 3, '')
    }

    @Test
    void test_AnnotationContainsAnotherOpeningBraceOnSameLineAsClass_Violation() {
        final String SOURCE = '''
            @Requires({ sys[test] == 'dummy' }) class Foo {
                int a

                void hi() {
                }

            }
        '''
        rule.blankLineRequired = true
        assertSingleViolation(SOURCE, 3, 'int a')
    }

    @Test
    void test_Annotations_FollowedByBlankLine() {
        final String SOURCE = '''
            @Path("/myService")
            @Produces(MediaType.APPLICATION_XML)
             
            class MyService {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SingleLineClass_NoNewLineAtEndOfFile_NoViolations() {
        final String SOURCE = '''
            @groovy.transform.InheritConstructors
            class AppException extends Exception {}'''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_VeryLongInterfaceDeclaration_NoViolations() {
        final String SOURCE = '''
            interface SomeLongCollectionNameWhichIsHardToRenameWithoutUpdatingTheWholeProjectRepository 
                extends MongoRepository<SomeLongCollectionNameWhichIsHardToRenameWithoutUpdatingTheWholeProject, String>,
                        SomeLongCollectionNameWhichIsHardToRenameWithoutUpdatingTheWholeProjectRepositoryExtension { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_VeryLongClassDeclaration_NoViolations() {
        final String SOURCE = '''
            class SomeLongCollectionNameWhichIsHardToRenameWithoutUpdatingTheWholeProjectRepositoryImpl extends BaseRepositoryExtension
                implements SomeLongCollectionNameWhichIsHardToRenameWithoutUpdatingTheWholeProjectRepositoryExtension {
            
                @Override
                void customRepositoryMethod() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ClassStartsWithBlankLineRule createRule() {
        new ClassStartsWithBlankLineRule()
    }
}
