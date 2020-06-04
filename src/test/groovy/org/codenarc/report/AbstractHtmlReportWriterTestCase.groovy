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
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.rule.basic.ReturnFromFinallyBlockRule
import org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule
import org.codenarc.rule.unnecessary.UnnecessaryStringInstantiationRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before

import static org.junit.Assert.assertEquals

/**
 * Abstract superclass for HTML ReportWriter test classes
 *
 * @author Chris Mair
 */
@SuppressWarnings('LineLength')
abstract class AbstractHtmlReportWriterTestCase extends AbstractTestCase {

    protected static final LONG_LINE = 'throw new Exception() // Some very long message 1234567890123456789012345678901234567890'
    protected static final MESSAGE = 'bad stuff'
    protected static final LINE1 = 111
    protected static final LINE2 = 222
    protected static final LINE3 = 333
    protected static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:'if (file) {')
    protected static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE)
    protected static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:LONG_LINE, message: 'Other info')
    protected static final VIOLATION4 = new Violation(rule:new StubRule(name:'RULE4', priority:4), lineNumber:LINE1, sourceLine:'if (file) {')
    protected static final NEW_REPORT_FILE = createTemporaryReportFile().absolutePath
    protected static final TITLE = 'My Cool Project'
    protected static final String LOGO_FILE = 'http://codenarc.github.io/CodeNarc/images/codenarc-logo.png'
    protected static final String CODENARC_URL = 'https://www.codenarc.org'

    protected reportWriter
    protected analysisContext
    protected results
    protected dirResultsMain
    protected ruleSet
    protected String cssFileContents

    //------------------------------------------------------------------------------------
    // Setup and tear-down and helper methods
    //------------------------------------------------------------------------------------

    @Before
    void setUpAbstractHtmlReportWriterTest() {
        log(new File('.').absolutePath)

        dirResultsMain = new DirectoryResults('src/main', 1)
        def dirResultsCode = new DirectoryResults('src/main/code', 2)
        def dirResultsTest = new DirectoryResults('src/main/test', 3)
        def dirResultsTestSubdirNoViolations = new DirectoryResults('src/main/test/noviolations', 4)
        def dirResultsTestSubdirEmpty = new DirectoryResults('src/main/test/empty')
        def fileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def fileResults2 = new FileResults('src/main/MyAction2.groovy', [VIOLATION3])
        def fileResults3 = new FileResults('src/main/MyActionTest.groovy', [VIOLATION1, VIOLATION2])
        dirResultsMain.addChild(fileResults1)
        dirResultsMain.addChild(dirResultsCode)
        dirResultsMain.addChild(dirResultsTest)
        dirResultsCode.addChild(fileResults2)
        dirResultsTest.addChild(fileResults3)
        dirResultsTest.addChild(dirResultsTestSubdirNoViolations)
        dirResultsTest.addChild(dirResultsTestSubdirEmpty)
        results = new DirectoryResults()
        results.addChild(dirResultsMain)

        ruleSet = new ListRuleSet([
                new UnnecessaryBooleanInstantiationRule(),
                new ReturnFromFinallyBlockRule(),
                new UnnecessaryStringInstantiationRule(),
                new ThrowExceptionFromFinallyBlockRule(),
                new DuplicateImportRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:['/src/main'], ruleSet:ruleSet)

        cssFileContents = new File('src/main/resources/codenarc-htmlreport.css').text
    }

    protected String getReportText() {
        def writer = new StringWriter()
        reportWriter.writeReport(writer, analysisContext, results)
        return writer.toString()
    }

    protected void assertReportContents(String expected) {
        String actual = getReportText()
        assertEquals(normalizeXml(expected), normalizeXml(actual))
    }

    protected void assertReportFileContents(String filename, String expected) {
        reportWriter.writeReport(analysisContext, results)
        def actual = new File(filename).text
        assertEquals(normalizeXml(expected), normalizeXml(actual))
    }

    protected static File createTemporaryReportFile() {
        def file = File.createTempFile('CodeNarcReport', '.html')
        file.deleteOnExit()
        return file
    }

    /**
     * Normalize the XML string. Remove all whitespace between elements, and normalize line-endings.
     * @param xml - the input XML string to normalize
     * @return the normalized XML
     */
    protected static String normalizeXml(String xml) {
        assert xml != null
        def resultXml = xml.replaceAll(/\>\s*\</, '><').trim()
        return resultXml.replace('\r\n', '\n')
    }

}
