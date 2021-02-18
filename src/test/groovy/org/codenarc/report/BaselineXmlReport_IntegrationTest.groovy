/*
 * Copyright 2018 the original author or authors.
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
import org.codenarc.plugin.FileViolations
import org.codenarc.plugin.baseline.BaselineResultsPlugin
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.Violation
import org.codenarc.rule.basic.EmptyCatchBlockRule
import org.codenarc.rule.imports.UnusedImportRule
import org.codenarc.rule.unused.UnusedPrivateMethodRule
import org.codenarc.test.AbstractTestCase
import org.codenarc.util.io.StringResource
import org.junit.Before
import org.junit.Test

/**
 * Integration tests for BaselineXmlReportWriter, BaselineXmlReportParser and BaselineResultsPlugin.
 */
class BaselineXmlReport_IntegrationTest extends AbstractTestCase {

    private static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    private static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    private static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<> "Guaraní" "75%–84%" "Tschüß" "…" "str\t\n\r"" <&>"'
    private static final MESSAGE3 = 'Other info'
    private static final VIOLATION1 = new Violation(rule:new UnusedImportRule(), sourceLine:'111')
    private static final VIOLATION2 = new Violation(rule:new UnusedPrivateMethodRule(), message:MESSAGE2)
    private static final VIOLATION3 = new Violation(rule:new EmptyCatchBlockRule(), sourceLine:'333', message:MESSAGE3)

    private BaselineXmlReportWriter reportWriter = new BaselineXmlReportWriter()
    private DirectoryResults results
    private DirectoryResults srcMainDaoDirResults

    private FileResults srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
    private FileResults fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
    private FileResults fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])

    private AnalysisContext analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:null)
    private StringWriter stringWriter = new StringWriter()

    @Test
    void test_WriteAndProcessBaselineXmlReport_NoNewViolations() {
        // Write report
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def baselineXml = stringWriter.toString()
        log("baselineXml=$baselineXml")

        // Process report
        BaselineResultsPlugin plugin = new BaselineResultsPlugin(new StringResource(baselineXml))
        plugin.initialize()
        plugin.processViolationsForFile(new FileViolations(srcMainFileResults1))
        plugin.processViolationsForFile(new FileViolations(fileResultsMainDao1))
        plugin.processViolationsForFile(new FileViolations(fileResultsMainDao2))

        assert results.violations.size() == 0
        assert plugin.numViolationsRemoved == 7
    }

    @Test
    void test_WriteAndProcessBaselineXmlReport_OneNewViolation() {
        // Write report
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def baselineXml = stringWriter.toString()

        // Add an extra Violation
        def newFileResults = new FileResults('src/main/dao/SomeOtherDao.groovy', [VIOLATION1])
        srcMainDaoDirResults.addChild(newFileResults)

        // Process report
        BaselineResultsPlugin plugin = new BaselineResultsPlugin(new StringResource(baselineXml))
        plugin.initialize()

        plugin.processViolationsForFile(new FileViolations(srcMainFileResults1))
        plugin.processViolationsForFile(new FileViolations(fileResultsMainDao1))
        plugin.processViolationsForFile(new FileViolations(fileResultsMainDao2))
        plugin.processViolationsForFile(new FileViolations(newFileResults))

        // Just to log final count
        plugin.processReports(null)

        assert results.violations.size() == 1
        assert plugin.numViolationsRemoved == 7
    }

    @Before
    void setUp() {
        def srcMainDirResults = new DirectoryResults('src/main', 1)
        srcMainDaoDirResults = new DirectoryResults('src/main/dao', 2)
        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)

        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:null)
        stringWriter = new StringWriter()
    }

}
