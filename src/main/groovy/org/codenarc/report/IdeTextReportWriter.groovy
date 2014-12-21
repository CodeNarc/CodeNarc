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

import org.codenarc.rule.Violation
import org.codenarc.util.PathUtil

/**
 * ReportWriter that generates an simple ASCII text report, and includes IDE-compatible
 * (Eclipse, Idea) hyperlinks to source code for violations.
 *
 * Set the maxPriority property to control the maximum priority level for violations in
 * the report. For instance, setting maxPriority to 2 will result in the report containing
 * only priority 1 and 2 violations (and omitting violations with priority 3). The
 * maxPriority property defaults to 3.
 *
 * @author Chris Mair
 */
class IdeTextReportWriter extends TextReportWriter {

    @Override
    protected String getViolationLocationString(Violation violation, String path) {
        def fileName = PathUtil.getName(path)
        return "Loc=.($fileName:${violation.lineNumber})"
    }

}
