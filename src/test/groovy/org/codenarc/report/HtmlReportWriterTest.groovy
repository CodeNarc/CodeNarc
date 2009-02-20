/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.report

import org.codenarc.AnalysisContext
import org.codenarc.report.HtmlReportWriter
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.rule.basic.BooleanInstantiationRule
import org.codenarc.rule.basic.ReturnFromFinallyBlockRule
import org.codenarc.rule.basic.StringInstantiationRule
import org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTest

/**
 * Tests for HtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class HtmlReportWriterTest extends AbstractTest {

    static VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:20, sourceLine:'if (file) {')
    static VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:33, message:'bad stuff')
    static VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:95, sourceLine:'throw new Exception()', message: 'Other info')
    static OUTPUT_DIR = "."
    static REPORT_FILENAME = "HtmlReport.html"
    static REPORT_CONTENTS = ['html', '/src/main', 'MyAction.groovy', 'MyAction2.groovy', 'MyActionTest.groovy']
    static NEW_REPORT_FILE = 'NewReport.html'
    static TITLE = 'My Cool Project'

    def reportWriter
    def analysisContext
    def results
    def ruleSet

    void testWriteOutReport() {
        reportWriter.writeOutReport(analysisContext, results)

        def reportText = new File(HtmlReportWriter.DEFAULT_OUTPUT_FILE).text
        assertContainsAllInOrder(reportText, REPORT_CONTENTS)
        assertContainsRuleIds(reportText)
    }

    void testWriteOutReport_NoDescriptionsForRuleIds() {
        ruleSet = new ListRuleSet([new StubRule(name:'MyRuleXX'), new StubRule(name:'MyRuleYY')])
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        analysisContext.ruleSet = ruleSet
        reportWriter.writeOutReport(analysisContext, results)

        def reportText = new File(HtmlReportWriter.DEFAULT_OUTPUT_FILE).text
        assertContainsAllInOrder(reportText, REPORT_CONTENTS)
        assertContainsAllInOrder(reportText, ['MyRuleXX', 'No description', 'MyRuleYY', 'No description'])
    }

    void testWriteOutReport_RuleDescriptionsProvidedInCodeNarcMessagesFile() {
        def biRule = new BooleanInstantiationRule()
        ruleSet = new ListRuleSet([new StubRule(name:'MyRuleXX'), new StubRule(name:'MyRuleYY'), biRule])
        analysisContext.ruleSet = ruleSet
        reportWriter.writeOutReport(analysisContext, results)

        def reportText = new File(HtmlReportWriter.DEFAULT_OUTPUT_FILE).text
        assertContainsAllInOrder(reportText, REPORT_CONTENTS)
        assertContainsAllInOrder(reportText, [biRule.name, 'MyRuleXX', 'My Rule XX', 'MyRuleYY', 'My Rule YY'])
    }

    void testWriteOutReport_SetOutputFileAndTitle() {
        static OUTPUT_FILE = NEW_REPORT_FILE
        reportWriter.outputFile = OUTPUT_FILE
        reportWriter.title = TITLE
        reportWriter.writeOutReport(analysisContext, results)

        def reportText = new File(OUTPUT_FILE).text
        assertContainsAllInOrder(reportText, REPORT_CONTENTS)
        assertContainsAllInOrder(reportText, ['Narc Report:', TITLE])
        assertContainsRuleIds(reportText)
    }

    void testWriteOutReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeOutReport(analysisContext, null) }
    }

    void testWriteOutReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeOutReport(null, results) }
    }

    void testIsDirectoryContainingFilesWithViolations() {
        def results = new FileResults('', [])
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        results = new FileResults('', [VIOLATION1])
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        results = new DirectoryResults('')
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        results.addChild(new FileResults('', []))
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results), 'child with no violations'

        def child = new DirectoryResults('')
        child.addChild(new FileResults('', [VIOLATION1]))
        results.addChild(child)
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results), 'grandchild with violations'

        results.addChild(new FileResults('', [VIOLATION1]))
        assert reportWriter.isDirectoryContainingFilesWithViolations(results)
    }

    void testIsDirectoryContainingFiles() {
        def results = new FileResults('', [])
        assert !reportWriter.isDirectoryContainingFiles(results)

        results = new DirectoryResults('')
        assert !reportWriter.isDirectoryContainingFiles(results)

        results.numberOfFilesInThisDirectory = 2
        assert reportWriter.isDirectoryContainingFiles(results)
    }

    void setUp() {
        super.setUp()
        reportWriter = new HtmlReportWriter()

        def dirResultsMain = new DirectoryResults(path:'src/main', numberOfFilesInThisDirectory:1)
        def dirResultsCode = new DirectoryResults(path:'src/main/code', numberOfFilesInThisDirectory:2)
        def dirResultsTest = new DirectoryResults(path:'src/main/test', numberOfFilesInThisDirectory:3)
        def dirResultsTest_SubdirNoViolations = new DirectoryResults(path:'src/main/test/noviolations', numberOfFilesInThisDirectory:4)
        def dirResultsTest_SubdirEmpty = new DirectoryResults(path:'src/main/test/empty')
        def fileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResults2 = new FileResults('src/main/MyAction2.groovy', [VIOLATION3])
        def fileResults3 = new FileResults('src/main/MyActionTest.groovy', [VIOLATION1, VIOLATION2])
        dirResultsMain.addChild(fileResults1)
        dirResultsMain.addChild(dirResultsCode)
        dirResultsMain.addChild(dirResultsTest)
        dirResultsCode.addChild(fileResults2)
        dirResultsTest.addChild(fileResults3)
        dirResultsTest.addChild(dirResultsTest_SubdirNoViolations)
        dirResultsTest.addChild(dirResultsTest_SubdirEmpty)
        results = new DirectoryResults()
        results.addChild(dirResultsMain)

        ruleSet = new ListRuleSet([
                new BooleanInstantiationRule(),
                new ReturnFromFinallyBlockRule(),
                new StringInstantiationRule(),
                new ThrowExceptionFromFinallyBlockRule(),
                new DuplicateImportRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:['/src/main'], ruleSet:ruleSet)
    }

    void tearDown() {
        super.tearDown()
//        new File(HtmlReportWriter.DEFAULT_OUTPUT_FILE).delete()
        new File(NEW_REPORT_FILE).delete()
    }

    private void assertContainsRuleIds(String reportText) {
        def ruleIds = ruleSet.rules.collect { it.name }
        assertContainsAllInOrder(reportText, ruleIds.sort())
        assert !reportText.contains('No description')
    }

}