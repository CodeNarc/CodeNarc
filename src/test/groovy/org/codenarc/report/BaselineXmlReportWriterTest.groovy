/*
 * Copyright 2015 the original author or authors.
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
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for BaselineXmlReportWriter.
 *
 * @author Chris Mair
 */
class BaselineXmlReportWriterTest extends AbstractXmlReportWriterTestCase {

    private static final REPORT_XML = """<?xml version="1.0" encoding="UTF-8"?>
    <CodeNarc url="${CODENARC_URL}" version="${VERSION}">
        <Report timestamp="${FORMATTED_TIMESTAMP}" type="baseline"/>

        <Project title="My Cool Project">
            <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
            <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
        </Project>

        <File path="src/main/MyAction.groovy">
            <Violation ruleName="UnusedImport"/>
            <Violation ruleName="EmptyCatchBlock">
                <Message><![CDATA[Other info]]></Message>
            </Violation>
            <Violation ruleName="EmptyCatchBlock">
                <Message><![CDATA[Other info]]></Message>
            </Violation>
            <Violation ruleName="UnusedImport"/>
            <Violation ruleName="UnusedPrivateMethod">
                <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
            </Violation>
        </File>

        <File path="src/main/dao/MyDao.groovy">
            <Violation ruleName="EmptyCatchBlock">
                <Message><![CDATA[Other info]]></Message>
            </Violation>
        </File>
        <File path="src/main/dao/MyOtherDao.groovy">
            <Violation ruleName="UnusedPrivateMethod">
                <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
            </Violation>
        </File>
    </CodeNarc>
    """

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def xmlAsString = stringWriter.toString()
        assertXml(xmlAsString, REPORT_XML)
        log xmlAsString
        assert xmlAsString.readLines().size() > 28      // make sure it is formatted (pretty-printed)
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
    void testDefaultOutputFile_CodeNarcXmlReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcBaselineViolations.xml'
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @Before
    void setUpBaselineXmlReportWriterTest() {
        reportWriter = new BaselineXmlReportWriter(title:TITLE)
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

        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }

}
