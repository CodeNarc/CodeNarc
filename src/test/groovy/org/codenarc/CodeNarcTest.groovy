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

import org.codenarc.test.AbstractTest
import org.codenarc.ant.CodeNarcTaskAccessor
import org.codenarc.ant.CodeNarcTask
import org.codenarc.report.HtmlReportWriter

/**
 * Tests for CodeNarc command-line runner
 *
 * @author Chris Mair
 * @version $Revision: 106 $ - $Date: 2009-03-28 22:36:10 -0400 (Sat, 28 Mar 2009) $
 */
class CodeNarcTest extends AbstractTest {
    static final BASE_DIR = 'src/test/resources'
    static final RULESET1 = 'rulesets/RuleSet1.xml'
    static final RULESET_FILES = 'rulesets/RuleSet1.xml,rulesets/RuleSet2.xml'
    static final INCLUDES = 'sourcewithdirs/**/*.groovy'
    static final EXCLUDES = '**/*File2.groovy'
    static final TITLE = 'My Title'
    static final REPORT_FILE = 'CodeNarcTest-Report.html'
    static final REPORT_STR = "html:$REPORT_FILE"

    private codeNarc
    private outputFile

    // TODO Test help option?

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

    void testParseArgs_Report() {
        parseArgs("-report=$REPORT_STR")
        assert codeNarc.reports.size() == 1
        assert codeNarc.reports[0].type == 'html'
        assert codeNarc.reports[0].toFile == REPORT_FILE
    }

    void testParseArgs_TwoReports() {
        parseArgs("-report=$REPORT_STR", '-report=xml')
        assert codeNarc.reports.size() == 2
        assert codeNarc.reports[0].type == 'html'
        assert codeNarc.reports[0].toFile == REPORT_FILE
        assert codeNarc.reports[1].type == 'xml'
        assert codeNarc.reports[1].toFile == null
    }

    void testBuildAntTask() {
        parseArgs("-report=$REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET_FILES")
        def antTaskRaw = codeNarc.buildAntTask()
        assert antTaskRaw instanceof CodeNarcTask
        def antTask = new CodeNarcTaskAccessor(antTaskRaw)
        def fileSet = antTask.fileSet
        def project = fileSet.project
        assert fileSet.getDir(project) == new File(BASE_DIR)
        def dirScanner = fileSet.getDirectoryScanner(project)

        def includedFiles = dirScanner.includedFiles as List
        log("includedFiles=${includedFiles}")
        assert includedFiles.contains(convertToLocalFileSeparators('sourcewithdirs/SourceFile1.groovy'))
        assert !includedFiles.find { it.startsWith('rule') }

        def excludedFiles = dirScanner.excludedFiles as List
        log("excludedFiles=${excludedFiles}")
        assert excludedFiles.contains(convertToLocalFileSeparators('sourcewithdirs/subdir1/Subdir1File2.groovy'))

        assert antTask.ruleSetFiles == RULESET_FILES

        assert antTask.reportWriters.size() == 1
        assertReportWriter(antTask.reportWriters[0], TITLE, REPORT_FILE)
    }

    void testSetDefaultsIfNecessary_ValuesNotSet() {
        codeNarc.setDefaultsIfNecessary()
        assert codeNarc.includes == '**/*.groovy'
        assert codeNarc.ruleSetFiles == 'rulesets/basic.xml'
        assertReport(codeNarc.reports[0], 'html', null, null)
        assert codeNarc.baseDir == '.'
    }

    void testSetDefaultsIfNecessary_TitleSet() {
        codeNarc.title = 'abc'
        codeNarc.setDefaultsIfNecessary()
        assertReport(codeNarc.reports[0], 'html', null, 'abc')
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

    void testMain() {
        final ARGS = [
                "-report=$REPORT_STR", "-basedir=$BASE_DIR", "-includes=$INCLUDES",
                "-title=$TITLE", "-excludes=$EXCLUDES", "-rulesetfiles=$RULESET1"] as String[]
        CodeNarc.main(ARGS)
        assert outputFile.exists()
    }

    void testMain_BadOptionFormat() {
        final ARGS = ["-report=$REPORT_STR", '&^%#BAD%$#'] as String[]
        def stdout = captureSystemOut {
            CodeNarc.main(ARGS)
        }
        log("stdout=[$stdout]")
        assert stdout.contains(ARGS[1])
        assert stdout.contains(CodeNarc.HELP)
        assert !outputFile.exists()
    }

    void testMain_UnknownOption() {
        final ARGS = ["-unknown=23", "-report=$REPORT_STR"] as String[]
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
        outputFile = new File(REPORT_FILE)
    }

    void tearDown() {
        super.tearDown()
        outputFile.delete()
    }

    private void parseArgs(Object... args) {
        def argsAsArray = args as String[]
        codeNarc.parseArgs(argsAsArray)
    }

    private void assertReportWriter(reportWriter, String title, String outputFile) {
        assert reportWriter instanceof HtmlReportWriter
        assert reportWriter.title == title
        assert reportWriter.outputFile == outputFile
    }

    private void assertReport(report, String type, String toFile, String title) {
        assert report.type == type
        assert report.toFile == toFile
        assert report.title == title
    }

    private String convertToLocalFileSeparators(String path) {
        final SEP = '/' as char
        char separatorChar = System.getProperty("file.separator").charAt(0)
        return (separatorChar != SEP) ? path.replace(SEP, separatorChar) : path
    }

    private String captureSystemOut(Closure closure) {
        def originalSystemOut = System.out
        def byteOut = new ByteArrayOutputStream()
        def newSystemOut = new PrintStream(byteOut)
        try {
            System.out = newSystemOut
            closure.run()
        }
        finally {
            System.out = originalSystemOut
        }
        return byteOut.toString()
    }
}
