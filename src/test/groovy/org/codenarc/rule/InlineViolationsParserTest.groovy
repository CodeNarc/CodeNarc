/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule

import org.junit.Test

import static org.codenarc.rule.InlineViolationsParser.inlineViolation

/**
 * Tests for InlineViolationsParser
 *
 * @author Artur Gajowy
 */
class InlineViolationsParserTest {

    @Test
    void testFindsNoViolations() {
        def source = '''
            class NoViolations {
            }
        '''
        assertParse(source, [], source)
    }

    @Test
    void testFindsSingleViolation() {
        def violatingLine = /println 'Hello, World!'/
        def violationMessage = /Use a logger instead of a println!/
        assertParse(
            violatingLine + ' ' + "#$violationMessage", 
            [createViolation(1, violatingLine, violationMessage)],
            violatingLine
        )
    }

    @Test
    void testFindsSingleViolationWhenUsingInlineViolationMethod() {
        def violatingLine = /println 'Hello, World!'/
        def violationMessage = /Use a logger instead of a println!/
        assertParse(
            violatingLine + ' ' + "${inlineViolation(violationMessage)}",
            [createViolation(1, violatingLine, violationMessage)],
            violatingLine
        )
    }

    @Test
    void testFindsViolationsInMultipleLines() {
        assertParse("""
            class TwoViolations {   ${inlineViolation('violation 1')}
                String foo
            }                       ${inlineViolation('violation 2')}
        """, [
            createViolation(2, 'class TwoViolations {', 'violation 1'),
            createViolation(4, '}', 'violation 2'),
        ], '''
            class TwoViolations {           
                String foo                
            }                                              
        ''')
    }

    @Test
    void testFindsMultipleViolationsPerLine() {
        assertParse("""
            class TwoViolations {   ${inlineViolation('violation 1')}${inlineViolation('violation 2')}
                String foo          ${inlineViolation('violation 3')}       ${inlineViolation('violation 4')}    
            }                       
        """, [
            createViolation(2, 'class TwoViolations {', 'violation 1'),
            createViolation(2, 'class TwoViolations {', 'violation 2'),
            createViolation(3, 'String foo', 'violation 3'),
            createViolation(3, 'String foo', 'violation 4'),
        ], '''
            class TwoViolations {   
                String foo          
            }                         
        ''')
    }

    @Test
    void testFindsNoViolationsInEmptySource() {
        String source = ''
        assertParse(source, [], source)
    }

    @Test
    void testFindsNoViolationsInAllWhitespaceSource() {
        String source = '''

        '''
        assertParse(source, [], source)
    }

    @Test
    @SuppressWarnings('ConsecutiveBlankLines')
    void testFindsViolationsInOtherwiseEmptySource() {
        assertParse("""
            #violation 1
            ${inlineViolation('violation 2')}
        """, [
            createViolation(2, '', 'violation 1'),
            createViolation(3, '', 'violation 2')
        ], '''

        
        ''')
    }

    @Test
    void testViolationMessagesCanContainEscapedHash() {
        def violatingLine = /def penguin/
        def violationMessage = /'penguin' is a swearword. Ask on #kernelnewbies why./
        assertParse(
            "$violatingLine    #'penguin' is a swearword. Ask on \\#kernelnewbies why.",
            [createViolation(1, violatingLine, violationMessage)],
            violatingLine
        )
    }

    @Test
    void testViolationMessagesAreEscapedByInlineViolationMethod() {
        def violatingLine = /def penguin/
        def violationMessage = /'penguin' is a swearword. Ask on #kernelnewbies why./
        assertParse(
            "$violatingLine    ${inlineViolation(violationMessage)}",
            [createViolation(1, violatingLine, violationMessage)],
            violatingLine
        )
    }

    @Test
    void testBackslashAtViolationMessageEndDoesNotEscapeNextViolation() {
        def messageEndingWithBackslash = 'It\'s a trap!\\'
        def anotherMessage = 'Yep, I know...'
        assertParse(
            "${inlineViolation(messageEndingWithBackslash)}#$anotherMessage",
            [
                createViolation(1, '', messageEndingWithBackslash),
                createViolation(1, '', anotherMessage)
            ],
            ''
        )
    }
    
    @Test
    void testNoViolationsInScriptSource() {
        def source = 
        '''#!/usr/bin/groovy
        println 'All well!'
        '''
        assertParse(source, [], source)
    }
    
    @Test
    void testViolationsInScriptSource() {
        assertParse(
            """#!/usr/bin/groovy    #Hardcoded groovy location! Use `\\#!/usr/bin/env groovy` instead!      #Really do! 
            println "All well"      ${inlineViolation('v1')}${inlineViolation('v2')}    
                                    #v3#v4
            """,
            [
                createViolation(1, '#!/usr/bin/groovy', 'Hardcoded groovy location! Use `#!/usr/bin/env groovy` instead!'),
                createViolation(1, '#!/usr/bin/groovy', 'Really do!'),
                createViolation(2, 'println "All well"', 'v1'),
                createViolation(2, 'println "All well"', 'v2'),
                createViolation(3, '', 'v3'),
                createViolation(3, '', 'v4'),
            ], 
            '''#!/usr/bin/groovy
            println "All well"  
                        
            '''
        )
    }

    @Test
    void testOnlyFirstTwoCharactersOfScriptTreatedAsShebang() {
        def violationMessage = '!/user/bin/groovy'
        def shebang = "#$violationMessage"
        assertParse(" $shebang", [createViolation(1, '', violationMessage)], '')
        assertParse("\n$shebang", [createViolation(2, '', violationMessage)], '\n')
    }

    private Map createViolation(int lineNumber, String sourceLineText, String messageText) {
        return [lineNumber: lineNumber, sourceLineText: sourceLineText, messageText: messageText]
    }

    private void assertParse(String source, List<Map> expectedViolations, String expectedSourceWithoutMarkers) {
        def expectedSource = removeTrailingWhitespace(expectedSourceWithoutMarkers)
        def parser = new InlineViolationsParser()
        def result = parser.parse(source)
        assert result.violations == expectedViolations
        assert result.source == expectedSource
    }

    private String removeTrailingWhitespace(String lines) {
        lines.replaceAll(~/(?m)[ \t\f]*$/, '')
    }
}
