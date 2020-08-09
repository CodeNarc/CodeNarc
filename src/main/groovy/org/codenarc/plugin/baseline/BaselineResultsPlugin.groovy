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
package org.codenarc.plugin.baseline

import org.codenarc.plugin.AbstractCodeNarcPlugin
import org.codenarc.plugin.FileViolations
import org.codenarc.report.BaselineViolation
import org.codenarc.report.BaselineXmlReportParser
import org.codenarc.report.ReportWriter
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.util.io.Resource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * CodeNarc Plugin that removes matching violations specified in a Baseline report.
 * The Baseline violations are read from a Baseline Report from the provided Resource.
 */
class BaselineResultsPlugin extends AbstractCodeNarcPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(BaselineResultsPlugin)

    final Resource resource
    private final BaselineXmlReportParser parser = new BaselineXmlReportParser()
    protected Map<String,Collection<BaselineViolation>> baselineViolationsMap
    protected int numViolationsRemoved = 0

    BaselineResultsPlugin(Resource resource) {
        this.resource = resource
    }

    @Override
    void initialize() {
        InputStream inputStream = resource.getInputStream()
        baselineViolationsMap = parser.parseBaselineXmlReport(inputStream)
    }

    @Override
    void processViolationsForFile(FileViolations fileViolations) {
        assert fileViolations
        def baselineViolations = baselineViolationsMap[fileViolations.path]
        baselineViolations.each { baselineViolation -> removeMatchingViolation(fileViolations.violations, baselineViolation) }
    }

    @Override
    void processReports(List<ReportWriter> reportWriters) {
        // Not technically a report, but want this to happen after all violations are processed. Better place for this?
        LOG.info("Ignored $numViolationsRemoved baseline violations")
    }

    private void removeMatchingViolation(List<Violation> violations, BaselineViolation baselineViolation) {
        def matchingViolation = violations.find { v ->
            v.rule.name == baselineViolation.ruleName && sameMessage(v.message, baselineViolation.message)
        }
        if (matchingViolation) {
            numViolationsRemoved++
            violations.remove(matchingViolation)
        }
    }

    private boolean sameMessage(String m1, String m2) {
        return (!m1 && !m2) || scrub(m1) == scrub(m2)
    }

    private String scrub(String str) {
        // The \r character, specifically, was causing comparisons to fail. See #303.
        // In Java 11 (as opposed to 8 and 14) there are leading and trailing empty characters. See #471.
        return str?.replaceAll(/\R/, '')?.trim()
    }

    private void addFilesWithViolations(List<FileResults> map, Results results) {
        if (results.isFile()) {
            map << results
        }
        else {
            results.children.each { child ->
                addFilesWithViolations(map, child)
            }
        }
    }
}
