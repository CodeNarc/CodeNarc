/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc

import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.HtmlReportWriter
import org.codenarc.report.ReportWriter
import org.codenarc.results.FileResults
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import org.codenarc.rule.FakePathRule

/**
 * Tests for CodeNarcRunner
 *
 * @author Chris Mair
 */
class CodeNarcRunnerTest extends AbstractTestCase {
    private static final XML_RULESET1 = 'rulesets/RuleSet1.xml'
    private static final GROOVY_RULESET1 = 'rulesets/GroovyRuleSet1.txt'
    private static final RULESET_FILES = 'rulesets/RuleSet1.xml,rulesets/GroovyRuleSet2.txt'
    private static final RULESET_FILES_WITH_SPACES = 'rulesets/RuleSet1.xml , rulesets/GroovyRuleSet2.txt,  rulesets/RuleSet3.xml  '
    private static final REPORT_FILE = 'CodeNarcTest-Report.html'
    private static final RESULTS = new FileResults('path', [])
    private static final SOURCE_DIRS = ['abc']

    private codeNarcRunner

    @Test
    void testExecute_NoRuleSetFiles() {
        shouldFailWithMessageContaining('ruleSetFiles') { codeNarcRunner.execute() }
    }

    @Test
    void testExecute_NoSourceAnalyzer() {
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        codeNarcRunner.reportWriters << new HtmlReportWriter(outputFile:REPORT_FILE)
        shouldFailWithMessageContaining('sourceAnalyzer') { codeNarcRunner.execute() }
    }

    @Test
    void testExecute() {
        def ruleSet
        def sourceAnalyzer = [analyze: { rs -> ruleSet = rs; RESULTS }, getSourceDirectories: { SOURCE_DIRS }] as SourceAnalyzer
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer

        def analysisContext, results
        def reportWriter = [writeReport: { ac, res -> analysisContext = ac; results = res }] as ReportWriter
        codeNarcRunner.reportWriters << reportWriter

        codeNarcRunner.ruleSetFiles = XML_RULESET1

        assert codeNarcRunner.execute() == RESULTS

        assert ruleSet.rules*.class == [FakePathRule]

        assert analysisContext.ruleSet == ruleSet
        assert analysisContext.sourceDirectories == SOURCE_DIRS
        assert results == RESULTS
    }

    @Test
    void testExecute_NoReportWriters() {
        def sourceAnalyzer = [analyze: { RESULTS }, getSourceDirectories: { SOURCE_DIRS }] as SourceAnalyzer
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        assert codeNarcRunner.execute() == RESULTS
    }

    @Test
    void testCreateRuleSet_OneXmlRuleSet() {
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules*.name == ['TestPath']
    }

    @Test
    void testCreateRuleSet_OneGroovyRuleSet() {
        codeNarcRunner.ruleSetFiles = GROOVY_RULESET1
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock']
    }

    @Test
    void testCreateRuleSet_MultipleRuleSets() {
        codeNarcRunner.ruleSetFiles = RULESET_FILES
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules*.name == ['TestPath', 'CatchThrowable', 'ThrowExceptionFromFinallyBlock', 'StatelessClass']
    }

    @Test
    void testCreateRuleSet_MultipleRuleSets_WithSpaces() {
        codeNarcRunner.ruleSetFiles = RULESET_FILES_WITH_SPACES
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules*.name == ['TestPath', 'CatchThrowable', 'ThrowExceptionFromFinallyBlock', 'StatelessClass', 'Stub']
    }

    @Test
    void testCreateRuleSet_RuleSetFileDoesNotExist() {
        codeNarcRunner.ruleSetFiles = 'rulesets/NoSuchRuleSet.txt'
        shouldFail(FileNotFoundException) { codeNarcRunner.createRuleSet() }
    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUpCodeNarcRunnerTest() {
        codeNarcRunner = new CodeNarcRunner()
    }
}
