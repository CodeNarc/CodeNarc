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

/**
 * Tests for BracesForIfElseRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class BracesForIfElseRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForIfElse'
    }

    void testSuccessScenarioSameLine() {

        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    void testSuccessScenarioNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertNoViolations(SOURCE)
    }

    void testMultilineIfStatementSuccess() {
        final SOURCE = '''
            if (a &&
                    b) {
            }
        '''

        assertNoViolations(SOURCE)
    }

    void testMultilineIfStatementFail() {
        final SOURCE = '''
            if (a &&
                    b)
            {
            }
        '''

        assertSingleViolation(SOURCE, 2, 'if (a &&', 'Braces should start on the same line')
    }

    void testViolationSameLine() {

        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 40, sourceLineText: 'if(1==1)', messageText: 'Braces should start on the same line'],
                [lineNumber: 43, sourceLineText: 'else if (2==2)', messageText: 'Braces should start on the same line'],
                [lineNumber: 50, sourceLineText: 'if (3==3)', messageText: 'Braces should start on the same line'])
    }

    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 28, sourceLineText: 'if(1==1) {', messageText: 'Braces should start on a new line'],
                [lineNumber: 29, sourceLineText: '} else if(2==2)', messageText: 'Braces should start on a new line'],
                [lineNumber: 33, sourceLineText: 'if (3==3){', messageText: 'Braces should start on a new line'])
    }

    protected Rule createRule() {
        new BracesForIfElseRule()
    }
}