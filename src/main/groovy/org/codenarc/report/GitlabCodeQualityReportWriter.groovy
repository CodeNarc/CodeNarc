/*
 * Copyright 2024 the original author or authors.
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
 * limitations under the License

 * @author Nicolas Vuillamy
 */
package org.codenarc.report

import org.codenarc.AnalysisContext
import org.codenarc.results.FileResults
import org.codenarc.results.Results

import groovy.json.JsonOutput

/**
 * ReportWriter that generates a JSON report in the format expected by GitLab's Code Quality service.
 *
 * See https://docs.gitlab.com/ee/ci/testing/code_quality.html#implement-a-custom-tool
 */
class GitlabCodeQualityReportWriter extends AbstractReportWriter {
    String defaultOutputFile = 'CodeNarcGitlabCodeQualityReport.json'

    Boolean writeAsSingleLine = false

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()

        // mapping between CodeNarc priorities and output severities
        // TODO: Verify that these are good defaults.
        // TODO: Optionally make this configurable.
        def priorityMap = [
            1: 'info',
            2: 'major',
            3: 'blocker',
        ]

        // generate report from results
        def fileResults = getFileResults(results)
        def resultsObj = []
        fileResults.each { fileResult ->
            fileResult.getViolations().each { violation ->
                resultsObj << [
                    description: violation.message,
                    check_name: violation.rule.name,
                    // generate fingerprint from the source line
                    // This uses the hash of the line with leading and
                    // trailing whitespace trimmed. Trimming first ensures
                    // that even when indenting the line differently, it
                    // will still be recognized.
                    fingerprint: violation.sourceLine.trim().digest('SHA-1'),
                    severity: priorityMap[violation.rule.priority],
                    location: [
                        path: fileResult.getPath(),
                        lines: [
                            begin: violation.lineNumber,
                        ]
                    ]
                ]
            }
        }

        /*
           Append JSON to writer
           - isWriteAsSingleLine == true: writes result in a stdout single line
           - isWriteAsSingleLine == false: pretty print it for easier reading
        */
        def json = JsonOutput.toJson(resultsObj)
        if (!isWriteAsSingleLine()) {
            json = JsonOutput.prettyPrint(json)
        }
        if (isWriteToStandardOut()) {
            def printWriter = new PrintWriter(writer)
            printWriter.println(json)
            printWriter.flush()
        }
        else {
            writer << json
        }
    }

    private List<FileResults> getFileResults(Results results, List<FileResults> fileResults = []) {
        if (results.isFile()) {
            fileResults << results
        }
        else {
            results.children.each { child ->
                getFileResults(child, fileResults)
            }
        }
        return fileResults
    }

    boolean isWriteAsSingleLine() {
        writeAsSingleLine == true
    }
}
