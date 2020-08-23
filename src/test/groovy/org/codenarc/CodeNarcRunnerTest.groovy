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

import static org.codenarc.test.TestUtil.shouldFail
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.plugin.CodeNarcPlugin
import org.codenarc.plugin.TestPlugin1
import org.codenarc.plugin.TestPlugin2
import org.codenarc.plugin.disablerules.DisableRulesInCommentsPlugin
import org.codenarc.report.HtmlReportWriter
import org.codenarc.report.ReportWriter
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.FakePathRule
import org.codenarc.rule.Rule
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.ruleset.RuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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
    private static final Rule RULE = new StubRule(name:'Rule1', priority:1)

    private static final RESULTS = new FileResults('path', [])
    private static final SOURCE_DIRS = ['abc']
    private static final ENCODING = 'UTF-8'

    private CodeNarcRunner codeNarcRunner
    private RuleSet analyzedRuleSet
    private Results results = RESULTS
    private SourceAnalyzer sourceAnalyzer = [analyze: { rs -> analyzedRuleSet = rs; results }, getSourceDirectories: { SOURCE_DIRS }] as SourceAnalyzer

    @Test
    void test_InitialPropertyValues() {
        assert codeNarcRunner.reportWriters == []
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
        def plugin1 = [initialize:{ initialized << 'plugin1' }, processRules:{ }, processReports:{ }, processViolationsForFile:{ }] as CodeNarcPlugin
        def plugin2 = [initialize:{ initialized << 'plugin2' }, processRules:{ }, processReports:{ }, processViolationsForFile:{ }] as CodeNarcPlugin

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
                processRules:{ rules -> rules.find { rule -> rule.name == 'CatchThrowable' }.priority = 5 },
                processReports:{ },
                processViolationsForFile:{ }
            ] as CodeNarcPlugin
        def plugin2 = [             // ADD a new rule
                initialize:{ },
                processRules:{ rules -> rules.add(new StubRule(name:'NewRule')) },
                processReports:{ },
                processViolationsForFile:{ }
        ] as CodeNarcPlugin
        def plugin3 = [             // DELETE a rule
                initialize:{ },
                processRules:{ rules -> rules.removeAll { rule -> rule.name == 'ThrowExceptionFromFinallyBlock' } },
                processReports:{ },
                processViolationsForFile:{ }
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

    @Test
    void test_Plugin_execute_Calls_processViolationsForFile() {
        Violation violation1 = new Violation(lineNumber:1, rule:RULE)
        Violation violation2 = new Violation(lineNumber:2, rule:RULE)
        Violation violation3 = new Violation(lineNumber:3, rule:RULE)

        def plugin1 = [                         // MODIFY a violation
                initialize:{ },
                processRules:{ },
                processViolationsForFile:{ fv -> fv.violations.find { v -> v.lineNumber == 2 }?.message = 'CHANGED' },
                processReports:{ } ] as CodeNarcPlugin
        def plugin2 = [                         // ADD a new violation
                initialize:{ },
                processRules:{ },
                processViolationsForFile:{ fv ->
                    if (fv.path == 'path1') {
                        fv.violations.add(violation3)
                    }
                },
                processReports:{ } ] as CodeNarcPlugin
        def plugin3 = [                         // DELETE a violation
                initialize:{ },
                processRules:{ },
                processViolationsForFile:{ fv -> fv.violations.remove(violation1) },
                processReports:{ } ] as CodeNarcPlugin

        codeNarcRunner.registerPlugin(plugin1)
        codeNarcRunner.registerPlugin(plugin2)
        codeNarcRunner.registerPlugin(plugin3)

        def fileResults1 = new FileResults('path1', [violation1])
        def fileResults2 = new FileResults('path2', [violation2])
        results = new DirectoryResults('path')
        def childDirResults = new DirectoryResults('path/sub')
        results.addChild(fileResults1)
        results.addChild(childDirResults)
        childDirResults.addChild(fileResults2)
        codeNarcRunner.ruleSetFiles = GROOVY_RULESET1

        Results afterResults = codeNarcRunner.execute()

        log("afterResults=$afterResults")
        assert afterResults.violations.size() == 2
        assert afterResults.violations.find { v -> v.lineNumber == 2 }.message == 'CHANGED'     // MODIFIED
        assert afterResults.violations.find { v -> v.lineNumber == 3 }                          // ADDED
        assert !afterResults.violations.find { v -> v.lineNumber == 1 }                         // DELETED
    }

    @Test
    void test_Plugin_execute_Calls_processReports() {
        def written = []
        def report1 = [writeReport:{ context, results -> written << '1' }] as ReportWriter
        def report2 = [writeReport:{ context, results -> written << '2' }] as ReportWriter
        def report3 = [writeReport:{ context, results -> written << '3' }] as ReportWriter

        def plugin1 = [             // ADD a new report
                initialize:{ },
                processRules:{ },
                processViolationsForFile:{ },
                processReports:{ reportWriters -> reportWriters << report3 }
        ] as CodeNarcPlugin
        def plugin2 = [             // DELETE a report
                initialize:{ },
                processRules:{ },
                processViolationsForFile:{ },
                processReports:{ reportWriters -> reportWriters.remove(report2) }
        ] as CodeNarcPlugin

        codeNarcRunner.registerPlugin(plugin1)
        codeNarcRunner.registerPlugin(plugin2)

        codeNarcRunner.ruleSetFiles = GROOVY_RULESET1
        codeNarcRunner.reportWriters = [report1, report2]

        codeNarcRunner.execute()

        def reports = codeNarcRunner.reportWriters
        log(reports)
        assert codeNarcRunner.reportWriters == [report1, report3]
        assert written == ['1', '3']
    }

    @Test
    void test_Plugin_InitializesPluginsFromSystemProperty() {
        String pluginClassNames = [TestPlugin1, TestPlugin2].name.join(', ')
        System.setProperty(CodeNarcRunner.PLUGINS_PROPERTY, pluginClassNames)
        log("pluginClassNames=$pluginClassNames")

        codeNarcRunner.ruleSetFiles = XML_RULESET1

        codeNarcRunner.execute()

        assert TestPlugin1.initialized
        assert TestPlugin2.initialized
    }

    @Test
    void test_Plugin_InitializesStandardPlugins() {
        log("plugins (before)=${codeNarcRunner.plugins}")

        codeNarcRunner.ruleSetFiles = XML_RULESET1

        codeNarcRunner.execute()

        log("plugins (after)=${codeNarcRunner.plugins}")
        assert codeNarcRunner.plugins*.getClass() == [DisableRulesInCommentsPlugin]
    }

    @Test
    void test_Plugin_InvalidPluginsSystemProperty() {
        System.setProperty(CodeNarcRunner.PLUGINS_PROPERTY, 'xx, yy')
        codeNarcRunner.ruleSetFiles = XML_RULESET1
        shouldFailWithMessageContaining('xx') { codeNarcRunner.execute() }
    }

    @Test
    void test_registerPluginsForClassNames() {
        def pluginClasses = [TestPlugin1, TestPlugin2]
        String pluginClassNames = pluginClasses.name.join(', ')
        codeNarcRunner.registerPluginsForClassNames(pluginClassNames)
        assert codeNarcRunner.plugins*.getClass() == pluginClasses
    }

    @Test
    void test_registerPluginsForClassNames_NullOrEmpty() {
        codeNarcRunner.registerPluginsForClassNames(null)
        codeNarcRunner.registerPluginsForClassNames('')
        assert codeNarcRunner.plugins.empty
    }

    @Test
    void test_registerPluginsForClassNames_InvalidClassName() {
        shouldFailWithMessageContaining('xx') { codeNarcRunner.registerPluginsForClassNames('xx') }

        // Not a CodeNarcPlugin
        String nonPluginClass = 'java.lang.Integer'
        shouldFailWithMessageContaining(nonPluginClass) { codeNarcRunner.registerPluginsForClassNames(nonPluginClass) }
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

    @After
    void cleanUp() {
        System.clearProperty(CodeNarcRunner.PLUGINS_PROPERTY)
    }

    private static String encode(String string) {
        return URLEncoder.encode(string, ENCODING)
    }

}
