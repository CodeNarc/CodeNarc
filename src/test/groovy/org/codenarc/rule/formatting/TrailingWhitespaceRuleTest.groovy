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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for TrailingWhitespaceRule
 *
 * @author Joe Sondow
 */
class TrailingWhitespaceRuleTest extends AbstractRuleTestCase<TrailingWhitespaceRule> {

    private static final String MESSAGE = 'Line ends with whitespace characters'

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'TrailingWhitespace'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            package org.codenarc

            class MyClass {

                def go() { /* ... */ }
                def goSomewhere() { /* ... */ }

            }
        '''.trim()
        assertNoViolations(SOURCE)
    }

    @Test
    void testLineEndingWithMultipleSpaces() {
        final SOURCE = 'class MyClass {}    \n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {}    ', MESSAGE)
    }

    @Test
    void testLineEndingWithOneSpace() {
        final SOURCE = 'class MyClass {} \n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {} ', MESSAGE)
    }

    @Test
    void testLineEndingWithATab() {
        final SOURCE = 'class MyClass {}\t\n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {}\t', MESSAGE)
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = 'package org.codenarc \n' +
            '\n' +
            'class MyClass {\t\n' +
            '\n' +
            '    def go() { /* ... */ }  \n' +
            '    \n' +
            '    def stop() { /* ... */ }\n' +
            '}\t\n'
        assertViolations(SOURCE,
            [lineNumber: 1, sourceLineText: 'package org.codenarc ', messageText: MESSAGE],
            [lineNumber: 3, sourceLineText: 'class MyClass {\t', messageText: MESSAGE],
            [lineNumber: 5, sourceLineText: '    def go() { /* ... */ }  ', messageText: MESSAGE],
            [lineNumber: 6, sourceLineText: '    ', messageText: MESSAGE],
            [lineNumber: 8, sourceLineText: '}\t', messageText: MESSAGE],
        )
    }

    @Test
    void testWindowsLineEndings() {
        final SOURCE = 'package org.codenarc \r\n' +
                '\r\n' +
                'class MyClass {\t\r\n' +
                '\r\n' +
                '    def go() {}  \r\n' +
                '    \r\n' +
                '    def stop() {}\r\n' +
                '}\t\r\n'
        assertViolations(SOURCE,
                [lineNumber: 1, sourceLineText: 'package org.codenarc ', messageText: MESSAGE],
                [lineNumber: 3, sourceLineText: 'class MyClass {\t', messageText: MESSAGE],
                [lineNumber: 5, sourceLineText: '    def go() {}  ', messageText: MESSAGE],
                [lineNumber: 6, sourceLineText: '    ', messageText: MESSAGE],
                [lineNumber: 8, sourceLineText: '}\t', messageText: MESSAGE],
        )
    }

    @Test
    @SuppressWarnings('TrailingWhitespace')
    void testWhitespaceOnlyLines() {
        final SOURCE = '''
            |package org.codenarc
            |    
            |class MyClass {
            |    
            |    def go() { /* ... */ }
            |    def goSomewhere() { /* ... */ }
            |}
        '''.trim().stripMargin()
        assertViolations(SOURCE,
            [lineNumber: 2, sourceLineText: '    ', messageText: MESSAGE],
            [lineNumber: 4, sourceLineText: '    ', messageText: MESSAGE],
        )
    }

    @Override
    protected TrailingWhitespaceRule createRule() {
        new TrailingWhitespaceRule()
    }

}
