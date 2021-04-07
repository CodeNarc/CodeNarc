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
import org.junit.Test

/**
 * Tests for BracesForIfElseRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @author Chris Mair
  */
class BracesForIfElseRuleTest extends AbstractRuleTestCase<BracesForIfElseRule> {

    @Test
    void testRuleProperties() {
        def rule = new BracesForIfElseRule()
        assert rule.priority == 2
        assert rule.name == 'BracesForIfElse'
        assert rule.sameLine == true
        assert rule.validateElse == false
        assert rule.elseOnSameLineAsClosingBrace == null
        assert rule.elseOnSameLineAsOpeningBrace == null
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
    void testBraceOnNewLine_SameLineFalse_OtherBraces_NoViolations() {
        rule.sameLine = false
        final SOURCE = '''
            if (someContainer."${SomeClass.SOME_CONSTANT}" != null)  // And what about {}
            {
                doStuff()
            }
        '''
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
            def myMethod1() {
                if (a &&
                        b) {
                }
             }

            def myMethod2() {
                if (rule.checkList &&
                        !isIgnoredOneElementList(expression) &&
                        !otherCheck() &&
                        someOtherCheck(expression)
                ) {
                    println 123
                }
             }

            def myMethod3() {
                if (flag1 &&
                        flag2
                        )      {
                }
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
                [line: 40, source: 'if(1==1)',                 message: "Opening brace should be on the same line as 'if'"],
                [line: 43, source: 'else if (2==2)',           message: "'else' should be on the same line as the closing brace"],
                [line: 43, source: 'else if (2==2)',           message: "Opening brace should be on the same line as 'if'"],
                [line: 47, source: '{',                        message: "'else' should be on the same line as the closing brace"],
                [line: 47, source: '{',                        message: "Opening brace should be on the same line as 'else'"],
                [line: 50, source: 'if (3==3)',                message: "Opening brace should be on the same line as 'if'"],
                [line: 74, source: 'if (list.any { it > 1 })', message: "Opening brace should be on the same line as 'if'"],
                [line: 83, source: 'if (list.size > 5',        message: "Opening brace should be on the same line as 'if'"])
    }

    @Test
    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [line: 28, source: 'if(1==1) {',                 message: "Opening brace should not be on the same line as 'if'"],
                [line: 29, source: '} else if(2==2) {',          message: "'else' should not be on the same line as the closing brace"],
                [line: 29, source: '} else if(2==2) {',          message: "Opening brace should not be on the same line as 'if'"],
                [line: 30, source: '} else{',                    message: "'else' should not be on the same line as the closing brace"],
                [line: 30, source: '} else{',                    message: "Opening brace should not be on the same line as 'else'"],
                [line: 33, source: 'if (3==3){',                 message: "Opening brace should not be on the same line as 'if'"],
                [line: 52, source: 'if (list.any { it > 1 }) {', message: "Opening brace should not be on the same line as 'if'"],
                [line: 58, source: 'if (list.size > 5',          message: "Opening brace should not be on the same line as 'if'"])
    }

    @Test
    void testEnum_NoViolations() {
        final SOURCE = '''
            package com.mycompany.enums

            enum Season {
                SUMMER, WINTER
            }'''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BracesForIfElseRule createRule() {
        BracesForIfElseRule rule = new BracesForIfElseRule()
        rule.validateElse = true

        rule
    }
}
