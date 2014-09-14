/*
 * Copyright 2010 the original author or authors.
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

/**
 * ReportWriter that generates an simple ASCII text report.
 *
 * Set the maxPriority property to control the maximum priority level for violations in
 * the report. For instance, setting maxPriority to 2 will result in the report containing
 * only priority 1 and 2 violations (and omitting violations with priority 3). The
 * maxPriority property defaults to 3.
 *
 * @author Chris Mair
 */
class TextReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcReport.txt'
    int maxPriority = 3

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        initializeResourceBundle()
        def printWriter = new PrintWriter(writer)
        writeTitle(printWriter)
        writeSummary(printWriter, results)
        writePackageViolations(printWriter, results)
        writeFooter(printWriter)
        printWriter.flush()
    }

    protected void writeTitle(Writer writer) {
        def titleString = 'CodeNarc Report' + (title ? ': ' + title : '') + ' - ' + getFormattedTimestamp()
        writer.println(titleString)
    }

    protected void writeSummary(Writer writer, Results results) {
        def summary = "Summary: TotalFiles=${results.totalNumberOfFiles} " +
            "FilesWithViolations=${results.getNumberOfFilesWithViolations(maxPriority)}"
        (1..maxPriority).each { p ->
            summary += " P$p=${results.getNumberOfViolationsWithPriority(p)}"
        }
        writer.println()
        writer.println(summary)
    }

    protected void writePackageViolations(Writer writer, Results results) {
        results.children.each { child ->
            if (child.isFile()) {
                writeFileViolations(writer, child)
            }
            else {
                writePackageViolations(writer, child)
            }
        }
    }

    protected void writeFileViolations(Writer writer, FileResults results) {
        if (results.violations.find { v -> v.rule.priority <= maxPriority }) {
            writer.println()
            writer.println('File: ' + results.path)
            def violations = results.violations.findAll { v -> v.rule.priority <= maxPriority }
            violations.sort { violation -> violation.rule.priority }.each { violation ->
                writeViolation(writer, violation, results.path)
            }
        }
    }

    protected void writeViolation(Writer writer, Violation violation, String path) {
        def rule = violation.rule
        def locationString = getViolationLocationString(violation, path)
        def message = violation.message ? " Msg=[${violation.message}]" : ''
        def sourceLine = violation.sourceLine ? " Src=[${violation.sourceLine}]" : ''
        writer.println "    Violation: Rule=${rule.name} P=${rule.priority} ${locationString}$message$sourceLine"
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected String getViolationLocationString(Violation violation, String path) {
        return "Line=${violation.lineNumber}"
    }

    protected void writeFooter(Writer writer) {
        writer.println()
        writer.println("[CodeNarc ($CODENARC_URL) v" + getCodeNarcVersion() + ']')
    }
}
