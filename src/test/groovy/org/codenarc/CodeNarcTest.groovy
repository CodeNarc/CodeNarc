/*
 * Copyright 2012 the original author or authors.
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
package org.codenarc

import org.codenarc.analyzer.FilesystemSourceAnalyzer
import org.codenarc.report.HtmlReportWriter
import org.codenarc.report.XmlReportWriter
import org.codenarc.test.AbstractTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for CodeNarc command-line runner
 *
 * @author Chris Mair
 */
class CodeNarcTest extends AbstractTestCase {

    private static final BASE_DIR = 'src/test/resources'
    private static final BASIC_RULESET = 'rulesets/basic.xml'
    private static final RULESET1 = 'rulesets/RuleSet1.xml'
    private static final INCLUDES = 'sourcewithdirs/**/*.groovy'
    private static final EXCLUDES = '**/*File2.groovy'
    private static final TITLE = 'My Title'
    private static final HTML_REPORT_FILE = new File('CodeNarcTest-Report.html').absolutePath
    private static final HTML_REPORT_STR = "html:$HTML_REPORT_FILE"
    private static final XML_REPORT_FILE = 'CodeNarcTest-Report.xml'
    private static final XML_REPORT_STR = "xml:$XML_REPORT_FILE"

    private codeNarc
    private outputFile
    private int exitCode

    @Test
    void testParseArgs_InvalidOptionName() {
        shouldFailWithMessageContaining('unknown') { parseArgs('-unknown=abc') }
    }

    @Test
    void testParseArgs_InvalidOption_NoHyphen() {
        shouldFailWithMessageContaining('bad') { parseArgs('bad=abc') }
    }

    @Test
    void testParseArgs_InvalidOption_NoEquals() {
        shouldFailWithMessageContaining('badstuff') { parseArgs('badstuff') }
    }

    @Test
    void testParseArgs_SingleRuleSetFile() {
        parseArgs("-rulesetfiles=$RULESET1")
        assert codeNarc.ruleSetFiles == RULESET1
    }

    @Test
    void testParseArgs_BaseDir() {
        parseArgs("-basedir=$BASE_DIR")
        assert codeNarc.baseDir == BASE_DIR
    }

    @Test
    void testParseArgs_Includes() {
        parseArgs("-includes=$INCLUDES")
        assert codeNarc.includes == INCLUDES
    }

    @Test
    void testParseArgs_Excludes() {
        parseArgs("-excludes=$EXCLUDES")
        assert codeNarc.excludes == EXCLUDES
    }

    @Test
    void testParseArgs_Title() {
        parseArgs("-title=$TITLE")
        assert codeNarc.title == TITLE
    }

    @Test
    void testParseArgs_SingleHtmlReport() {
        parseArgs("-report=$HTML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

    @Test
    void testParseArgs_SingleXmlReport() {
        parseArgs("-report=$XML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == XmlReportWriter
        assert codeNarc.reports[0].outputFile == XML_REPORT_FILE
    }

    @Test
    void testParseArgs_SingleReportSpecifyingFullReportWriterClassName() {
        def reportString = "org.codenarc.report.HtmlReportWriter:$HTML_REPORT_FILE"
        parseArgs("-report=$reportString")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

    @Test
    void testParseArgs_ThreeReports() {
        parseArgs("-report=$HTML_REPORT_STR", '-report=html', "-report=$XML_REPORT_STR")
        assert codeNarc.reports.size() == 3
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
        assert codeNarc.reports[1].class == HtmlReportWriter
        assert codeNarc.reports[1].outputFile == null
        assert codeNarc.reports[2].class == XmlReportWriter
        assert codeNarc.reports[2].outputFile == XML_REPORT_FILE
    }

    @Test
    void testParseArgs_InvalidReportType() {
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf') }
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf:MyReport.pdf') }
    }

    @Test
    void testSetDefaultsIfNecessary_ValuesNotSet() {
        codeNarc.setDefaultsIfNecessary()
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.ruleSetFiles == BASIC_RULESET
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, null)
        assert codeNarc.baseDir == '.'
    }

    @Test
    void testSetDefaultsIfNecessary_TitleSet() {
        codeNarc.title = 'abc'
        codeNarc.setDefaultsIfNecessary()
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, 'abc')
    }

    @Test
    void testSetDefaultsIfNecessary_ValuesAlreadySet() {
        codeNarc.includes = 'aaa'
        codeNarc.ruleSetFiles = 'bbb'
        codeNarc.reports = ['ccc']      // just need a non-empty list
        codeNarc.baseDir = 'ddd'
        codeNarc.setDefaultsIfNecessary()
        assert codeNarc.includes == 'aaa'
        assert codeNarc.ruleSetFiles == 'bbb'
        assert codeNarc.reports == ['ccc']
        assert codeNarc.baseDir == 'ddd'
    }

    @Test
    void testExecute() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]

        def codeNarcRunner = [execute: { }]
        codeNarc.createCodeNarcRunner = { codeNarcRunner }

        codeNarc.execute(ARGS)

        assert codeNarc.ruleSetFiles == RULESET1
        assert codeNarc.includes == INCLUDES
        assert codeNarc.excludes == EXCLUDES

        def sourceAnalyzer = codeNarcRunner.sourceAnalyzer
        assert sourceAnalyzer.class == FilesystemSourceAnalyzer
        assert sourceAnalyzer.baseDirectory == BASE_DIR
        assert sourceAnalyzer.includes == INCLUDES
        assert sourceAnalyzer.excludes == EXCLUDES

        assert codeNarcRunner.ruleSetFiles == RULESET1

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
        assert exitCode == 0
    }

    @Test
    void testExecute_NoArgs() {
        final ARGS = [] as String[]

        def codeNarcRunner = [execute: { }]
        codeNarc.createCodeNarcRunner = { codeNarcRunner }

        codeNarc.execute(ARGS)

        assert codeNarc.ruleSetFiles == BASIC_RULESET
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.excludes == null

        assert codeNarcRunner.sourceAnalyzer.class == FilesystemSourceAnalyzer
        assert codeNarcRunner.sourceAnalyzer.baseDirectory == '.'
        assert codeNarcRunner.ruleSetFiles == BASIC_RULESET

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assertReport(reportWriter, HtmlReportWriter, null, null)
        assert exitCode == 0
    }

    @Test
    void testMain() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]
        CodeNarc.main(ARGS)
        assert outputFile.exists()
        assert exitCode == 0
    }

    @Test
    void testMain_Help() {
        final ARGS = ['-help'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert !stdout.contains('ERROR')
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
        assert exitCode == 0
    }

    @Test
    void testMain_BadOptionFormat() {
        final ARGS = ["-report=$HTML_REPORT_STR", '&^%#BAD%$#'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert stdout.contains(ARGS[1])
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
        assert exitCode == 1
    }

    @Test
    void testMain_UnknownOption() {
        final ARGS = ['-unknown=23', "-report=$HTML_REPORT_STR"] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert stdout.contains(ARGS[0])
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
        assert exitCode == 1
    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    @Before
    void setUp() {
        codeNarc = new CodeNarc()
        codeNarc.systemExit = { code -> exitCode = code }
        outputFile = new File(HTML_REPORT_FILE)
    }

    @After
    void tearDown() {
        outputFile.delete()
    }

    private void parseArgs(Object[] args) {
        def argsAsArray = args as String[]
        codeNarc.parseArgs(argsAsArray)
    }

    private void assertReport(report, Class reportClass, String toFile, String title) {
        assert report.class == reportClass
        assert report.outputFile == toFile
        assert report.title == title
    }

}
