/*
 * Copyright 2021 the original author or authors.
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

import org.codenarc.results.FileResults
import org.codenarc.results.Results

/**
 * ReportWriter that generates an simple ASCII text report, with one line per violation.
 *
 * Set the maxPriority property to control the maximum priority level for violations in
 * the report. For instance, setting maxPriority to 2 will result in the report containing
 * only priority 1 and 2 violations (and omitting violations with priority 3). The
 * maxPriority property defaults to 3.
 *
 * @author Luis Zimmermann
 * @author Chris Mair
 */
class CompactTextReportWriter extends TextReportWriter {

    @Override
    protected void writeTitle(Writer writer) {
        // do nothing; no title
    }

    @Override
    protected void writeSummary(Writer writer, Results results) {
        // do nothing; no summary
    }

    @Override
    protected void writeFooter(Writer writer) {
        // do nothing; no footer
    }

    protected void writeFileViolations(Writer writer, FileResults results) {
        if (results.violations.find { v -> v.rule.priority <= maxPriority }) {
            String file = results.path
            def violations = results.violations.findAll { v -> v.rule.priority <= maxPriority }
            violations.sort { violation -> violation.rule.priority }.each { violation ->
                writer.println "${file}:${violation.lineNumber}:${violation.rule.name} ${violation.message}"
            }
        }
    }
}
