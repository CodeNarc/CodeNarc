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

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.analyzer.FilesystemSourceAnalyzer
import org.codenarc.report.*
import org.codenarc.results.Results
import org.codenarc.test.AbstractTestCase
import org.codenarc.util.CodeNarcVersion
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Tests for CodeNarc command-line runner
 *
 * @author Chris Mair
 * @author Nicolas Vuillamy
 */
class CodeNarcTest extends AbstractTestCase {

    private static final String BASE_DIR = 'src/test/resources'
    private static final String BASIC_RULESET = 'rulesets/basic.xml'
    private static final String RULESET1 = 'rulesets/RuleSet1.xml'
    private static final String INCLUDES = 'sourcewithdirs/**/*.groovy'
    private static final String EXCLUDES = '**/*File2.groovy'
    private static final String PROPERTIES_FILENAME = 'some.properties'
    private static final String TITLE = 'My Title'
    private static final String HTML_REPORT_FILE = new File('CodeNarcTest-Report.html').absolutePath
    private static final String HTML_REPORT_STR = "html:$HTML_REPORT_FILE"
    private static final String XML_REPORT_FILE = 'CodeNarcTest-Report.xml'
    private static final String XML_REPORT_STR = "xml:$XML_REPORT_FILE"
    private static final String XML_REPORT_STDOUT_STR = 'xml:stdout'
    private static final String JSON_REPORT_FILE = 'CodeNarcTest-Report.json'
    private static final String JSON_REPORT_STR = "json:$JSON_REPORT_FILE"
    private static final String JSON_REPORT_STDOUT_STR = 'json:stdout'
    private static final String PLUGIN_NAMES = 'abc,def'
    private static final int P1 = 1, P2 = 2, P3 = 3
    private static final String RULESET_AS_JSON = URLEncoder.encode('{ "org.codenarc.rule.StubRule": { "name": "XXXX"} }', 'UTF-8')

    private CodeNarc codeNarc
    private File outputFile
    private int exitCode

    private final Map numViolations = [:].withDefault { 0 }
    private final Results results = [
        getNumberOfViolationsWithPriority: { priority, recursive ->
            assert recursive == true
            return numViolations[priority]
        }] as Results
    private codeNarcRunner = [execute: { results }]

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------

    @Test
    void test_parseArgs_InvalidOptionName() {
        shouldFailWithMessageContaining('unknown') { parseArgs('-unknown=abc') }
    }

    @Test
    void test_parseArgs_InvalidOption_NoHyphen() {
        shouldFailWithMessageContaining('bad') { parseArgs('bad=abc') }
    }

    @Test
    void test_parseArgs_InvalidOption_NoEquals() {
        shouldFailWithMessageContaining('badstuff') { parseArgs('badstuff') }
    }

    @Test
    void test_parseArgs_SingleRuleSetFile() {
        parseArgs("-rulesetfiles=$RULESET1")
        assert codeNarc.ruleSetFiles == RULESET1
    }

    @Test
    void test_parseArgs_BaseDir() {
        parseArgs("-basedir=$BASE_DIR")
        assert codeNarc.baseDir == BASE_DIR
    }

    @Test
    void test_parseArgs_Includes() {
        parseArgs("-includes=$INCLUDES")
        assert codeNarc.includes == INCLUDES
    }

    @Test
    void test_parseArgs_Excludes() {
        parseArgs("-excludes=$EXCLUDES")
        assert codeNarc.excludes == EXCLUDES
    }

    @Test
    void test_parseArgs_Title() {
        parseArgs("-title=$TITLE")
        assert codeNarc.title == TITLE
    }

    @Test
    void test_parseArgs_Properties() {
        parseArgs("-properties=$PROPERTIES_FILENAME")
        assert codeNarc.propertiesFilename == PROPERTIES_FILENAME
    }

    @Test
    void test_parseArgs_SingleHtmlReport() {
        parseArgs("-report=$HTML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

    @Test
    void test_parseArgs_SingleHtmlReport_WritingToStandardOut() {
        parseArgs('-report=html:stdout')
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].writeToStandardOut == true
    }

    @Test
    void test_parseArgs_SingleXmlReport() {
        parseArgs("-report=$XML_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == XmlReportWriter
        assert codeNarc.reports[0].outputFile == XML_REPORT_FILE
    }

    @Test
    void test_parseArgs_SingleXmlReportStdout() {
        parseArgs("-report=$XML_REPORT_STDOUT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == XmlReportWriter
        assert codeNarc.reports[0].isWriteToStandardOut()
    }

    @Test
    void test_parseArgs_SingleJsonReport() {
        parseArgs("-report=$JSON_REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == JsonReportWriter
        assert codeNarc.reports[0].outputFile == JSON_REPORT_FILE
    }

    @Test
    void test_parseArgs_SingleJsonReportStdout() {
        parseArgs("-report=$JSON_REPORT_STDOUT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == JsonReportWriter
        assert codeNarc.reports[0].isWriteToStandardOut()
        assert codeNarc.reports[0].isWriteAsSingleLine() == true
    }

    @Test
    void test_parseArgs_SingleReportSpecifyingFullReportWriterClassName() {
        def reportString = "org.codenarc.report.HtmlReportWriter:$HTML_REPORT_FILE"
        parseArgs("-report=$reportString")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].class == HtmlReportWriter
        assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
    }

    @Test
    void test_parseArgs_ThreeReports() {
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
    void test_parseArgs_InvalidReportType() {
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf') }
        shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf:MyReport.pdf') }
    }

    @Test
    void test_setDefaultsIfNecessary_ValuesNotSet() {
        codeNarc.setDefaultsIfNecessary()
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.ruleSetFiles == BASIC_RULESET
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, null)
        assert codeNarc.baseDir == '.'
    }

    @Test
    void test_setDefaultsIfNecessary_TitleSet() {
        codeNarc.title = 'abc'
        codeNarc.setDefaultsIfNecessary()
        assertReport(codeNarc.reports[0], HtmlReportWriter, null, 'abc')
    }

    @Test
    void test_setDefaultsIfNecessary_ValuesAlreadySet() {
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
    void test_checkMaxViolations_ActualLessThanOrEqualToMax() {
        codeNarc.checkMaxViolations(results, P1, 1)
        assert exitCode == 0

        codeNarc.checkMaxViolations(results, P2, 0)
        assert exitCode == 0

        codeNarc.checkMaxViolations(results, P3, 0)
        assert exitCode == 0
    }

    @Test
    void test_checkMaxViolations_ActualExceedsMax() {
        numViolations[P1] = 2
        codeNarc.checkMaxViolations(results, P1, 1)
        assert exitCode == 1

        numViolations[P2] = 2
        codeNarc.checkMaxViolations(results, P2, 1)
        assert exitCode == 1

        numViolations[P3] = 2
        codeNarc.checkMaxViolations(results, P3, 1)
        assert exitCode == 1
    }

    // Tests for execute()

    @Test
    void test_execute() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]

        codeNarc.execute(ARGS)

        assert codeNarc.ruleSetFiles == RULESET1
        assert codeNarc.includes == INCLUDES
        assert codeNarc.excludes == EXCLUDES

        def sourceAnalyzer = codeNarcRunner.sourceAnalyzer
        assert sourceAnalyzer.class == FilesystemSourceAnalyzer
        assert sourceAnalyzer.baseDirectory == BASE_DIR
        assert sourceAnalyzer.includes == INCLUDES
        assert sourceAnalyzer.excludes == EXCLUDES

        assert codeNarcRunner.propertiesFile == null
        assert codeNarcRunner.ruleSetFiles == RULESET1

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
        assert exitCode == 0
    }

    @Test
    void test_execute_NoArgs() {
        final ARGS = [] as String[]

        codeNarc.execute(ARGS)

        assert codeNarc.ruleSetFiles == BASIC_RULESET
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.excludes == null

        assert codeNarcRunner.sourceAnalyzer.class == FilesystemSourceAnalyzer
        assert codeNarcRunner.sourceAnalyzer.baseDirectory == '.'
        assert codeNarcRunner.ruleSetFiles == BASIC_RULESET
        assert codeNarcRunner.propertiesFile == null

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assertReport(reportWriter, HtmlReportWriter, null, null)
        assert exitCode == 0
    }

    @Test
    void test_execute_RuleSetStringJSON() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-ruleset=$RULESET_AS_JSON"] as String[]

        codeNarc.execute(ARGS)

        assert codeNarc.ruleset == URLDecoder.decode(RULESET_AS_JSON, 'UTF-8')
        assert codeNarc.includes == INCLUDES
        assert codeNarc.excludes == EXCLUDES

        def sourceAnalyzer = codeNarcRunner.sourceAnalyzer
        assert sourceAnalyzer.class == FilesystemSourceAnalyzer
        assert sourceAnalyzer.baseDirectory == BASE_DIR
        assert sourceAnalyzer.includes == INCLUDES
        assert sourceAnalyzer.excludes == EXCLUDES

        assert codeNarcRunner.ruleSetString == URLDecoder.decode(RULESET_AS_JSON, 'UTF-8')

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
        assert exitCode == 0
    }

    @Test
    void test_execute_Plugins() {
        final ARGS = ["-plugins=$PLUGIN_NAMES"] as String[]

        def pluginClassNames
        codeNarcRunner = [execute: { results }, registerPluginsForClassNames: { names -> pluginClassNames = names }]

        codeNarc.execute(ARGS)

        assert pluginClassNames == PLUGIN_NAMES
    }

    @Test
    void test_execute_Properties() {
        final ARGS = ["-properties=$PROPERTIES_FILENAME"] as String[]

        codeNarc.execute(ARGS)

        assert codeNarcRunner.propertiesFile == null
    }

    @Test
    void test_execute_ExceedsMaxPriority1Violations() {
        final ARGS = ['-maxPriority1Violations=3'] as String[]
        numViolations[P1] = 4
        codeNarc.execute(ARGS)
        assert exitCode == 1
    }

    @Test
    void test_execute_ExceedsMaxPriority2Violations() {
        final ARGS = ['-maxPriority2Violations=3'] as String[]
        numViolations[P2] = 4
        codeNarc.execute(ARGS)
        assert exitCode == 1
    }

    @Test
    void test_execute_ExceedsMaxPriority3Violations() {
        final ARGS = ['-maxPriority3Violations=3'] as String[]
        numViolations[P3] = 4
        codeNarc.execute(ARGS)
        assert exitCode == 1
    }

    @Test
    void test_execute_ReportClassDoesNotSupportSetTitle() {
        final ARGS = ["-report=${NoTitleReportWriter.name}", "-title=$TITLE"] as String[]

        codeNarc.execute(ARGS)

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assert reportWriter.class == NoTitleReportWriter
        assert exitCode == 0
    }

    @Test
    void test_execute_ReportWritesToStandardOut() {
        final ARGS = ['-report=xml:stdout'] as String[]

        codeNarc.execute(ARGS)

        assert codeNarcRunner.reportWriters.size == 1
        def reportWriter = codeNarcRunner.reportWriters[0]
        assert reportWriter.class == XmlReportWriter
        assert reportWriter.writeToStandardOut
        assert exitCode == 0
    }

    // Test for main()

    @Test
    void test_main() {
        final ARGS = [
                "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]
        CodeNarc.main(ARGS)
        assert outputFile.exists()
        assert exitCode == 0
    }

    @Test
    void test_main_Help() {
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
    void test_main_Version() {
        final ARGS = ['-version'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert !stdout.contains('ERROR')
        def version = CodeNarcVersion.getVersion()
        def expectedVersion = "CodeNarc version $version"
        assert stdout.contains(expectedVersion), "$expectedVersion not found in $stdout"
        assert !outputFile.exists()
        assert exitCode == 0
    }

    @Test
    void test_main_BadOptionFormat() {
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
    void test_main_UnknownOption() {
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
        codeNarc.createCodeNarcRunner = { codeNarcRunner }
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

    private void assertReport(AbstractReportWriter report, Class reportClass, String toFile, String title) {
        assert report.class == reportClass
        assert report.outputFile == toFile
        assert report.title == title
        assert !report.writeToStandardOut
    }

}

class NoTitleReportWriter implements ReportWriter {

    @Override
    void writeReport(AnalysisContext analysisContext, Results results) {
    }

}
