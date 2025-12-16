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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for BaselineXmlReportWriter.
 *
 * @author Chris Mair
 */
class BaselineXmlReportWriterTest extends AbstractXmlReportWriterTestCase<BaselineXmlReportWriter> {

    private static final REPORT_XML = """<?xml version="1.0" encoding="UTF-8"?>
        <CodeNarc url="${CODENARC_URL}" version="${VERSION}">
            <Report type="baseline"/>
            <Project title="My Cool Project">
                <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
                <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
            </Project>
            <File path="src/main/MyAction.groovy">
                <Violation ruleName="Rule1"/>
                <Violation ruleName="Rule3">
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                </Violation>
                <Violation ruleName="Rule3">
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                </Violation>
                <Violation ruleName="Rule1"/>
                <Violation ruleName="Rule2">
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                </Violation>
            </File>
            <File path="src/main/dao/MyDao.groovy">
                <Violation ruleName="Rule3">
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                </Violation>
            </File>
            <File path="src/main/dao/MyOtherDao.groovy">
                <Violation ruleName="Rule2">
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
    @Override
    protected BaselineXmlReportWriter createReportWriter() {
        return new BaselineXmlReportWriter(title:TITLE)
    }

    @BeforeEach
    void setUpBaselineXmlReportWriterTest() {
        def dirResults = new DirectoryResults()
        def dirResultsMain = new DirectoryResults('src/main')
        def dirResultsMainDao = new DirectoryResults('src/main/dao')
        def dirResultsTest = new DirectoryResults('src/test')
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])
        def fileResultsMyAction = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResultsMyCleanAction = new FileResults('src/main/MyCleanAction.groovy', [])

        // assemble results
        // The order here is not alphabetically, because directory scanning
        // isn't either, but the baseline report has to sort the data, which
        // is done via the comparison with the report XML above.
        dirResultsMain.addChild(fileResultsMyAction)
        dirResultsMain.addChild(fileResultsMyCleanAction)
        dirResultsMain.addChild(dirResultsMainDao)
        dirResultsMainDao.addChild(fileResultsMainDao2)
        dirResultsMainDao.addChild(fileResultsMainDao1)
        dirResults.addChild(dirResultsTest)
        dirResults.addChild(dirResultsMain)

        // init baseclass fields
        results = dirResults
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }

}
