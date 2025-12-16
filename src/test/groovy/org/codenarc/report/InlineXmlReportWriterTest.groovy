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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for InlineXmlReportWriter.
 *
 * @author Robin Bramley
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class InlineXmlReportWriterTest extends AbstractXmlReportWriterTestCase<InlineXmlReportWriter> {

    private static final REPORT_XML = """<?xml version='1.0'?>
    <CodeNarc url='${CODENARC_URL}' version='${VERSION}'>
        <Report timestamp='${FORMATTED_TIMESTAMP}'/>

        <Project title='My Cool Project'>
            <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
            <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
        </Project>
        <PackageSummary totalFiles='4' filesWithViolations='3' priority1='2' priority2='0' priority3='5'></PackageSummary>
        <Package path='src/main' totalFiles='4' filesWithViolations='3' priority1='2' priority2='0' priority3='5'>
            <File name='MyAction.groovy'>
                <Violation ruleName='Rule1' priority='1' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99 &amp;&amp; name.contains('')) {]]></SourceLine>
                    <Description><![CDATA[No description provided for rule named [Rule1]]]></Description>
                </Violation>
                <Violation ruleName='Rule3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                    <Description><![CDATA[No description provided for rule named [Rule3]]]></Description>
                </Violation>
                <Violation ruleName='Rule3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                    <Description><![CDATA[No description provided for rule named [Rule3]]]></Description>
                </Violation>
                <Violation ruleName='Rule1' priority='1' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99 &amp;&amp; name.contains('')) {]]></SourceLine>
                    <Description><![CDATA[No description provided for rule named [Rule1]]]></Description>
                </Violation>
                <Violation ruleName='Rule2' priority='3' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                    <Description><![CDATA[No description provided for rule named [Rule2]]]></Description>
                </Violation>
            </File>
        </Package>
        <Package path='src/main/dao' totalFiles='2' filesWithViolations='2' priority1='0' priority2='0' priority3='2'>
            <File name='MyDao.groovy'>
                <Violation ruleName='Rule3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info c:\\\\data]]></Message>
                    <Description><![CDATA[No description provided for rule named [Rule3]]]></Description>
                </Violation>
            </File>
            <File name='MyOtherDao.groovy'>
                <Violation ruleName='Rule2' priority='3' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                    <Description><![CDATA[No description provided for rule named [Rule2]]]></Description>
                </Violation>
            </File>
        </Package>
        <Package path='src/test' totalFiles='0' filesWithViolations='0' priority1='0' priority2='0' priority3='0'></Package>
    </CodeNarc>
    """

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def xmlAsString = stringWriter.toString()
        assertXml(xmlAsString, REPORT_XML)
    }

    @Override
    protected InlineXmlReportWriter createReportWriter() {
        return new InlineXmlReportWriter(title:TITLE)
    }

    @BeforeEach
    void setUpInlineXmlReportWriterTest() {
        def srcMainDirResults = new DirectoryResults('src/main')
        def srcMainDaoDirResults = new DirectoryResults('src/main/dao')
        def srcTestDirResults = new DirectoryResults('src/test')
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def srcMainFileResults2 = new FileResults('src/main/MyCleanAction.groovy', [])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainFileResults2)
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
