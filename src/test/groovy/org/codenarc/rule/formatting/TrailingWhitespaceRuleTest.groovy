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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for TrailingWhitespaceRule
 * 
 * @author Joe Sondow
 */
class TrailingWhitespaceRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'TrailingWhitespace'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''\
            package org.codenarc

            class MyClass {

                def go() { /* ... */ }
                def goSomewhere() { /* ... */ }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testLineEndingWithMultipleSpaces() {
        final SOURCE = 'class MyClass {}    \n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {}    ', 'Line ends with whitespace characters')
    }

    @Test
    void testLineEndingWithOneSpace() {
        final SOURCE = 'class MyClass {} \n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {} ', 'Line ends with whitespace characters')
    }

    @Test
    void testLineEndingWithATab() {
        final SOURCE = 'class MyClass {}\t\n'
        assertSingleViolation(SOURCE, 1, 'class MyClass {}\t', 'Line ends with whitespace characters')
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
        def msg = 'Line ends with whitespace characters'
        assertViolations(SOURCE, [
            [lineNumber: 1, sourceLineText: 'package org.codenarc ', messageText: msg],
            [lineNumber: 3, sourceLineText: 'class MyClass {\t', messageText: msg],
            [lineNumber: 5, sourceLineText: '    def go() { /* ... */ }  ', messageText: msg],
            [lineNumber: 6, sourceLineText: '    ', messageText: msg],
            [lineNumber: 8, sourceLineText: '}\t', messageText: msg],
        ] as Map[])
    }

    protected Rule createRule() {
        new TrailingWhitespaceRule()
    }
}
