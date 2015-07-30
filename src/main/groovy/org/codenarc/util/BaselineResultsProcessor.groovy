/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.util

import org.apache.log4j.Logger
import org.codenarc.ResultsProcessor
import org.codenarc.report.BaselineViolation
import org.codenarc.report.BaselineXmlReportParser
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.util.io.Resource

/**
 * Implementation of ResultsProcessor that removes matching violations specified in a Baseline report.
 * The Baseline violations are read from a Baseline Report from the provided Resource.
 */
class BaselineResultsProcessor implements ResultsProcessor {

    private static final LOG = Logger.getLogger(BaselineResultsProcessor)

    final Resource resource
    private final BaselineXmlReportParser parser = new BaselineXmlReportParser()
    protected int numViolationsRemoved = 0

    BaselineResultsProcessor(Resource resource) {
        this.resource = resource
    }

    @Override
    void processResults(Results results) {
        assert results

        InputStream inputStream = resource.getInputStream()
        Map<String,Collection<BaselineViolation>> baselineViolationsMap = parser.parseBaselineXmlReport(inputStream)
        Map<String,List<Violation>> violationsByFile = buildViolationsByFile(results)

        // For each file
        violationsByFile.each { path, violations ->
            def baselineViolations = baselineViolationsMap[path]
            baselineViolations.each { baselineViolation -> removeMatchingViolation(results, violations, baselineViolation) }
        }

        LOG.info("Ignored $numViolationsRemoved baseline violations")
    }

    private void removeMatchingViolation(Results results, List<Violation> violations, BaselineViolation baselineViolation) {
        def matchingViolation = violations.find { v ->
            v.rule.name == baselineViolation.ruleName && sameMessage(v.message, baselineViolation.message)
        }
        if (matchingViolation) {
            numViolationsRemoved++
            results.removeViolation(matchingViolation)
            violations.remove(matchingViolation)
        }
    }

    private boolean sameMessage(String m1, String m2) {
        return m1 == m2 || (!m1 && !m2)
    }

    private Map<String,List<Violation>> buildViolationsByFile(Results results) {
        Map<String,List<Violation>> violationsByFile = [:]
        addViolationsByFile(violationsByFile, results)
        return violationsByFile
    }

    private void addViolationsByFile(Map<String,List<Violation>> map, Results results) {
        if (results.isFile()) {
            map[results.path] = results.violations
        }
        else {
            results.children.each { child ->
                addViolationsByFile(map, child)
            }
        }
    }
}
