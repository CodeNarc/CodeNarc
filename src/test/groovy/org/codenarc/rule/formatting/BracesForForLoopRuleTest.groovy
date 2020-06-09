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
 * Tests for BracesForForLoopRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class BracesForForLoopRuleTest extends AbstractRuleTestCase<BracesForForLoopRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForForLoop'
        assert rule.sameLine == true
    }

    @Test
    void testMultilineForLoop() {
        final SOURCE = '''
            for (int x = 0;
                    x < 10;
                    x++) {

            } '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultilineForLoopOverride() {
        final SOURCE = '''
            for (int x = 0;
                    x < 10;
                    x++)
            {

            } '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testForLoopWithNoBraces_NoViolations() {
        final SOURCE = '''
            for (int x = 0; x < 10; x++)
                println x
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNewLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertSingleViolation(SOURCE, 33, 'for (i in 0..3)', 'Braces should start on the same line')
    }

    @Test
    void testNewLineOverride() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testSameLine() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    @Test
    void testSameLineOverride() {
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        rule.sameLine = false
        assertSingleViolation(SOURCE, 23, 'for (i in 0..3){', 'Braces should start on a new line')
    }

    @Test
    void testSameLineFalse_OtherBraces_NoViolations() {
        final SOURCE = '''
            for(String name=${SomeClass.SOME_CONSTANT}; name==null;)  // And what about {}
            {
                doStuff()
            }
        '''
        rule.sameLine = false
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassicForLoop_NoViolations() {
        final SOURCE = '''
            for (Iterator iter = searchResults.iterator(); iter.hasNext(); iter.next()) {
                println iter
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClassicForLoop_NoIncrementExpression_NoViolations() {
        final SOURCE = '''
            for (Iterator iter = searchResults.iterator(); iter.hasNext();) {
                println iter.next()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected BracesForForLoopRule createRule() {
        new BracesForForLoopRule()
    }
}
