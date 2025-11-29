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

import org.codenarc.analyzer.FilesSourceAnalyzer

import static org.codenarc.test.TestUtil.captureSystemOut
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.analyzer.FilesystemSourceAnalyzer
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.*
import org.codenarc.results.Results
import org.codenarc.test.AbstractTestCase
import org.codenarc.util.CodeNarcVersion
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
    private static final String EXCLUDE_FILE = 'config/CodeNarcBaselineViolations.xml'
    private static final String INCLUDES = 'sourcewithdirs/**/*.groovy'
    private static final String EXCLUDES = '**/*File2.groovy'
    private static final String SOURCE_FILES = 'src/test/resources/sourcewithdirs/SourceFile1.groovy,src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy,src/test/resources/sourcewithdirs/subdir1/Subdir1File2.groovy'
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

    @Nested
    class ParseArgs {

        @Test
        void InvalidOptionName() {
            shouldFailWithMessageContaining('unknown') { parseArgs('-unknown=abc') }
        }

        @Test
        void InvalidOption_NoHyphen() {
            shouldFailWithMessageContaining('bad') { parseArgs('bad=abc') }
        }

        @Test
        void InvalidOption_NoEquals() {
            shouldFailWithMessageContaining('badstuff') { parseArgs('badstuff') }
        }

        @Test
        void SingleRuleSetFile() {
            parseArgs("-rulesetfiles=$RULESET1")
            assert codeNarc.ruleSetFiles == RULESET1
        }

        @Test
        void excludeBaseline() {
            parseArgs('-excludeBaseline=' + EXCLUDE_FILE)
            assert codeNarc.excludeBaseline == EXCLUDE_FILE
        }

        @Test
        void baseDir() {
            parseArgs("-basedir=$BASE_DIR")
            assert codeNarc.baseDir == BASE_DIR
        }

        @Test
        void includes() {
            parseArgs("-includes=$INCLUDES")
            assert codeNarc.includes == INCLUDES
        }

        @Test
        void excludes() {
            parseArgs("-excludes=$EXCLUDES")
            assert codeNarc.excludes == EXCLUDES
        }

        @Test
        void title() {
            parseArgs("-title=$TITLE")
            assert codeNarc.title == TITLE
        }

        @Test
        void properties() {
            parseArgs("-properties=$PROPERTIES_FILENAME")
            assert codeNarc.propertiesFilename == PROPERTIES_FILENAME
        }

        @Test
        void failOnError() {
            parseArgs('-failOnError=true')
            assert codeNarc.failOnError == true

            parseArgs('-failOnError=false')
            assert codeNarc.failOnError == false
        }

        @Test
        void SingleHtmlReport() {
            parseArgs("-report=$HTML_REPORT_STR")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == HtmlReportWriter
            assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
        }

        @Test
        void SingleHtmlReport_WritingToStandardOut() {
            parseArgs('-report=html:stdout')
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == HtmlReportWriter
            assert codeNarc.reports[0].writeToStandardOut == true
        }

        @Test
        void SingleXmlReport() {
            parseArgs("-report=$XML_REPORT_STR")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == XmlReportWriter
            assert codeNarc.reports[0].outputFile == XML_REPORT_FILE
        }

        @Test
        void SingleXmlReportStdout() {
            parseArgs("-report=$XML_REPORT_STDOUT_STR")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == XmlReportWriter
            assert codeNarc.reports[0].isWriteToStandardOut()
        }

        @Test
        void SingleJsonReport() {
            parseArgs("-report=$JSON_REPORT_STR")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == JsonReportWriter
            assert codeNarc.reports[0].outputFile == JSON_REPORT_FILE
        }

        @Test
        void SingleJsonReportStdout() {
            parseArgs("-report=$JSON_REPORT_STDOUT_STR")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == JsonReportWriter
            assert codeNarc.reports[0].isWriteToStandardOut()
            assert codeNarc.reports[0].isWriteAsSingleLine() == true
        }

        @Test
        void SingleReportSpecifyingFullReportWriterClassName() {
            def reportString = "org.codenarc.report.HtmlReportWriter:$HTML_REPORT_FILE"
            parseArgs("-report=$reportString")
            assert codeNarc.reports.size() == 1
            assert codeNarc.reports[0].class == HtmlReportWriter
            assert codeNarc.reports[0].outputFile == HTML_REPORT_FILE
        }

        @Test
        void ThreeReports() {
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
        void InvalidReportType() {
            shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf') }
            shouldFailWithMessageContaining('pdf') { parseArgs('-report=pdf:MyReport.pdf') }
        }

    }

    @Nested
    class SetDefaultsIfNecessary {

        @Test
        void ValuesNotSet() {
            codeNarc.setDefaultsIfNecessary()
            assert codeNarc.includes == '**/*.groovy'
            assert codeNarc.ruleSetFiles == BASIC_RULESET
            assertReport(codeNarc.reports[0], HtmlReportWriter, null, null)
            assert codeNarc.baseDir == '.'
        }

        @Test
        void TitleSet() {
            codeNarc.title = 'abc'
            codeNarc.setDefaultsIfNecessary()
            assertReport(codeNarc.reports[0], HtmlReportWriter, null, 'abc')
        }

        @Test
        void ValuesAlreadySet() {
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
        void SourceFilesDefaults() {
            codeNarc.sourceFiles = 'a,b,c'
            codeNarc.setDefaultsIfNecessary()
            assert codeNarc.baseDir == '.'
            assert codeNarc.includes == null
            assert codeNarc.sourceFiles == 'a,b,c'
            assert codeNarc.ruleSetFiles == BASIC_RULESET
            assertReport(codeNarc.reports[0], HtmlReportWriter, null, null)
        }

    }

    @Nested
    class CheckMaxViolations {

        @Test
        void ActualLessThanOrEqualToMax() {
            codeNarc.checkMaxViolations(results, P1, 1)
            assert exitCode == 0

            codeNarc.checkMaxViolations(results, P2, 0)
            assert exitCode == 0

            codeNarc.checkMaxViolations(results, P3, 0)
            assert exitCode == 0
        }

        @Test
        void ActualExceedsMax() {
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

    }

    @Nested
    class Execute {

        @Test
        void MultipleOptions() {
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
            assert sourceAnalyzer.failOnError == false

            assert codeNarcRunner.propertiesFile == null
            assert codeNarcRunner.ruleSetFiles == RULESET1

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
            assert exitCode == 0
        }

        @Test
        void SourceFiles() {
            final ARGS = ["-sourcefiles=$SOURCE_FILES", "-rulesetfiles=$RULESET1"] as String[]

            codeNarc.execute(ARGS)

            assert codeNarc.ruleSetFiles == RULESET1

            SourceAnalyzer sourceAnalyzer = codeNarcRunner.sourceAnalyzer
            assert sourceAnalyzer.class == FilesSourceAnalyzer
            assert sourceAnalyzer.sourceFiles == SOURCE_FILES.split(',')
            assert sourceAnalyzer.failOnError == false
            assert sourceAnalyzer.baseDirectory == '.'

            assert codeNarcRunner.propertiesFile == null
            assert codeNarcRunner.ruleSetFiles == RULESET1

            assert exitCode == 0
        }

        @Test
        void SourceFiles_BaseDir() {
            final ARGS = ["-sourcefiles=$SOURCE_FILES", '-basedir=example'] as String[]

            codeNarc.execute(ARGS)

            FilesSourceAnalyzer sourceAnalyzer = codeNarcRunner.sourceAnalyzer as FilesSourceAnalyzer
            assert sourceAnalyzer.sourceFiles == SOURCE_FILES.split(',')
            assert sourceAnalyzer.baseDirectory == 'example'
        }

        @Test
        void NoArgs() {
            final ARGS = [] as String[]

            codeNarc.execute(ARGS)

            assert codeNarc.ruleSetFiles == BASIC_RULESET
            assert codeNarc.includes == '**/*.groovy'
            assert codeNarc.excludes == null

            assert codeNarcRunner.sourceAnalyzer.class == FilesystemSourceAnalyzer
            assert codeNarcRunner.sourceAnalyzer.baseDirectory == '.'
            assert codeNarcRunner.sourceAnalyzer.failOnError == false
            assert codeNarcRunner.ruleSetFiles == BASIC_RULESET
            assert codeNarcRunner.propertiesFile == null

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assertReport(reportWriter, HtmlReportWriter, null, null)
            assert exitCode == 0
        }

        @Test
        void RuleSetStringJSON() {
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

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
            assert exitCode == 0
        }

        @Test
        void BaselineExclude() {
            final ARGS = [
                    "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                    "-title=$TITLE", "-excludes=$EXCLUDES", "-ruleset=$RULESET_AS_JSON",
                    "-excludeBaseline=$EXCLUDE_FILE"] as String[]

            codeNarc.execute(ARGS)

            assert codeNarc.ruleset == URLDecoder.decode(RULESET_AS_JSON, 'UTF-8')
            assert codeNarc.excludeBaseline == EXCLUDE_FILE
            assert codeNarc.includes == INCLUDES
            assert codeNarc.excludes == EXCLUDES

            def sourceAnalyzer = codeNarcRunner.sourceAnalyzer
            assert sourceAnalyzer.class == FilesystemSourceAnalyzer
            assert sourceAnalyzer.baseDirectory == BASE_DIR
            assert sourceAnalyzer.includes == INCLUDES
            assert sourceAnalyzer.excludes == EXCLUDES

            assert codeNarcRunner.ruleSetString == URLDecoder.decode(RULESET_AS_JSON, 'UTF-8')

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assertReport(reportWriter, HtmlReportWriter, HTML_REPORT_FILE, TITLE)
            assert exitCode == 0
        }

        @Test
        void Plugins() {
            final ARGS = ["-plugins=$PLUGIN_NAMES"] as String[]

            def pluginClassNames
            codeNarcRunner = [execute: { results }, registerPluginsForClassNames: { names -> pluginClassNames = names }]

            codeNarc.execute(ARGS)

            assert pluginClassNames == PLUGIN_NAMES
        }

        @Test
        void Properties() {
            final ARGS = ["-properties=$PROPERTIES_FILENAME"] as String[]

            codeNarc.execute(ARGS)

            assert codeNarcRunner.propertiesFile == null
        }

        @Test
        void ExceedsMaxPriority1Violations() {
            final ARGS = ['-maxPriority1Violations=3'] as String[]
            numViolations[P1] = 4
            codeNarc.execute(ARGS)
            assert exitCode == 1
        }

        @Test
        void ExceedsMaxPriority2Violations() {
            final ARGS = ['-maxPriority2Violations=3'] as String[]
            numViolations[P2] = 4
            codeNarc.execute(ARGS)
            assert exitCode == 1
        }

        @Test
        void ExceedsMaxPriority3Violations() {
            final ARGS = ['-maxPriority3Violations=3'] as String[]
            numViolations[P3] = 4
            codeNarc.execute(ARGS)
            assert exitCode == 1
        }

        @Test
        void FailOnError() {
            final ARGS = ['-failOnError=true'] as String[]

            codeNarc.execute(ARGS)

            assert codeNarcRunner.sourceAnalyzer.failOnError == true
        }

        @Test
        void ReportClassDoesNotSupportSetTitle() {
            final ARGS = ["-report=${NoTitleReportWriter.name}", "-title=$TITLE"] as String[]

            codeNarc.execute(ARGS)

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assert reportWriter.class == NoTitleReportWriter
            assert exitCode == 0
        }

        @Test
        void ReportWritesToStandardOut() {
            final ARGS = ['-report=xml:stdout'] as String[]

            codeNarc.execute(ARGS)

            assert codeNarcRunner.reportWriters.size() == 1
            def reportWriter = codeNarcRunner.reportWriters[0]
            assert reportWriter.class == XmlReportWriter
            assert reportWriter.writeToStandardOut
            assert exitCode == 0
        }

    }

    @Nested
    class Main {

        @Test
        void MultipleOptions() {
            final ARGS = [
                    "-report=$HTML_REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                    "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]
            CodeNarc.main(ARGS)
            assert outputFile.exists()
            assert exitCode == 0
        }

        @Test
        void help() {
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
        void version() {
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
        void BadOptionFormat() {
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
        void UnknownOption() {
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

    }

    //--------------------------------------------------------------------------
    // Test setUp/tearDown and helper methods
    //--------------------------------------------------------------------------

    @BeforeEach
    void setUp() {
        codeNarc = new CodeNarc()
        codeNarc.systemExit = { code -> exitCode = code }
        codeNarc.createCodeNarcRunner = { codeNarcRunner }
        outputFile = new File(HTML_REPORT_FILE)
    }

    @AfterEach
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
