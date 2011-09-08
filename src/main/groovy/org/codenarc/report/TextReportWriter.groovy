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
import org.codenarc.results.Results
import org.codenarc.results.FileResults
import org.codenarc.rule.Violation

/**
 * ReportWriter that generates an simple ASCII text report.
 *
 * @author Chris Mair
 */
class TextReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcReport.txt'

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

    private void writeTitle(Writer writer) {
        def titleString = 'CodeNarc Report' + (title ? ': ' + title : '') + ' - ' + getFormattedTimestamp()
        writer.println(titleString)
    }

    private void writeSummary(Writer writer, Results results) {
        def summary = "Summary: TotalFiles=${results.totalNumberOfFiles} " +
            "FilesWithViolations=${results.numberOfFilesWithViolations} " +
            "P1=${results.getNumberOfViolationsWithPriority(1)} " +
            "P2=${results.getNumberOfViolationsWithPriority(2)} " +
            "P3=${results.getNumberOfViolationsWithPriority(3)}"
        writer.println()
        writer.println(summary)
    }

    private void writePackageViolations(Writer writer, Results results) {
        results.children.each { child ->
            if (child.isFile()) {
                writeFileViolations(writer, child)
            }
            else {
                writePackageViolations(writer, child)
            }
        }
    }

    private void writeFileViolations(Writer writer, FileResults results) {
        if (results.violations) {
            writer.println()
            writer.println('File: ' + results.path)
            results.violations.sort { violation -> violation.rule.priority }.each { violation ->
                writeViolation(writer, violation)
            }
        }
    }

    private void writeViolation(Writer writer, Violation violation) {
        def rule = violation.rule
        def message = violation.message ? " Msg=[${violation.message}]" : ''
        def sourceLine = violation.sourceLine ? " Src=[${violation.sourceLine}]" : ''
        writer.println "    Violation: Rule=${rule.name} P=${rule.priority} Line=${violation.lineNumber}$message$sourceLine"
    }

    private void writeFooter(Writer writer) {
        writer.println()
        writer.println("[CodeNarc ($CODENARC_URL) v" + getCodeNarcVersion() + ']')
    }
}