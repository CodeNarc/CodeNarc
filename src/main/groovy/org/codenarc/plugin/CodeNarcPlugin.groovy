/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.plugin

import org.codenarc.report.ReportWriter
import org.codenarc.rule.Rule

/**
 * The interface defining the set of hooks for a CodeNarc Plugin
 */
interface CodeNarcPlugin {

    /**
     * Perform plugin initialization
     */
    void initialize()

    /**
     * Process the list of Rules. The List can be modified in-place. Rules within the list can be modified, or Rules
     * can be added or deleted from the list.
     *
     * @param rules - the initial List of Rules to be applied
     */
    void processRules(List<Rule> rules)

    /**
     * Process the violations for a single file. The List of violations within FileViolations can be modified in-place.
     *
     * @param fileViolations - the violations and associated metadata for a single file
     */
    void processViolationsForFile(FileViolations fileViolations)

    /**
     * Process the list of ReportWriter. The List can be modified in-place. ReportWriters within the list can be modified,
     * or ReportWriters can be added or deleted from the list.
     *
     * @param rules - the initial List of Rules to be applied
     */
    void processReports(List<ReportWriter> reportWriters)

}
