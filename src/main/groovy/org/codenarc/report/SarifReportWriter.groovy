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

import groovy.json.JsonOutput
import org.codenarc.AnalysisContext
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Rule

/**
 * Writes a report in the Static Analysis Results Interchange Format (SARIF).
 *
 * See https://sarifweb.azurewebsites.net/
 */
class SarifReportWriter extends AbstractJsonReportWriter {

    String defaultOutputFile = 'CodeNarcSarifReport.sarif.json'

    @Override
    Object buildJsonStructure(AnalysisContext analysisContext, Results results) {
        def toolRules = buildToolRules(analysisContext)
        def sarifResults = buildSarifResults(results)
        return [
            '$schema': 'https://json.schemastore.org/sarif-2.1.0.json',
            version: '2.1.0',
            runs: [
                [
                    tool: [
                        driver: [
                            name: 'CodeNarc',
                            version: getCodeNarcVersion(),
                            informationUri: CODENARC_URL,
                            rules: toolRules
                        ]
                    ],
                    results: sarifResults
                ]
            ]
        ]
    }

    private List<Map> buildToolRules(AnalysisContext analysisContext) {
        def sortedRules = getSortedRules(analysisContext)
        return sortedRules.collect { Rule rule ->
            def packageName = rule.class.package.name
            def category = packageName.tokenize('.').last()
            [
                id: rule.name,
                name: rule.class.simpleName,
                shortDescription: [
                    text: getDescriptionForRule(rule)
                ],
                helpUri: "${CODENARC_URL}/codenarc-rules-${category}.html#${rule.name.toLowerCase()}-rule",
                properties: [
                    priority: rule.priority
                ]
            ]
        }
    }

    private List<Map> buildSarifResults(Results results) {
        def fileResults = getFileResults(results)
        def sarifResults = []
        fileResults.each { fileResult ->
            fileResult.violations.each { violation ->
                sarifResults << [
                    ruleId: violation.rule.name,
                    level: convertPriorityToLevel(violation.rule.priority),
                    message: [
                        text: violation.message ?: getDescriptionForRule(violation.rule)
                    ],
                    locations: [
                        [
                            physicalLocation: [
                                artifactLocation: [
                                    uri: fileResult.path
                                ],
                                region: buildRegion(violation)
                            ]
                        ]
                    ]
                ]
            }
        }
        return sarifResults
    }

    private Map buildRegion(violation) {
        def region = [startLine: violation.lineNumber]
        if (violation.sourceLine) {
            region.snippet = [text: violation.sourceLine]
        }
        return region
    }

    private String convertPriorityToLevel(int priority) {
        switch (priority) {
            case 1: return 'error'
            case 2: return 'warning'
            case 3: return 'note'
            default: return 'warning'
        }
    }

    private List<FileResults> getFileResults(Results results, List<FileResults> fileResults = []) {
        if (results.isFile()) {
            fileResults << results
        } else {
            results.children.each { child ->
                getFileResults(child, fileResults)
            }
        }
        return fileResults
    }
}
