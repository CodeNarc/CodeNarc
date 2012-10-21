/*
 * Copyright 2010 the original author or authors.
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
import org.codenarc.test.AbstractTestCase

import java.text.DateFormat

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for TestReportWriter
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class TextReportWriterTest extends AbstractTestCase {

    private static final LINE1 = 11
    private static final LINE2 = 2
    private static final LINE3 = 333
    private static final SOURCE_LINE1 = 'if (count < 23 && index <= 99) {'
    private static final SOURCE_LINE3 = 'throw new Exception() // Something bad happened'
    private static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    private static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'Rule1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'AnotherRule', priority:2), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'BadStuff', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3 )
    private static final NEW_REPORT_FILE = 'NewTextReport.txt'
    private static final TITLE = 'My Cool Project'
    private static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    private static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    private static final VERSION = new File(VERSION_FILE).text
    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final REPORT_TEXT = """
CodeNarc Report: My Cool Project - ${FORMATTED_TIMESTAMP}

Summary: TotalFiles=6 FilesWithViolations=3 P1=3 P2=2 P3=3

File: src/main/MyAction.groovy
    Violation: Rule=Rule1 P=1 Line=11 Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=Rule1 P=1 Line=11 Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=AnotherRule P=2 Line=2 Msg=[bad stuff: !@#\$%^&*()_+<>]
    Violation: Rule=BadStuff P=3 Line=333 Msg=[Other info] Src=[throw new Exception() // Something bad happened]
    Violation: Rule=BadStuff P=3 Line=333 Msg=[Other info] Src=[throw new Exception() // Something bad happened]

File: src/main/dao/MyDao.groovy
    Violation: Rule=BadStuff P=3 Line=333 Msg=[Other info] Src=[throw new Exception() // Something bad happened]

File: src/main/dao/MyOtherDao.groovy
    Violation: Rule=Rule1 P=1 Line=11 Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=AnotherRule P=2 Line=2 Msg=[bad stuff: !@#\$%^&*()_+<>]

[CodeNarc (http://www.codenarc.org) v${VERSION}]
""".trim()

    private reportWriter
    private analysisContext
    private results, srcMainDaoDirResults
    private stringWriter

    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def reportText = stringWriter.toString()
        assertReportText(reportText)
    }

    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcReport.txt')
        def reportText = reportFile.text
        // reportFile.delete()      // keep report file around for easy inspection
        assertReportText(reportText)
    }

    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def reportText = reportFile.text
        reportFile.delete()
        assertReportText(reportText)
    }

    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assertReportText(output)
    }

    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    void testDefaultOutputFile_CodeNarcReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcReport.txt'
    }

    void setUp() {
        super.setUp()
        reportWriter = new TextReportWriter(title:TITLE)
        reportWriter.getTimestamp = { TIMESTAMP_DATE }

        def srcMainDirResults = new DirectoryResults('src/main', 1)
        srcMainDaoDirResults = new DirectoryResults('src/main/dao', 2)
        def srcTestDirResults = new DirectoryResults('src/test', 3)
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2, VIOLATION1])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)
        results.addChild(srcTestDirResults)

        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2])
        stringWriter = new StringWriter()
    }

    @SuppressWarnings('JUnitStyleAssertions')
    private void assertReportText(String actualText) {
        def actualLines = actualText.readLines()
        def expectedLines = REPORT_TEXT.readLines()
        actualLines.eachWithIndex { line, index ->
            def lineNumber = "$index".padLeft(2)
            println "$lineNumber: $line"
            assertEquals("line=$line", expectedLines[index], line)
        }
        assertEquals(expectedLines.size(), actualLines.size())
    }

}
