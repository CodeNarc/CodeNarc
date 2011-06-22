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
package org.codenarc

import org.codenarc.test.AbstractTestCase
import org.codenarc.report.HtmlReportWriter
import org.codenarc.results.FileResults
import org.codenarc.analyzer.FilesystemSourceAnalyzer
import org.codenarc.report.XmlReportWriter

/**
 * Tests for CodeNarc command-line runner
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CodeNarcTest extends AbstractTestCase {
    static final BASE_DIR = 'src/test/resources'
    static final BASIC_RULESET = 'rulesets/basic.xml'
    static final RULESET1 = 'rulesets/RuleSet1.xml'
    static final RULESET_FILES = 'rulesets/RuleSet1.xml,rulesets/RuleSet2.xml'
    static final INCLUDES = 'sourcewithdirs/**/*.groovy'
    static final EXCLUDES = '**/*File2.groovy'
    static final TITLE = 'My Title'
    static final HTML_REPORT_FILE = 'CodeNarcTest-Report.html'
    static final HTML_REPORT_STR = "html:$HTML_REPORT_FILE"
    static final XML_REPORT_FILE = 'CodeNarcTest-Report.xml'
    static final XML_REPORT_STR = "xml:$XML_REPORT_FILE"
    static final RESULTS = new FileResults('path', [])

    private codeNarc
    private outputFile

    void testParseArgs_InvalidOptionName() {
        shouldFailWithMessageContaining('unknown') { parseArgs('-unknown=abc') }
    }

    void testParseArgs_InvalidOption_NoHyphen() {
        shouldFailWithMessageContaining('bad') { parseArgs('bad=abc') }
    }

    void testParseArgs_InvalidOption_NoEquals() {
        shouldFailWithMessageContaining('badstuff') { parseArgs('badstuff') }
    }

    void testParseArgs_SingleRuleSetFile() {
        parseArgs("-rulesetfiles=$RULESET1")
        assert codeNarc.ruleSetFiles == RULESET1
    }

    void testParseArgs_BaseDir() {
        parseArgs("-basedir=$BASE_DIR")
        assert codeNarc.baseDir == BASE_DIR
    }

    void testParseArgs_Includes() {
        parseArgs("-includes=$INCLUDES")
        assert codeNarc.includes == INCLUDES
    }

    void testParseArgs_Excludes() {
        parseArgs("-excludes=$EXCLUDES")
        assert codeNarc.excludes == EXCLUDES
    }

    void testParseArgs_Title() {
        parseArgs("-title=$TITLE")
        assert codeNarc.title == TITLE
    }

    void testParseArgs_SingleHtmlReport() {
        parseArgs("-report=$HTML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

    void testParseArgs_SingleXmlReport() {
        parseArgs("-report=$XML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == XmlReportWriter
        assert codeNarc.reports[0].outputFile == XML_REPORT_FILE
    }

    void testParseArgs_SingleReportSpecifyingFullReportWriterClassName() {
        def reportString = "org.codenarc.report.HtmlReportWriter:$HTML_REPORT_FILE"
        parseArgs("-report=$reportString")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

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

    void testParseArgs_InvalidReportType() {
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf') }
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf:MyReport.pdf') }
    }

    void testSetDefaultsIfNecessary_ValuesNotSet() {
        codeNarc.setDefaultsIfNecessary()
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.ruleSetFiles == BASIC_RULESET
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, null)
        assert codeNarc.baseDir == '.'
    }

    void testSetDefaultsIfNecessary_TitleSet() {
        codeNarc.title = 'abc'
        codeNarc.setDefaultsIfNecessary()
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, 'abc')
    }

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
    }

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
    }

    void testMain() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]
        CodeNarc.main(ARGS)
        assert outputFile.exists()
    }

    void testMain_Help() {
        final ARGS = ['-help'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert !stdout.contains('ERROR')
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
    }

    void testMain_BadOptionFormat() {
        final ARGS = ["-report=$HTML_REPORT_STR", '&^%#BAD%$#'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert stdout.contains(ARGS[1])
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
    }

    void testMain_UnknownOption() {
        final ARGS = ['-unknown=23', "-report=$HTML_REPORT_STR"] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert stdout.contains(ARGS[0])
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    void setUp() {
        super.setUp()
        codeNarc = new CodeNarc()
        outputFile = new File(HTML_REPORT_FILE)
    }

    void tearDown() {
        super.tearDown()
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
