/*
 * Copyright 2025 the original author or authors.
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

import static org.codenarc.test.TestUtil.*

import groovy.json.JsonOutput
import org.junit.jupiter.api.Test

/**
 * Tests for GitlabCodeQualityReportWriter
 */
class GitlabCodeQualityReportWriterTest extends AbstractJsonReportWriterTestCase<GitlabCodeQualityReportWriter> {

    private static final NEW_REPORT_FILE = 'target/NewGitlabCodeQualityReport.json'

    private static final List<Map> EXPECTED_LIST = [
        [
            description: VIOLATION1.message,
            check_name: VIOLATION1.rule.name,
            fingerprint: (VIOLATION1.sourceLine),
            severity: 'info',
            location: [path: 'src/main/MyAction.groovy', lines: [begin: VIOLATION1.lineNumber]]
        ],
        [
            description: VIOLATION3.message,
            check_name: VIOLATION3.rule.name,
            fingerprint: (VIOLATION3.sourceLine),
            severity: 'blocker',
            location: [path: 'src/main/MyAction.groovy', lines: [begin: VIOLATION3.lineNumber]]
        ],
        // duplicate VIOLATION3
        [
            description: VIOLATION3.message,
            check_name: VIOLATION3.rule.name,
            fingerprint: (VIOLATION3.sourceLine),
            severity: 'blocker',
            location: [path: 'src/main/MyAction.groovy', lines: [begin: VIOLATION3.lineNumber]]
        ],
        // second VIOLATION1
        [
            description: VIOLATION1.message,
            check_name: VIOLATION1.rule.name,
            fingerprint: (VIOLATION1.sourceLine),
            severity: 'info',
            location: [path: 'src/main/MyAction.groovy', lines: [begin: VIOLATION1.lineNumber]]
        ],
        // VIOLATION2 (no sourceLine) in src/main/MyAction.groovy
        [
            description: VIOLATION2.message,
            check_name: VIOLATION2.rule.name,
            fingerprint: ("src/main/MyAction.groovy:${VIOLATION2.lineNumber}:${VIOLATION2.rule.name}").digest('SHA-1'),
            severity: 'blocker',
            location: [path: 'src/main/MyAction.groovy', lines: [begin: VIOLATION2.lineNumber]]
        ],
        // VIOLATION3 in src/main/dao/MyDao.groovy
        [
            description: VIOLATION3.message,
            check_name: VIOLATION3.rule.name,
            fingerprint: (VIOLATION3.sourceLine),
            severity: 'blocker',
            location: [path: 'src/main/dao/MyDao.groovy', lines: [begin: VIOLATION3.lineNumber]]
        ],
        // VIOLATION2 in src/main/dao/MyOtherDao.groovy
        [
            description: VIOLATION2.message,
            check_name: VIOLATION2.rule.name,
            fingerprint: ("src/main/dao/MyOtherDao.groovy:${VIOLATION2.lineNumber}:${VIOLATION2.rule.name}").digest('SHA-1'),
            severity: 'blocker',
            location: [path: 'src/main/dao/MyOtherDao.groovy', lines: [begin: VIOLATION2.lineNumber]]
        ]
    ]

    private static final String REPORT_JSON = JsonOutput.prettyPrint(JsonOutput.toJson(EXPECTED_LIST)).trim()

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def jsonAsString = stringWriter.toString().trim()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assert output
        assertJson(output.trim(), REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcGitlabCodeQualityReport.json')
        def jsonAsString = reportFile.text.trim()
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def jsonAsString = reportFile.text.trim()
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
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
    void testDefaultOutputFile_CodeNarcGitlabCodeQualityReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcGitlabCodeQualityReport.json'
    }

    @Override
    protected GitlabCodeQualityReportWriter createReportWriter() {
        return new GitlabCodeQualityReportWriter()
    }

}
