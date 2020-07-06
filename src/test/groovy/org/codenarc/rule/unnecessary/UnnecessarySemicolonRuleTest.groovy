/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for UnnecessarySemicolonRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class UnnecessarySemicolonRuleTest extends AbstractRuleTestCase<UnnecessarySemicolonRule> {

    private static final String MESSAGE = 'Semicolons as line endings can be removed safely'

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySemicolon'
    }

    @Test
    void test_NoViolations() {
        final SOURCE = '''
/*
 *
 * (the "License");
* (the "License");
   * (the "License");
 * you may not use this file except in compliance with the License.
 *
 */

            //comment is not a violation;

            package foo
            import java.lang.String
            println(value)
            println(value); println (otherValue)

            @SuppressWarnings('UnnecessarySemicolon')
            def method() {
                ;
            }

            @SuppressWarnings('UnnecessarySemicolon')
            class A {
                String a = 'text';
            }

            def anotherMethod() {
                for(int i=0; i < 100; i++) {
                    println i
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_SemicolonFollowedByComment() {
        final SOURCE = '''
            println(value); // comment
        '''
        assertSingleViolation(SOURCE, 2, 'println(value); // comment', MESSAGE)
    }

    @Test
    void test_SimpleString() {
        final SOURCE = """
            def string = 'hello world';
            """
        assertSingleViolation SOURCE, 2, "def string = 'hello world';"
    }
    @Test
    void test_SemicolonInMultilineString() {
        final SOURCE = """
            def javascript = '''
                // this next semicolon is ignored
                window.alert("some embedded javascript...");
            // this next semicolon is not ignored!
            ''';
            """
        assertSingleViolation SOURCE, 6, '            \'\'\';'
    }

    @Test
    void test_SemicolonInGStringString() {
        final SOURCE = '''
            def var = 'yo yo yo'
            def javascript = """
                // this next semicolon is ignored
                window.alert($var);
            // this next semicolon is not ignored!
            """;
            '''
        assertSingleViolation SOURCE, 7, '            """;'
    }

    @Test
    void test_Package() {
        final SOURCE = '''
            package my.company.server;
        '''
        assertSingleViolation(SOURCE, 2, 'package my.company.server;', MESSAGE)
    }

    @Test
    void test_ForLoop() {
        final SOURCE = '''
            for (def x : list);
        '''
        assertSingleViolation(SOURCE, 2, 'for (def x : list);', MESSAGE)
    }

    @Test
    void test_MethodCall() {
        final SOURCE = '''
            println(value);
        '''
        assertSingleViolation(SOURCE, 2, 'println(value);', MESSAGE)
    }

    @Test
    void test_Imports() {
        final SOURCE = '''
            import java.net.*;
            import java.lang.String;

            import org.SomeOther                    // no violation
            import org.util.*                       // no violation
            import static org.SomeUtil.*            // no violation
            import static org.SomeUtil.doStuff      // no violation

            import static java.lang.Math.*;
            import static org.other.OtherUtil.doStuff;
        '''
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'import java.net.*;', messageText:MESSAGE],
                [lineNumber:3, sourceLineText:'import java.lang.String;', messageText:MESSAGE],
                [lineNumber:10, sourceLineText:'import static java.lang.Math.*;', messageText:MESSAGE],
                [lineNumber:11, sourceLineText:'import static org.other.OtherUtil.doStuff;', messageText:MESSAGE],
        )
    }

    @Test
    void test_PackageAndImports_NoViolations() {
        final SOURCE = '''
            package foo

            import java.io.CharArrayReader
            import java.nio.file.AccessMode
            import java.io.Bits

            import java.lang.String

            class A {
                String firstName

                int a() {
                    return 1
                }
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void test_Class() {
        final SOURCE = '''
            class A {
                int a() {
                    return 1;
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'return 1;', MESSAGE)
    }

    @Test
    void test_Field() {
        final SOURCE = '''
            class A {
                String name;
                def value = new Object(); // comment
                def closure = { };
                int count;//comment
            }
        '''
        assertViolations(SOURCE,
                [lineNumber:3, sourceLineText:'String name;', messageText:MESSAGE],
                [lineNumber:4, sourceLineText:'def value = new Object();', messageText:MESSAGE],
                [lineNumber:5, sourceLineText:'def closure = { };', messageText:MESSAGE],
                [lineNumber:6, sourceLineText:'int count;', messageText:MESSAGE],
        )
    }

    @Test
    void test_SemicolonInMultilineCommentsWithoutLeadingAsterisk() {
        final SOURCE = '''
        /*
         (the "License");
         (the "License");
         (the "License");
         you may not use this file except in compliance with the License.
         */

         /*
          have a multiline comment
          */ /*  and start a new multiline comment on the previous ending line;
          though i doubt this is a good thing
          */

        '''
        assert !manuallyApplyRule(SOURCE)
    }

    @Test
    void test_MultilineCommentWrittenAsASingleLine() {
        final SOURCE = '''
        /* no semi colon here */
        println("raccoon");
        '''
        assertSingleViolation(SOURCE, 3, 'println("raccoon");', MESSAGE)
    }

    @Override
    protected UnnecessarySemicolonRule createRule() {
        new UnnecessarySemicolonRule()
    }
}
