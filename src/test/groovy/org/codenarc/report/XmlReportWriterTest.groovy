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
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import java.text.DateFormat

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for XmlReportWriter
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class XmlReportWriterTest extends AbstractTestCase {

    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final LINE3 = 333
    private static final SOURCE_LINE1 = "if (count < 23 && index <= 99 && name.contains('\u0000')) {"
    private static final SOURCE_LINE3 = 'throw new Exception("cdata=<![CDATA[whatever]]>") // Some very long message 1234567890123456789012345678901234567890'
    private static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    private static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3 )
    private static final NEW_REPORT_FILE = 'target/NewXmlReport.xml'
    private static final TITLE = 'My Cool Project'
    private static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    private static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    private static final VERSION = new File(VERSION_FILE).text
    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    @SuppressWarnings('LineLength')
    private static final REPORT_XML = """<?xml version='1.0'?>
    <CodeNarc url='http://www.codenarc.org' version='${VERSION}'>
        <Report timestamp='${FORMATTED_TIMESTAMP}'/>

        <Project title='My Cool Project'>
            <SourceDirectory>c:/MyProject/src/main/groovy</SourceDirectory>
            <SourceDirectory>c:/MyProject/src/test/groovy</SourceDirectory>
        </Project>

        <PackageSummary totalFiles='6' filesWithViolations='3' priority1='2' priority2='2' priority3='3'>
        </PackageSummary>

        <Package path='src/main' totalFiles='3' filesWithViolations='3' priority1='2' priority2='2' priority3='3'>
            <File name='MyAction.groovy'>
                <Violation ruleName='RULE1' priority='1' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99 &amp;&amp; name.contains('')) {]]></SourceLine>
                </Violation>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
                <Violation ruleName='RULE1' priority='1' lineNumber='111'>
                    <SourceLine><![CDATA[if (count &lt; 23 &amp;&amp; index &lt;= 99 &amp;&amp; name.contains('')) {]]></SourceLine></Violation>
                <Violation ruleName='RULE2' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                </Violation>
            </File>
        </Package>

        <Package path='src/main/dao' totalFiles='2' filesWithViolations='2' priority1='0' priority2='1' priority3='1'>
            <File name='MyDao.groovy'>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception("cdata=&lt;![CDATA[whatever]]&gt;") // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
            </File>
            <File name='MyOtherDao.groovy'>
                <Violation ruleName='RULE2' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                </Violation>
            </File>
        </Package>

        <Package path='src/test' totalFiles='3' filesWithViolations='0' priority1='0' priority2='0' priority3='0'>
        </Package>

        <Rules>
            <Rule name='DuplicateImport'>
                <Description><![CDATA[Custom: Duplicate imports]]></Description>
            </Rule>
            <Rule name='UnnecessaryBooleanInstantiation'>
                <Description><![CDATA[Use Boolean.valueOf() for variable values or Boolean.TRUE and Boolean.FALSE for constant values instead of calling the Boolean() constructor directly or calling Boolean.valueOf(true) or Boolean.valueOf(false).]]></Description>
            </Rule>
        </Rules>
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

    @Test
    void testWriteReport_Writer_ProperPackageSummaryForPackageWithEmptyRelativePath() {
        final XML = """
            <PackageSummary totalFiles='2' filesWithViolations='1' priority1='0' priority2='0' priority3='1'>
            </PackageSummary>

            <Package path='' totalFiles='2' filesWithViolations='1' priority1='0' priority2='0' priority3='1'>
        """
        def dirResults = new DirectoryResults('', 2)
        dirResults.addChild(new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3]))
        def rootResults = new DirectoryResults()
        rootResults.addChild(dirResults)
        reportWriter.writeReport(stringWriter, analysisContext, rootResults)
        assertContainsXml(stringWriter.toString(), XML)
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcXmlReport.xml')
        def xmlAsString = reportFile.text
        reportFile.delete()      // comment out to keep report file around for easy inspection
        assertXml(xmlAsString)
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def xmlAsString = reportFile.text
        reportFile.delete()
        assertXml(xmlAsString)
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
        assert reportWriter.defaultOutputFile == 'CodeNarcXmlReport.xml'
    }

    //--------------------------------------------------------------------------
    // Setup and helper methods
    //--------------------------------------------------------------------------

    @Test
    void testRemoveIllegalCharacters() {
        assert reportWriter.removeIllegalCharacters('\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008') == ''
        assert reportWriter.removeIllegalCharacters('\uD800') == ''

        // Valid chars
        assert reportWriter.removeIllegalCharacters('') == ''
        assert reportWriter.removeIllegalCharacters('01234567890 ABC abc') == '01234567890 ABC abc'
        assert reportWriter.removeIllegalCharacters('!@#$%^&*()-_=+[]{};\'",./<>?') == '!@#$%^&*()-_=+[]{};\'",./<>?'
        assert reportWriter.removeIllegalCharacters('\u0009') == '\u0009'
        assert reportWriter.removeIllegalCharacters('\t\n\r') == '\t\n\r'
        assert reportWriter.removeIllegalCharacters('\uE000') == '\uE000'
    }

    @Before
    void setUpXmlReportWriterTest() {
        reportWriter = new XmlReportWriter(title:TITLE)
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

    private void assertXml(String actualXml) {
        log(actualXml)
        assert normalizeXml(REPORT_XML) == normalizeXml(actualXml)

        // Verify that it is valid XML
        new XmlSlurper().parseText(actualXml)
    }

    private void assertContainsXml(String actualXml, String expectedPartialXml) {
        log(actualXml)
        assert normalizeXml(actualXml).contains(normalizeXml(expectedPartialXml))
    }

    private String normalizeXml(String xml) {
        xml.replaceAll(/\>\s*\</, '><').trim()
    }

}
