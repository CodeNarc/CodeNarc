/*
 * Copyright 2014 the original author or authors.
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
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.util.io.ClassPathResource

/**
 * ReportWriter that generates an HTML report that can be dynamically sorted in multiple ways.
 * <p/>
 * The default localized messages, including rule descriptions, are read from the "codenarc-base-messages"
 * ResourceBundle. You can override these messages using the normal ResourceBundle mechanisms (i.e.
 * creating a locale-specific resource bundle file on the classpath, such as "codenarc-base-messages_de").
 * You can optionally add rule descriptions for custom rules by placing them within a "codenarc-messages.properties"
 * file on the classpath, with entries of the form: {rule-name}.description=..."
 * <p/>
 * Set the includeRuleDescriptions property to false to exclude the rule descriptions section of the report. It defaults to true.
 * <p/>
 * Set the maxPriority property to control the maximum priority level for violations in
 * the report. For instance, setting maxPriority to 2 will result in the report containing
 * only priority 1 and 2 violations (and omitting violations with priority 3). The
 * maxPriority property defaults to 3.
 *
 * @author Chris Mair
 */
@SuppressWarnings('DuplicateMapLiteral')
class SortableHtmlReportWriter extends AbstractHtmlReportWriter {

    public static final String DEFAULT_OUTPUT_FILE = 'CodeNarcSortableReport.html'
    private static final String JS_FILE = 'js/sort-table.js'

    private class ViolationAndPath {
        Violation violation
        String path
    }

    String defaultOutputFile = DEFAULT_OUTPUT_FILE

    @Override
    String toString() {
        "SortableHtmlReportWriter[outputFile=$outputFile, title=$title]"
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    @Override
    protected Closure buildScript() {
        return {
            def jsInputStream = ClassPathResource.getInputStream(JS_FILE)
            assert jsInputStream, "JS File [$JS_FILE] not found"
            def js = jsInputStream.text
            script {
                unescaped << js
            }
        }
    }

    @Override
    protected Closure buildBodySection(AnalysisContext analysisContext, Results results) {
        return {
            body {
                out << buildLogo()
                h1(getResourceBundleString('htmlReport.titlePrefix'))
                out << buildReportMetadata()
                out << buildSummary(results)

                out << buildAllViolationsSection(results)
                if (includeRuleDescriptions) {
                    out << buildRuleDescriptions(analysisContext)
                }
            }
        }
    }

    private Closure buildSummary(Results results) {
        return {
            div(class: 'summary') {
                h2(getResourceBundleString('htmlReport.summary.title'))
                table {
                    thead {
                        tr(class: 'tableHeader') {
                            th(getResourceBundleString('htmlReport.summary.totalFilesHeading'))
                            th(getResourceBundleString('htmlReport.summary.filesWithViolationsHeading'))
                            (1..maxPriority).each { p ->
                                th(getResourceBundleString("htmlReport.summary.priority${p}Heading"))
                            }
                        }
                    }
                    tbody {
                        tr {
                            td(results.getTotalNumberOfFiles(true), class:'number')
                            td(results.getNumberOfFilesWithViolations(maxPriority, true) ?: '-', class:'number')
                            (1..maxPriority).each { p ->
                                td(results.getNumberOfViolationsWithPriority(p, true) ?: '-', class:'priority' + p)
                            }
                        }
                    }
                }
            }
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

    private Closure buildAllViolationsSection(Results results) {
        return {
            h2(getResourceBundleString('htmlReport.violations.title'))
            out << buildButtons()
            table(id:'violationsTable', border:'1') {
                thead {
                    tr(class:'tableHeader') {
                        th(getResourceBundleString('htmlReport.violations.file'))
                        th(getResourceBundleString('htmlReport.violations.ruleName'))
                        th(getResourceBundleString('htmlReport.violations.priority'))
                        th(getResourceBundleString('htmlReport.violations.lineNumber'))
                        th(getResourceBundleString('htmlReport.violations.sourceLine'))
                    }
                }

                def fileResults = getFileResults(results)
                def vpList = []
                fileResults.each { fileResult ->
                    fileResult.getViolations().each { v ->
                        if (v.rule.priority <= maxPriority) {
                            vpList << new ViolationAndPath(violation: v, path: fileResult.path)
                        }
                        return vpList
                    }
                }
                vpList.sort { vp -> vp.violation.rule.priority }

                tbody {
                    vpList.each { ViolationAndPath vp ->
                        Violation violation = vp.violation
                        def moreInfo = violation.message ? violation.message : ''
                        tr {
                            td(class:'pathColumn', vp.path)
                            td(class:'ruleColumn') {
                                a(violation.rule.name, href: "#${violation.rule.name}")
                            }
                            td(class: "priority${violation.rule.priority} priorityColumn", violation.rule.priority)
                            td(violation.lineNumber, class: 'number')
                            td {
                                if (violation.sourceLine) {
                                    def formattedSourceLine = formatSourceLine(violation.sourceLine)
                                    p(class: 'violationInfo') {
                                        span('[SRC]', class: 'violationInfoPrefix')
                                        span(formattedSourceLine, class: 'sourceCode')
                                    }
                                }
                                if (moreInfo) {
                                    p(class: 'violationInfo') {
                                        span('[MSG]', class: 'violationInfoPrefix')
                                        span(moreInfo, class: 'violationMessage')
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Closure buildButtons() {
        return {
            div(class:'buttons') {
                button(type:'button', onclick:'sortData(sortByRuleName)', getResourceBundleString('htmlReport.button.sortByRuleName'))
                button(type:'button', onclick:'sortData(sortByPriority)', getResourceBundleString('htmlReport.button.sortByPriority'))
                button(type:'button', onclick:'sortData(sortByRule)', getResourceBundleString('htmlReport.button.sortByRule'))
                button(type:'button', onclick:'sortData(sortByFile)', getResourceBundleString('htmlReport.button.sortByFile'))
            }
        }
    }
}
