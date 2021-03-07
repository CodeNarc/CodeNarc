/*
 * Copyright 2014 the original author or authors.
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
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import static org.junit.Assert.assertEquals

/**
 * Abstract superclass for TestReportWriter and subclass tests
 *
 * @author Luis Zimmermann
 */
abstract class AbstractCompactTextReportWriterTestCase extends AbstractTestCase {

    protected static final int LINE1 = 11
    protected static final int LINE2 = 2
    protected static final int LINE3 = 333
    protected static final String SOURCE_LINE1 = 'if (count < 23 && index <= 99) {'
    protected static final String SOURCE_LINE3 = 'throw new Exception() // Something bad happened'
    protected static final String MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    protected static final String MESSAGE3 = 'Other info'
    protected static final Violation VIOLATION1 = new Violation(rule:new StubRule(name:'Rule1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    protected static final Violation VIOLATION2 = new Violation(rule:new StubRule(name:'AnotherRule', priority:2), lineNumber:LINE2, message:MESSAGE2)
    protected static final Violation VIOLATION3 = new Violation(rule:new StubRule(name:'BadStuff', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3)
    protected static final String SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    protected static final String SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    protected static final String VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    protected static final String VERSION = new File(VERSION_FILE).text
    protected static final String CODENARC_URL = 'https://www.codenarc.org'

    protected reportWriter
    protected analysisContext
    protected results, srcMainDaoDirResults
    protected stringWriter

    //------------------------------------------------------------------------------------
    // Abstract declarations
    //------------------------------------------------------------------------------------

    protected abstract CompactTextReportWriter createReportWriter()
    protected abstract String getReportText()

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    @Test
    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assertReportText(output, getReportText())
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
    void testMaxPriority_DefaultsTo3() {
        assert reportWriter.maxPriority == 3
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @Before
    void setUpAbstractCompactTextReportWriterTestCase() {
        reportWriter = createReportWriter()
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
    protected void assertReportText(String actualText, String expectedText) {
        def actualLines = actualText.readLines()
        def expectedLines = expectedText.readLines()
        actualLines.eachWithIndex { line, index ->
            def lineNumber = "$index".padLeft(2)
            println "$lineNumber: $line"
            assertEquals("line=$line", expectedLines[index], line)
        }
        assertEquals(expectedLines.size(), actualLines.size())
    }

    @SuppressWarnings('ConfusingMethodName')
    protected static String version() {
        return VERSION
    }
}
