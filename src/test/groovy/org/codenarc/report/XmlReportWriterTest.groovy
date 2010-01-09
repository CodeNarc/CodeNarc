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

import org.codenarc.test.AbstractTestCase
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.rule.basic.BooleanInstantiationRule
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.AnalysisContext
import org.codenarc.rule.Violation
import org.codenarc.rule.StubRule
import java.text.DateFormat

/**
 * Tests for XmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReportWriterTest extends AbstractTestCase {

    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final LINE3 = 333
    private static final SOURCE_LINE1 = 'if (count < 23 && index <= 99) {'
    private static final SOURCE_LINE3 = 'throw new Exception() // Some very long message 1234567890123456789012345678901234567890'
    private static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    private static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3 )
    private static final NEW_REPORT_FILE = 'NewXmlReport.xml'
    private static final TITLE = 'My Cool Project'
    private static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    private static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    private static final VERSION = new File(VERSION_FILE).text
    private static final TIMESTAMP_DATE = new Date(1262361072497)
    private static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    private static final REPORT_XML = """<?xml version="1.0"?>
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
                    <SourceLine><![CDATA[if (count < 23 && index <= 99) {]]></SourceLine>
                </Violation>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception() // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception() // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
                <Violation ruleName='RULE1' priority='1' lineNumber='111'>
                    <SourceLine><![CDATA[if (count < 23 && index <= 99) {]]></SourceLine></Violation>
                <Violation ruleName='RULE2' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&*()_+<>]]></Message>
                </Violation>
            </File>
        </Package>

        <Package path='src/main/dao' totalFiles='2' filesWithViolations='2' priority1='0' priority2='1' priority3='1'>
            <File name='MyDao.groovy'>
                <Violation ruleName='RULE3' priority='3' lineNumber='333'>
                    <SourceLine><![CDATA[throw new Exception() // Some very long message 1234567890123456789012345678901234567890]]></SourceLine>
                    <Message><![CDATA[Other info]]></Message>
                </Violation>
            </File>
            <File name='MyOtherDao.groovy'>
                <Violation ruleName='RULE2' priority='2' lineNumber='222'>
                    <Message><![CDATA[bad stuff: !@#\$%^&*()_+<>]]></Message>
                </Violation>
            </File>
        </Package>

        <Package path='src/test' totalFiles='3' filesWithViolations='0' priority1='0' priority2='0' priority3='0'>
        </Package>

        <Rules>
            <Rule name='BooleanInstantiation'>
                <Description><![CDATA[Use <em>Boolean.valueOf()</em> for variable values or <em>Boolean.TRUE</em> and <em>Boolean.FALSE</em> for constant values instead of calling the <em>Boolean()</em> constructor directly or calling <em>Boolean.valueOf(true)</em> or <em>Boolean.valueOf(false)</em>.]]></Description>
            </Rule>
            <Rule name='DuplicateImport'>
                <Description><![CDATA[Custom: Duplicate imports]]></Description>
            </Rule>
        </Rules>
    </CodeNarc>
    """

    private reportWriter
    private analysisContext
    private results, srcMainDaoDirResults
    private ruleSet
    private stringWriter

    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def xmlAsString = stringWriter.toString()
        assertXml(xmlAsString)
    }

    void testWriteReport_Writer_ProperPackageSummaryForPackageWithEmptyRelativePath() {
        final XML = """
            <PackageSummary totalFiles='2' filesWithViolations='2' priority1='0' priority2='1' priority3='1'>
            </PackageSummary>

            <Package path='' totalFiles='2' filesWithViolations='2' priority1='0' priority2='1' priority3='1'>
        """
        srcMainDaoDirResults.path = ''
        def rootResults = new DirectoryResults()
        rootResults.addChild(srcMainDaoDirResults)
        reportWriter.writeReport(stringWriter, analysisContext, rootResults)
        assertContainsXml(stringWriter.toString(), XML)
    }

    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcXmlReport.xml')
        def xmlAsString = reportFile.text
        // reportFile.delete()      // keep report file around for easy inspection
        assertXml(xmlAsString)
    }

    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def xmlAsString = reportFile.text
        reportFile.delete()
        assertXml(xmlAsString)
    }

    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    void testDefaultOutputFile_CodeNarcXmlReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcXmlReport.xml'
    }

    void setUp() {
        super.setUp()
        reportWriter = new XmlReportWriter(title:TITLE)
        reportWriter.getTimestamp = { TIMESTAMP_DATE }

        def srcMainDirResults = new DirectoryResults(path:'src/main', numberOfFilesInThisDirectory:1)
        srcMainDaoDirResults = new DirectoryResults(path:'src/main/dao', numberOfFilesInThisDirectory:2)
        def srcTestDirResults = new DirectoryResults(path:'src/test', numberOfFilesInThisDirectory:3)
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
            new BooleanInstantiationRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }

    private void assertXml(String actualXml) {
        log(actualXml)
        assertEquals(normalizeXml(REPORT_XML), normalizeXml(actualXml))
    }

    private void assertContainsXml(String actualXml, String expectedPartialXml) {
        log(actualXml)
        assert normalizeXml(actualXml).contains(normalizeXml(expectedPartialXml))
    }

    private String normalizeXml(String xml) {
        return xml.replaceAll(/\>\s*\</, '><').trim()
    }

}