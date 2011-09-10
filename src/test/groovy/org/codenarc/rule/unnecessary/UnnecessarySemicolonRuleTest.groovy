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
import org.codenarc.rule.Rule

/**
 * Tests for UnnecessarySemicolonRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessarySemicolonRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessarySemicolon'
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testSuccessScenario() {
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

            println(value); // comment so no violation

            @SuppressWarnings('UnnecessarySemicolon')
            def method() {
                ;
            }

        '''
        assertNoViolations(SOURCE)
    }

    void testSimpleString() {
        final SOURCE = """
            def string = 'hello world';
            """
        assertSingleViolation SOURCE, 2, "def string = 'hello world';"
    }
    void testSemiColonInMultilineString() {
        final SOURCE = """
            def javascript = '''
                // this next semicolon is ignored
                window.alert("some embedded javascript...");
            // this next semicolon is not ignored!
            ''';
            """
        assertSingleViolation SOURCE, 6, '            \'\'\';'
    }

    void testSemiColonInGStringString() {
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

    @SuppressWarnings('UnnecessarySemicolon')
    void testPackage() {
        final SOURCE = '''
            package my.company.server;
        '''
        assertSingleViolation(SOURCE, 2, 'package my.company.server;', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testLoop() {
        final SOURCE = '''
            for (def x : list);
        '''
        assertSingleViolation(SOURCE, 2, 'for (def x : list);', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testMethodCall() {
        final SOURCE = '''
            println(value) ;
        '''
        assertSingleViolation(SOURCE, 2, 'println(value) ;', 'Semi-colons as line endings can be removed safely')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    void testImport() {
        final SOURCE = '''
            import java.lang.String;    
        '''
        assertSingleViolation(SOURCE, 2, 'import java.lang.String;', 'Semi-colons as line endings can be removed safely')
    }

    protected Rule createRule() {
        new UnnecessarySemicolonRule()
    }
}