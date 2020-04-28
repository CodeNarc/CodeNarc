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
 * limitations under the License

 * @author Nicolas Vuillamy
 */
package org.codenarc.report

import org.codenarc.AnalysisContext
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.util.PathUtil

import groovy.json.JsonOutput

/**
 * ReportWriter that generates an JSON report.
 */
class JsonReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcJsonReport.json'

    Boolean writeAsSingleLine = false

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()

        // Build results object
        def resultsObj = [
            'codeNarc': [url: CODENARC_URL, version: getCodeNarcVersion()],
            'report': buildReportElement(),
            'project': buildProjectElement(analysisContext),
            'summary': buildSummaryElement(results),
            'packages': buildPackageElements(results),
            'rules': buildRulesElement(analysisContext)
        ]

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

    boolean isWriteAsSingleLine() {
        writeAsSingleLine == true
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    protected Map buildReportElement() {
        return [
            timestamp:getFormattedTimestamp()
        ]
    }

    protected Map buildProjectElement(AnalysisContext analysisContext) {
        return [
            title: title,
            sourceDirectories: analysisContext.sourceDirectories
        ]
    }

    protected Map buildSummaryElement(Results results) {
        if (isRoot(results)) {
            return buildPackageAttributeMap(results)
        }
    }

    protected List buildPackageElements(Results results) {
        def packages = []
        // Build package info & files results except if root (case taken in account by buildSummaryElement)
        if (!isRoot(results)) {
            Map packageItem = buildPackageAttributeMap(results)
            packageItem.files = []
            results.children.each { child ->
                if (child.isFile()) {
                    packageItem.files << buildFileElement(child)
                }
            }
            packages << packageItem
        }

        // Browse sub-packages & gather results
        results.children.each { child ->
            if (!child.isFile()) {
                packages += buildPackageElements(child)
            }
        }
        return packages
    }

    protected Map buildPackageAttributeMap(Results results) {
        def attributeMap = [
            totalFiles: results.getTotalNumberOfFiles(),
            filesWithViolations: results.getNumberOfFilesWithViolations(3),
            priority1:results.getNumberOfViolationsWithPriority(1),
            priority2:results.getNumberOfViolationsWithPriority(2),
            priority3:results.getNumberOfViolationsWithPriority(3)
        ]
        if (!isRoot(results)) {
            attributeMap = [path:results.path] + attributeMap
        }
        return attributeMap
    }

    protected boolean isRoot(Results results) {
        results.path == null
    }

    protected Map buildFileElement(FileResults results) {
        def violations = []
        results.violations.each { violation ->
            violations << buildViolationElement(violation)
        }
        return [
            name: PathUtil.getName(results.path),
            violations: violations
        ]
    }

    protected Map buildViolationElement(Violation violation) {
        def rule = violation.rule
        def violationElement =  [
            ruleName:rule.name,
            priority:rule.priority,
            lineNumber:violation.lineNumber
        ]
        if (violation.sourceLine) {
            violationElement.sourceLine = violation.sourceLine
        }
        if (violation.message) {
            violationElement.message = violation.message
        }
        return violationElement
    }

    protected Object[] buildRulesElement(AnalysisContext analysisContext) {
        def sortedRules = getSortedRules(analysisContext)
        def rules = []
        sortedRules.each { rule ->
            def description = this.getDescriptionForRule(rule)
            rules.add([
                        name:rule.name,
                        description: description
                    ])
        }
        return rules
    }

}
