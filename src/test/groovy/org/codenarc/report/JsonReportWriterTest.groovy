/*
 * Copyright 2020 the original author or authors.
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

 * @author Nicolas Vuillamy
 */
package org.codenarc.report

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule
import org.codenarc.ruleset.ListRuleSet
import org.junit.Before
import org.junit.Test

/**
 * Tests for JsonReportWriter
 */
class JsonReportWriterTest extends AbstractJsonReportWriterTestCase {

    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3)
    private static final NEW_REPORT_FILE = 'target/NewJsonReport.json'

    @SuppressWarnings('LineLength')
    private static final REPORT_JSON = """
        {"codeNarc":{"url":"https://www.codenarc.org","version":"${VERSION}"},"report":{"timestamp":"${FORMATTED_TIMESTAMP}"},"project":{"title":"My Cool Project","sourceDirectories":["c:/MyProject/src/main/groovy","c:/MyProject/src/test/groovy"]},"summary":{"totalFiles":6,"filesWithViolations":3,"priority1":2,"priority2":2,"priority3":3},"packages":[{"path":"src/main","totalFiles":3,"filesWithViolations":3,"priority1":2,"priority2":2,"priority3":3,"files":[{"name":"MyAction.groovy","violations":[{"ruleName":"RULE1","priority":1,"lineNumber":111,"sourceLine":"if (count < 23 && index <= 99 && name.contains('\\u0000')) {"},{"ruleName":"RULE3","priority":3,"lineNumber":333,"sourceLine":"throw new Exception(\\"cdata=<![CDATA[whatever]]>\\") // Some very long message 1234567890123456789012345678901234567890","message":"Other info"},{"ruleName":"RULE3","priority":3,"lineNumber":333,"sourceLine":"throw new Exception(\\"cdata=<![CDATA[whatever]]>\\") // Some very long message 1234567890123456789012345678901234567890","message":"Other info"},{"ruleName":"RULE1","priority":1,"lineNumber":111,"sourceLine":"if (count < 23 && index <= 99 && name.contains('\\u0000')) {"},{"ruleName":"RULE2","priority":2,"lineNumber":222,"message":"bad stuff: !@#\$%^&*()_+<>"}]}]},{"path":"src/main/dao","totalFiles":2,"filesWithViolations":2,"priority1":0,"priority2":1,"priority3":1,"files":[{"name":"MyDao.groovy","violations":[{"ruleName":"RULE3","priority":3,"lineNumber":333,"sourceLine":"throw new Exception(\\"cdata=<![CDATA[whatever]]>\\") // Some very long message 1234567890123456789012345678901234567890","message":"Other info"}]},{"name":"MyOtherDao.groovy","violations":[{"ruleName":"RULE2","priority":2,"lineNumber":222,"message":"bad stuff: !@#\$%^&*()_+<>"}]}]},{"path":"src/test","totalFiles":3,"filesWithViolations":0,"priority1":0,"priority2":0,"priority3":0,"files":[]}],"rules":[{"name":"DuplicateImport","description":"Custom: Duplicate imports"},{"name":"UnnecessaryBooleanInstantiation","description":"Use Boolean.valueOf() for variable values or Boolean.TRUE and Boolean.FALSE for constant values instead of calling the Boolean() constructor directly or calling Boolean.valueOf(true) or Boolean.valueOf(false)."}]}
    """

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def jsonAsString = stringWriter.toString()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assert output != null && output != ''
        assertJson(output, REPORT_JSON)
    }

    @Test
    void testWriteReport_Writer_ProperPackageSummaryForPackageWithEmptyRelativePath() {
        final JSON = '''
            {
                "summary": {
                    "totalFiles": 2,
                    "filesWithViolations": 1,
                    "priority1": 0,
                    "priority2": 0,
                    "priority3": 1
                }
            }
        '''
        def dirResults = new DirectoryResults('', 2)
        dirResults.addChild(new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3]))
        def rootResults = new DirectoryResults()
        rootResults.addChild(dirResults)
        reportWriter.writeReport(stringWriter, analysisContext, rootResults)
        assertContainsJson(stringWriter.toString(), JSON)
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcJsonReport.json')
        def jsonAsString = reportFile.text
        reportFile.delete()      // comment out to keep report file around for easy inspection
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def jsonAsString = reportFile.text
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    @Test
    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    @Test
    void testDefaultOutputFile_CodeNarcJsonReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcJsonReport.json'
    }

    //--------------------------------------------------------------------------
    // Setup and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUpJsonReportWriterTest() {
        reportWriter = new JsonReportWriter(title:TITLE)
        reportWriter.getTimestamp = { TIMESTAMP_DATE }

        def srcMainDirResults = new DirectoryResults('src/main', 1)
        srcMainDaoDirResults = new DirectoryResults('src/main/dao', 2)
        def srcTestDirResults = new DirectoryResults('src/test', 3)
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)
        results.addChild(srcTestDirResults)

        ruleSet = new ListRuleSet([     // NOT in alphabetical order
            new DuplicateImportRule(description:'Custom: Duplicate imports'),
            new UnnecessaryBooleanInstantiationRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }

}
