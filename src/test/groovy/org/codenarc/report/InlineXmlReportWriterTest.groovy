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
import org.codenarc.rule.Violation
import org.codenarc.rule.basic.EmptyCatchBlockRule
import org.codenarc.rule.imports.UnusedImportRule
import org.codenarc.rule.unused.UnusedPrivateMethodRule
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import java.text.DateFormat

import static org.junit.Assert.assertEquals

/**
 * Tests for InlineXmlReportWriter.
 *
 * @author Robin Bramley
 * @author Hamlet D'Arcy
 */
class InlineXmlReportWriterTest extends AbstractTestCase {

    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final LINE3 = 333
    private static final SOURCE_LINE1 = 'if (count < 23 && index <= 99) {'
    private static final SOURCE_LINE3 = 'throw new Exception("cdata=<![CDATA[whatever]]>") // Some very long message 1234567890123456789012345678901234567890'
    private static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    private static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new UnusedImportRule(), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new UnusedPrivateMethodRule(), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new EmptyCatchBlockRule(), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3 )
    private static final TITLE = 'My Cool Project'
    private static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    private static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    private static final VERSION = new File(VERSION_FILE).text
    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final REPORT_XML = """<?xml version='1.0'?>
    <CodeNarc url='http://www.codenarc.org' version='${VERSION}'>
        <Report timestamp='${FORMATTED_TIMESTAMP}'/>

        <Project title='My Cool Project'>
            <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
            <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
        </Project>

        <PackageSummary totalFiles='6' filesWithViolations='3' priority1='0' priority2='5' priority3='2'>
        </PackageSummary>

        <Package path='src/main' totalFiles='3' filesWithViolations='3' priority1='0' priority2='5' priority3='2'>
            <File name='MyAction.groovy'>
                <Violation ruleName='UnusedImport' priority='3' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99) {]]></SourceLine>
                    <Description><![CDATA[Imports for a class that is never referenced within the source file is unnecessary.]]></Description>
                </Violation>
                <Violation ruleName='EmptyCatchBlock' priority='2' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                    <Description><![CDATA[In most cases, exceptions should not be caught and ignored (swallowed).]]></Description>
                </Violation>
                <Violation ruleName='EmptyCatchBlock' priority='2' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                    <Description><![CDATA[In most cases, exceptions should not be caught and ignored (swallowed).]]></Description>
                </Violation>
                <Violation ruleName='UnusedImport' priority='3' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99) {]]></SourceLine>
                    <Description><![CDATA[Imports for a class that is never referenced within the source file is unnecessary.]]></Description>
                </Violation>
                <Violation ruleName='UnusedPrivateMethod' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                    <Description><![CDATA[Checks for private methods that are not referenced within the same class.]]></Description>
                </Violation>
            </File>
        </Package>

        <Package path='src/main/dao' totalFiles='2' filesWithViolations='2' priority1='0' priority2='2' priority3='0'>
            <File name='MyDao.groovy'>
                <Violation ruleName='EmptyCatchBlock' priority='2' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                    <Description><![CDATA[In most cases, exceptions should not be caught and ignored (swallowed).]]></Description>
                </Violation>
            </File>
            <File name='MyOtherDao.groovy'>
                <Violation ruleName='UnusedPrivateMethod' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                    <Description><![CDATA[Checks for private methods that are not referenced within the same class.]]></Description>
                </Violation>
            </File>
        </Package>

        <Package path='src/test' totalFiles='3' filesWithViolations='0' priority1='0' priority2='0' priority3='0'>
        </Package>
    </CodeNarc>
    """

    private reportWriter
    private analysisContext
    private results, srcMainDaoDirResults
    private ruleSet
    private stringWriter

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def xmlAsString = stringWriter.toString()
        assertXml(xmlAsString)
    }

    @Before
    void setUpInlineXmlReportWriterTest() {
        reportWriter = new InlineXmlReportWriter(title:TITLE)
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

    @SuppressWarnings('JUnitStyleAssertions')
    private void assertXml(String actualXml) {
        log(actualXml)
        assertEquals(normalizeXml(REPORT_XML), normalizeXml(actualXml))
    }

    private String normalizeXml(String xml) {
        xml.replaceAll(/\>\s*\</, '><').trim()
    }

}
