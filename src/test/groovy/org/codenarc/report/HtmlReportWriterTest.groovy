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
package org.codenarc.report

import org.codenarc.AnalysisContext
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
import org.codenarc.test.AbstractTestCase
import org.codenarc.rule.Rule

/**
 * Tests for HtmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class HtmlReportWriterTest extends AbstractTestCase {
    private static final LONG_LINE = 'throw new Exception() // Some very long message 1234567890123456789012345678901234567890'
    private static final TRUNCATED_LONG_LINE = 'throw new Exception() // Some very long message 12345678..901234567890'
    private static final MESSAGE = 'bad stuff'
    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final LINE3 = 333
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:'if (file) {')
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:LONG_LINE, message: 'Other info')
    private static final HTML_TAG = 'html'
    private static final BOTTOM_LINK = "<a href='http://www.codenarc.org'>CodeNarc"
    private static final NEW_REPORT_FILE = 'NewReport.html'
    private static final TITLE = 'My Cool Project'
    private static final BASIC_CONTENTS = [
            HTML_TAG,
            'Report timestamp',
            'Summary by Package', 'Package', 'Total Files', 'Files with Violations', 'Priority 1', 'Priority 2', 'Priority 3',
            'MyAction.groovy', MESSAGE, TRUNCATED_LONG_LINE,
            'MyAction2.groovy',
            'MyActionTest.groovy',
            BOTTOM_LINK]

    private reportWriter
    private analysisContext
    private results
    private ruleSet

    void testWriteOutReport() {
        final CONTENTS = [
                HTML_TAG,
                'MyAction.groovy', 'RULE1', LINE1, 'RULE1', LINE1, 'RULE2', LINE2, MESSAGE, 'RULE3', LINE3, TRUNCATED_LONG_LINE,
                'MyAction2.groovy', 'RULE3', LINE3, TRUNCATED_LONG_LINE,
                'MyActionTest.groovy', 'RULE1', LINE1, 'RULE2', LINE2, MESSAGE,
                BOTTOM_LINK]
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText()
        assertContainsAllInOrder(reportText, CONTENTS)
        assertContainsRuleIds(reportText)
    }

    void testWriteOutReport_Priority4() {
        VIOLATION1.rule.name = 'RULE4'
        VIOLATION1.rule.priority = 4
        final CONTENTS = [HTML_TAG, 'MyActionTest.groovy', 'RULE2', 2, LINE2, MESSAGE, 'RULE4', 4, LINE1, BOTTOM_LINK]
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText()
        assertContainsAllInOrder(reportText, CONTENTS)
        assertContainsRuleIds(reportText)
    }

    void testWriteOutReport_NoDescriptionsForRuleIds() {
        ruleSet = new ListRuleSet([new StubRule(name:'MyRuleXX'), new StubRule(name:'MyRuleYY')])
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        analysisContext.ruleSet = ruleSet
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText()
        assertContainsAllInOrder(reportText, BASIC_CONTENTS)
        assertContainsAllInOrder(reportText, ['MyRuleXX', 'No description', 'MyRuleYY', 'No description'])
    }

    void testWriteOutReport_RuleDescriptionsProvidedInCodeNarcMessagesFile() {
        def biRule = new BooleanInstantiationRule()
        ruleSet = new ListRuleSet([new StubRule(name:'MyRuleXX'), new StubRule(name:'MyRuleYY'), biRule])
        analysisContext.ruleSet = ruleSet
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText()
        assertContainsAllInOrder(reportText, BASIC_CONTENTS)
        assertContainsAllInOrder(reportText, [biRule.name, 'MyRuleXX', 'My Rule XX', 'MyRuleYY', 'My Rule YY'])
    }

    void testWriteOutReport_RuleDescriptionsSetDirectlyOnTheRule() {
        ruleSet = new ListRuleSet([
                new StubRule(name:'MyRuleXX', description:'description77'),
                new StubRule(name:'MyRuleYY', description:'description88')])
        analysisContext.ruleSet = ruleSet
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText()
        assertContainsAllInOrder(reportText, BASIC_CONTENTS)
        assertContainsAllInOrder(reportText, ['MyRuleXX', 'description77', 'MyRuleYY', 'description88'])
    }

    void testWriteOutReport_IncludesRuleThatDoesNotSupportGetDescription() {
        analysisContext.ruleSet = new ListRuleSet([ [getName:{'RuleABC'}] as Rule])
        reportWriter.writeOutReport(analysisContext, results)
        assertContainsAllInOrder(getReportText(), ['RuleABC', 'No description'])
    }

    void testWriteOutReport_SetOutputFileAndTitle() {
        final OUTPUT_FILE = NEW_REPORT_FILE
        reportWriter.outputFile = OUTPUT_FILE
        reportWriter.title = TITLE
        reportWriter.writeOutReport(analysisContext, results)
        def reportText = getReportText(OUTPUT_FILE)
        assertContainsAllInOrder(reportText, BASIC_CONTENTS)
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

    void testFormatSourceLine() {
        assert reportWriter.formatSourceLine('') == null
        assert reportWriter.formatSourceLine('abc') == 'abc'
        assert reportWriter.formatSourceLine('abcdef'*20) == 'abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefab..abcdefabcdef'
        assert reportWriter.formatSourceLine('abc', 2) == 'abc'
        assert reportWriter.formatSourceLine('abcdef'*20, 2) == 'cdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd..abcdefabcdef'
    }

    void setUp() {
        super.setUp()
        reportWriter = new HtmlReportWriter()

        def dirResultsMain = new DirectoryResults(path:'src/main', numberOfFilesInThisDirectory:1)
        def dirResultsCode = new DirectoryResults(path:'src/main/code', numberOfFilesInThisDirectory:2)
        def dirResultsTest = new DirectoryResults(path:'src/main/test', numberOfFilesInThisDirectory:3)
        def dirResultsTestSubdirNoViolations = new DirectoryResults(path:'src/main/test/noviolations', numberOfFilesInThisDirectory:4)
        def dirResultsTestSubdirEmpty = new DirectoryResults(path:'src/main/test/empty')
        def fileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResults2 = new FileResults('src/main/MyAction2.groovy', [VIOLATION3])
        def fileResults3 = new FileResults('src/main/MyActionTest.groovy', [VIOLATION1, VIOLATION2])
        dirResultsMain.addChild(fileResults1)
        dirResultsMain.addChild(dirResultsCode)
        dirResultsMain.addChild(dirResultsTest)
        dirResultsCode.addChild(fileResults2)
        dirResultsTest.addChild(fileResults3)
        dirResultsTest.addChild(dirResultsTestSubdirNoViolations)
        dirResultsTest.addChild(dirResultsTestSubdirEmpty)
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

    private String getReportText(String filename=HtmlReportWriter.DEFAULT_OUTPUT_FILE) {
        return new File(filename).text
    }

}