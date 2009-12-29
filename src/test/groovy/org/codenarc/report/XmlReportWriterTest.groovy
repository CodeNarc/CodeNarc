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
import org.codenarc.rule.basic.ReturnFromFinallyBlockRule
import org.codenarc.rule.basic.StringInstantiationRule
import org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.AnalysisContext
import org.codenarc.rule.Violation
import org.codenarc.rule.StubRule

/**
 * Tests for XmlReportWriter
 *
 * @author Chris Mair
 * @version $Revision: 259 $ - $Date: 2009-12-26 22:10:00 -0500 (Sat, 26 Dec 2009) $
 */
class XmlReportWriterTest extends AbstractTestCase {
    private static final LONG_LINE = 'throw new Exception() // Some very long message 1234567890123456789012345678901234567890'
    private static final MESSAGE = 'bad stuff'
    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final LINE3 = 333
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:'if (file) {')
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE)
    private static final VIOLATION3 = new Violation(rule:new StubRule(name:'RULE3', priority:3), lineNumber:LINE3, sourceLine:LONG_LINE, message: 'Other info')
    private static final CODENARC_URL = "http://www.codenarc.org"
    private static final NEW_REPORT_FILE = 'NewXmlReport.html'
    private static final TITLE = 'My Cool Project'
    private static final SRC_DIR1 = 'src/main/groovy'
    private static final SRC_DIR2 = 'src/test/groovy'
    private static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    private static final BASIC_CONTENTS = [
            //HTML_TAG,
            'Report timestamp',
            'Summary by Package', 'Package', 'Total Files', 'Files with Violations', 'Priority 1', 'Priority 2', 'Priority 3',
            'MyAction.groovy', MESSAGE, LONG_LINE,
            'MyAction2.groovy',
            'MyActionTest.groovy',
            //BOTTOM_LINK
        ]

    private reportWriter
    private analysisContext
    private results
    private ruleSet
    private stringWriter
    private xmlSlurper
    private codeNarcVersion

    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        log(stringWriter.toString())
        def codeNarc = xmlSlurper.parseText(stringWriter.toString())
        assert codeNarc.@url == CODENARC_URL
        assert codeNarc.@version == codeNarcVersion

        def project = codeNarc.Project
        assert project.@title == TITLE
        assert project.SourceDirectory.list() == [SRC_DIR1, SRC_DIR2]

        // <Package name="org.codenarc.sample.domain" totalFiles="7" filesWithViolation="5" priority1="2" priority2="11" priority3="5">
        // <Class className="org.codenarc.sample.service.NewService">
        assert codeNarc.Package.size() == 2

        def all = codeNarc.Package[0]
        assertAttributes(all,
            [path:'[ALL]', totalFiles:1, filesWithViolations:1, priority1:2, priority2:1, priority3:2])

        def srcMain = codeNarc.Package[1]
        assertAttributes(srcMain,
            [path:'src/main', totalFiles:1, filesWithViolations:1, priority1:2, priority2:1, priority3:2])

        assert srcMain.File.size() == 1
        def file1 = srcMain.File[0]
//        assertAttributes(file1, [file:''])
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

        def dirResultsMain = new DirectoryResults(path:'src/main', numberOfFilesInThisDirectory:1)
//        def dirResultsCode = new DirectoryResults(path:'src/main/code', numberOfFilesInThisDirectory:2)
//        def dirResultsTest = new DirectoryResults(path:'src/main/test', numberOfFilesInThisDirectory:3)
//        def dirResultsTestSubdirNoViolations = new DirectoryResults(path:'src/main/test/noviolations', numberOfFilesInThisDirectory:4)
//        def dirResultsTestSubdirEmpty = new DirectoryResults(path:'src/main/test/empty')
        def fileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
//        def fileResults2 = new FileResults('src/main/MyAction2.groovy', [VIOLATION3])
//        def fileResults3 = new FileResults('src/main/MyActionTest.groovy', [VIOLATION1, VIOLATION2])
        dirResultsMain.addChild(fileResults1)

//        dirResultsMain.addChild(dirResultsCode)
//        dirResultsMain.addChild(dirResultsTest)
//        dirResultsCode.addChild(fileResults2)
//        dirResultsTest.addChild(fileResults3)
//        dirResultsTest.addChild(dirResultsTestSubdirNoViolations)
//        dirResultsTest.addChild(dirResultsTestSubdirEmpty)
        results = new DirectoryResults()
        results.addChild(dirResultsMain)

        ruleSet = new ListRuleSet([
                new BooleanInstantiationRule(),
                new ReturnFromFinallyBlockRule(),
                new StringInstantiationRule(),
                new ThrowExceptionFromFinallyBlockRule(),
                new DuplicateImportRule()
        ])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
        xmlSlurper = new XmlSlurper()
        codeNarcVersion = new File(VERSION_FILE).text
    }

    private void assertAttributes(node, Map attributes) {
        attributes.each { k, v ->
            assert node.@"$k" == v, "Attribute [$k]: expected [$v] but was [${node.@"$k"}]"
        }
    }
}