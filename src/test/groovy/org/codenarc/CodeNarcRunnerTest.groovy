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
import org.codenarc.plugin.CodeNarcPlugin
import org.codenarc.report.HtmlReportWriter
import org.codenarc.report.ReportWriter
import org.codenarc.results.FileResults
import org.codenarc.rule.Rule
import org.codenarc.rule.StubRule
import org.codenarc.ruleset.RuleSet
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
    private static final RULESET_AS_URL = 'file:src/test/resources/rulesets/RuleSet1.xml'
    private static final RULESET_URL_WITH_WEIRD_CHARS_ENCODED = 'file:' + encode('src/test/resources/rulesets/WeirdCharsRuleSet-,#.txt')
    private static final REPORT_FILE = 'CodeNarcTest-Report.html'
    private static final RESULTS = new FileResults('path', [])
    private static final SOURCE_DIRS = ['abc']
    private static final ENCODING = 'UTF-8'

    private CodeNarcRunner codeNarcRunner
    private RuleSet analyzedRuleSet
    private SourceAnalyzer sourceAnalyzer = [analyze: { rs -> analyzedRuleSet = rs; RESULTS }, getSourceDirectories: { SOURCE_DIRS }] as SourceAnalyzer

    @Test
    void test_InitialPropertyValues() {
        assert codeNarcRunner.reportWriters == []
        assert codeNarcRunner.resultsProcessor instanceof NullResultsProcessor
    }

    // Tests for execute()

    @Test
    void test_execute_NoRuleSetFiles() {
        shouldFailWithMessageContaining('ruleSetFiles') { codeNarcRunner.execute() }
    }

    @Test
    void test_execute_NoSourceAnalyzer() {
        codeNarcRunner.sourceAnalyzer = null
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        codeNarcRunner.reportWriters << new HtmlReportWriter(outputFile:REPORT_FILE)
        shouldFailWithMessageContaining('sourceAnalyzer') { codeNarcRunner.execute() }
    }

    @Test
    void testExecute() {
        def resultsProcessorCalled
        def resultsProcessor = [processResults:{ results ->
            assert results == RESULTS
            resultsProcessorCalled = true
        }] as ResultsProcessor
        codeNarcRunner.resultsProcessor = resultsProcessor

        def analysisContext, results
        def reportWriter = [writeReport: { ac, res ->
            analysisContext = ac
            results = res
        }] as ReportWriter
        codeNarcRunner.reportWriters << reportWriter

        codeNarcRunner.ruleSetFiles = XML_RULESET1

        assert codeNarcRunner.execute() == RESULTS

        assert analyzedRuleSet.rules*.class == [FakePathRule]

        assert analysisContext.ruleSet == analyzedRuleSet
        assert analysisContext.sourceDirectories == SOURCE_DIRS
        assert results == RESULTS
        assert resultsProcessorCalled
    }

    @Test
    void test_execute_NoReportWriters() {
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        assert codeNarcRunner.execute() == RESULTS
    }

    // Tests for Plugins

    @Test
    void test_registerPlugin() {
        def plugin1 = [a:1] as CodeNarcPlugin
        def plugin2 = [b:1] as CodeNarcPlugin

        codeNarcRunner.registerPlugin(plugin1)
        assert codeNarcRunner.getPlugins() == [plugin1]
        codeNarcRunner.registerPlugin(plugin2)
        assert codeNarcRunner.getPlugins() == [plugin1, plugin2]

        shouldFailWithMessageContaining('plugin') { codeNarcRunner.registerPlugin(null) }
    }

    @Test
    void test_Plugin_execute_Calls_initialize() {
        Set initialized = []
        def plugin1 = [initialize:{ initialized << 'plugin1' }, processRules:{ }] as CodeNarcPlugin
        def plugin2 = [initialize:{ initialized << 'plugin2' }, processRules:{ }] as CodeNarcPlugin

        codeNarcRunner.registerPlugin(plugin1)
        codeNarcRunner.registerPlugin(plugin2)

        codeNarcRunner.ruleSetFiles = XML_RULESET1

        codeNarcRunner.execute()

        assert initialized == ['plugin1', 'plugin2'] as Set
    }

    @Test
    void test_Plugin_execute_Calls_processRules() {
        def plugin1 = [             // MODIFY a rule
                initialize:{ },
                processRules:{ rules -> rules.find { rule -> rule.name == 'CatchThrowable' }.priority = 5 }
            ] as CodeNarcPlugin
        def plugin2 = [             // ADD a new rule
                initialize:{ },
                processRules:{ rules -> rules.add(new StubRule(name:'NewRule')) }
        ] as CodeNarcPlugin
        def plugin3 = [             // DELETE a rule
                initialize:{ },
                processRules:{ rules -> rules.removeAll { rule -> rule.name == 'ThrowExceptionFromFinallyBlock' } }
        ] as CodeNarcPlugin

        codeNarcRunner.registerPlugin(plugin1)
        codeNarcRunner.registerPlugin(plugin2)
        codeNarcRunner.registerPlugin(plugin3)

        codeNarcRunner.ruleSetFiles = GROOVY_RULESET1   // CatchThrowable, ThrowExceptionFromFinallyBlockRule

        codeNarcRunner.execute()

        log(analyzedRuleSet.rules)
        List<Rule> rules = analyzedRuleSet.getRules()
        assert rules.size() == 2
        assert rules.find { rule -> rule.name == 'CatchThrowable' }.priority == 5       // MODIFIED
        assert rules.find { rule -> rule.name == 'NewRule' }                            // ADDED
        assert !rules.find { rule -> rule.name == 'ThrowExceptionFromFinallyBlock' }    // DELETED
    }

    // Tests for createRuleSet()

    @Test
    void test_createRuleSet_OneXmlRuleSet() {
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['TestPath']
    }

    @Test
    void test_createRuleSet_OneGroovyRuleSet() {
        codeNarcRunner.ruleSetFiles = GROOVY_RULESET1
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['CatchThrowable', 'ThrowExceptionFromFinallyBlock']
    }

    @Test
    void test_createRuleSet_MultipleRuleSets() {
        codeNarcRunner.ruleSetFiles = RULESET_FILES
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['TestPath', 'CatchThrowable', 'ThrowExceptionFromFinallyBlock', 'StatelessClass']
    }

    @Test
    void test_createRuleSet_MultipleRuleSets_WithSpaces() {
        codeNarcRunner.ruleSetFiles = RULESET_FILES_WITH_SPACES
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['TestPath', 'CatchThrowable', 'ThrowExceptionFromFinallyBlock', 'StatelessClass', 'Stub']
    }

    @Test
    void test_createRuleSet_RuleSetAsUrl() {
        codeNarcRunner.ruleSetFiles = RULESET_AS_URL
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['TestPath']
    }

    @Test
    void test_createRuleSet_WeirdCharsRuleSetUrl_Encoded() {
        codeNarcRunner.ruleSetFiles = RULESET_URL_WITH_WEIRD_CHARS_ENCODED
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['EmptyClass']
    }

    @Test
    void test_createRuleSet_WeirdCharsRuleSetUrl_Encoded_MultipleRuleSets() {
        codeNarcRunner.ruleSetFiles = RULESET_URL_WITH_WEIRD_CHARS_ENCODED + ', ' + XML_RULESET1
        def ruleSet = codeNarcRunner.createInitialRuleSet()
        assert ruleSet.rules*.name == ['EmptyClass', 'TestPath']
    }

    @Test
    void test_createRuleSet_RuleSetFileDoesNotExist() {
        codeNarcRunner.ruleSetFiles = 'rulesets/NoSuchRuleSet.txt'
        shouldFail(FileNotFoundException) { codeNarcRunner.createInitialRuleSet() }
    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUp() {
        codeNarcRunner = new CodeNarcRunner()
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer
    }

    private static String encode(String string) {
        return URLEncoder.encode(string, ENCODING)
    }

}
