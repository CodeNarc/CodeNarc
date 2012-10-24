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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for BracesForIfElseRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class BracesForIfElseRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForIfElse'
    }

    @Test
    void testBraceOnSameLine_NoViolations() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testBraceOnNewLine_SameLineFalse_NoViolations() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfOrElseWithNoBraces_NoViolations() {
        final SOURCE = '''
            if (a && b)
                println 'ok'
            else
                println 'bad'
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineIfStatement_NoViolations() {
        final SOURCE = '''
            if (a &&
                    b) {
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineIfStatement_Violation() {
        final SOURCE = '''
            if (a &&
                    b)
            {
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (a &&', "Opening brace should be on the same line as 'if'")
    }

    @Test
    void testViolationSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 40, sourceLineText: 'if(1==1)',       messageText: "Opening brace should be on the same line as 'if'"],
                [lineNumber: 43, sourceLineText: 'else if (2==2)', messageText: "'else' should be on the same line as the closing brace"],
                [lineNumber: 43, sourceLineText: 'else if (2==2)', messageText: "Opening brace should be on the same line as 'if'"],
                [lineNumber: 47, sourceLineText: '{',              messageText: "'else' should be on the same line as the closing brace"],
                [lineNumber: 47, sourceLineText: '{',              messageText: "Opening brace should be on the same line as 'else'"],
                [lineNumber: 50, sourceLineText: 'if (3==3)',      messageText: "Opening brace should be on the same line as 'if'"])
    }

    @Test
    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 28, sourceLineText: 'if(1==1) {',        messageText: "Opening brace should not be on the same line as 'if'"],
                [lineNumber: 29, sourceLineText: '} else if(2==2) {', messageText: "'else' should not be on the same line as the closing brace"],
                [lineNumber: 29, sourceLineText: '} else if(2==2) {', messageText: "Opening brace should not be on the same line as 'if'"],
                [lineNumber: 30, sourceLineText: '} else{',           messageText: "'else' should not be on the same line as the closing brace"],
                [lineNumber: 30, sourceLineText: '} else{',           messageText: "Opening brace should not be on the same line as 'else'"],
                [lineNumber: 33, sourceLineText: 'if (3==3){',        messageText: "Opening brace should not be on the same line as 'if'"])
    }

    protected Rule createRule() {
        BracesForIfElseRule rule = new BracesForIfElseRule()
        rule.validateElse = true

        rule
    }
}
