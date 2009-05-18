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

import org.codenarc.test.AbstractTest
import org.codenarc.report.HtmlReportWriter
import org.codenarc.results.FileResults
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.ReportWriter

/**
 * Tests for CodeNarcRunner
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CodeNarcRunnerTest extends AbstractTest {
    static final BASIC_RULESET = 'rulesets/basic.xml'
    static final RULESET1 = 'rulesets/RuleSet1.xml'
    static final RULESET_FILES = 'rulesets/RuleSet1.xml,rulesets/RuleSet2.xml'
    static final REPORT_FILE = 'CodeNarcTest-Report.html'
    static final RESULTS = new FileResults('path', [])

    private codeNarcRunner

    void testExecute_NoRuleSetFiles() {
        shouldFailWithMessageContaining('ruleSetFiles') { codeNarcRunner.execute() }
    }

    void testExecute_NoReportWriters() {
        codeNarcRunner.ruleSetFiles = RULESET1
        shouldFailWithMessageContaining('reportWriters') { codeNarcRunner.execute() }
    }

    void testExecute_NoSourceAnalyzer() {
        codeNarcRunner.ruleSetFiles = RULESET1
        codeNarcRunner.reportWriters << new HtmlReportWriter(outputFile:REPORT_FILE)
        shouldFailWithMessageContaining('sourceAnalyzer') { codeNarcRunner.execute() }
    }

    void testExecute() {
        def ruleSet
        def sourceAnalyzer = [analyze: { rs -> ruleSet = rs; return RESULTS }] as SourceAnalyzer
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer

        def analysisContext, results
        def reportWriter = [writeOutReport: { ac, res -> analysisContext = ac; results = res }] as ReportWriter
        codeNarcRunner.reportWriters << reportWriter

        codeNarcRunner.ruleSetFiles = RULESET1

        assert codeNarcRunner.execute() == RESULTS

        assert ruleSet.rules*.class == [org.codenarc.rule.TestPathRule]

        assert analysisContext.ruleSet == ruleSet
        assert results == RESULTS
    }

    void testCreateRuleSet_OneRuleSet() {
        codeNarcRunner.ruleSetFiles = RULESET1 
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules*.class == [org.codenarc.rule.TestPathRule]
    }

    void testCreateRuleSet_MultipleRuleSets() {
        codeNarcRunner.ruleSetFiles = RULESET_FILES
        def ruleSet = codeNarcRunner.createRuleSet()
        assert ruleSet.rules.size() >= 4
    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        codeNarcRunner = new CodeNarcRunner()
    }
}
