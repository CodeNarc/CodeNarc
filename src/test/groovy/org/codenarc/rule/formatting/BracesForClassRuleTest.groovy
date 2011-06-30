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
 * Tests for BracesForClassRule
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class BracesForClassRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BracesForClass'
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

    void testViolationSameLine() {

        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestNewLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 5, sourceLineText: 'class First', messageText: 'Braces should start on the same line'],
                [lineNumber: 17, sourceLineText: 'class Second', messageText: 'Braces should start on the same line'],
                [lineNumber: 63, sourceLineText: 'interface Third', messageText: 'Braces should start on the same line'])
    }

    void testViolationNewLine() {
        rule.sameLine = false
        def testFile = this.getClass().getClassLoader().getResource('rule/BracesTestSameLine.txt')
        final SOURCE = new File(testFile.toURI()).text
        assertViolations(SOURCE,
                [lineNumber: 6, sourceLineText: 'class First{', messageText: 'Braces should start on a new line'],
                [lineNumber: 14, sourceLineText: 'class Second{', messageText: 'Braces should start on a new line'],
                [lineNumber: 44, sourceLineText: 'interface Third{', messageText: 'Braces should start on a new line'])
    }

    protected Rule createRule() {
        new BracesForClassRule()
    }
}