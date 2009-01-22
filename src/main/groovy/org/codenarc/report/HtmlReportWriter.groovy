/*
 * Copyright 2008 the original author or authors.
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

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import org.apache.log4j.Logger

/**
 * ReportWriter that generates an HTML report
 *
 * @author Chris Mair
 * @version $Revision: 213 $ - $Date: 2009-01-19 13:45:17 -0500 (Mon, 19 Jan 2009) $
 */
class HtmlReportWriter implements ReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'CodeNarcReport.html'
    static final ROOT_PACKAGE_NAME = '<Root>'
    static final LOG = Logger.getLogger(HtmlReportWriter)

    String title
    String outputFile = DEFAULT_OUTPUT_FILE

    /**
     * Write out a report for the specified analysis results
     * @param analysisContext - the AnalysisContext containing the analysis configuration information
     * @param results - the analysis results
     */
    void writeOutReport(AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        def builder = new StreamingMarkupBuilder()
        def reportFile = new File(outputFile)
        reportFile.withWriter { writer ->
            def html = builder.bind() {
                html {
                    out << buildHeaderSection()
                    out << buildBodySection(analysisContext, results)
                }
            }
            writer << html
        }
    }

    private buildCSS() {
        return {
            def CSS = getClass().getClassLoader().getResourceAsStream('codenarc.css').text
            unescaped << CSS
        }
    }

    private buildHeaderSection() {
        return {
            head {
                title(buildTitle())
                out << buildCSS()
            }
        }
    }

    private buildBodySection(AnalysisContext analysisContext, results) {
        return {
            body {
                h1(buildTitle())
                out << buildReportTimestamp()
                out << buildSourceDirectories(analysisContext)
                out << buildSummaryByPackage(results)
                out << buildAllPackageSections(results)
                out << buildRuleDescriptions(analysisContext)
                out << buildVersionFooter()
            }

        }
    }

    private buildReportTimestamp() {
        return {
            def timestamp = new SimpleDateFormat('MM/dd/yyyy hh:mmaa').format(new Date())
            p("Report timestamp: $timestamp", class:'reportInfo')
        }
    }

    private buildSourceDirectories(AnalysisContext analysisContext) {
        return {
            if (analysisContext.sourceDirectories) {
                p("Source Directories:", class:'reportInfo')
                ul {
                    analysisContext.sourceDirectories.each { sourceDir ->
                        li(sourceDir)
                    }
                }
            }
        }
    }

    private buildSummaryByPackage(results) {
        return {
            h2("Summary by Package")
            table() {
                tr(class:'tableHeader') {
                    th('Package')
                    th('Total Files')
                    th('Files with Violations')
                    th('Priority 1')
                    th('Priority 2')
                    th('Priority 3')
                }

                out << buildSummaryByPackageRow(results, true)
                out << buildAllSummaryByPackageRowsRecursively(results)
            }
        }
    }

    private buildAllSummaryByPackageRowsRecursively(results) {
        return {
            results.children.each { child ->
                if (isDirectoryContainingFiles(child)) {
                    out << buildSummaryByPackageRow(child, false)
                }
                if (!child.isFile()) {
                    out << buildAllSummaryByPackageRowsRecursively(child)
                }
            }
        }
    }

    private buildSummaryByPackageRow(results, boolean allPackages) {
        def recursive = allPackages
        return {
            tr {
                if (allPackages) {
                    td('All Packages', class:'allPackages')
                }
                else {
                    def pathName = results.path ?: ROOT_PACKAGE_NAME
                    if (isDirectoryContainingFilesWithViolations(results)) {
                        td {
                            a(pathName, href:"#${pathName}")
                        }
                    }
                    else {
                        td(pathName)
                    }
                }
                td(results.getTotalNumberOfFiles(recursive), class:'number')
                td(results.getNumberOfFilesWithViolations(recursive), class:'number')
                td(results.getNumberOfViolationsWithPriority(1, recursive), class:'priority1')
                td(results.getNumberOfViolationsWithPriority(2, recursive), class:'priority2')
                td(results.getNumberOfViolationsWithPriority(3, recursive), class:'priority3')
            }
        }
    }

    private buildAllPackageSections(results) {
        return {
            results.children.each { child ->
                out << buildPackageSection(child)
            }
        }
    }

    private buildPackageSection(results) {
        return {
            if (isDirectoryContainingFilesWithViolations(results)) {
                def pathName = results.path ?: ROOT_PACKAGE_NAME
                a(name:pathName)
                h2(pathName, class:'packageHeader')
            }
            results.children.each { child ->
                if (child.isFile()) {
                    h3(child.path, class:'fileHeader')
                    out << buildFileSection(child)
                }
                else {
                    out << buildPackageSection(child)
                }
            }
        }
    }

    private buildFileSection(results) {
        assert results.isFile()
        return {
            table(border:'1') {
                tr(class:'tableHeader') {
                    th('Rule ID')
                    th('Priority')
                    th('Line #')
                    th('Source Line or [More Info]')
                }

                def violations = results.getViolationsWithPriority(1) +
                    results.getViolationsWithPriority(2) +
                    results.getViolationsWithPriority(3)

                violations.each { violation ->
                    def priorityCssClass = "priority${violation.rule.priority}"
                    def moreInfo = violation.description ? "[${violation.description}]" : ""
                    tr {
                        td(violation.rule.id, class:priorityCssClass)
                        td(violation.rule.priority, class:priorityCssClass)
                        td(violation.lineNumber, class:'number')
                        td {
                            span(violation.sourceLine, class:'sourceCode')
                            span(moreInfo, class:'moreInfo')
                        }

                    }
                }
            }
        }
    }

    private buildRuleDescriptions(AnalysisContext analysisContext) {
        def bundle = ResourceBundle.getBundle("html-report-messages");
        def ruleIds = analysisContext.ruleSet.rules.collect { rule -> rule.id }
        def sortedRuleIds = ruleIds.sort()

        return {
            h2("Rule Descriptions")
            table(border:'1') {
                tr(class:'tableHeader') {
                    th('Rule ID')
                    th('Description')
                }

                sortedRuleIds.each { ruleId ->
                    tr {
                        td(ruleId, class:'ruleID')
                        td { unescaped << getDescriptionForRuleId(bundle, ruleId) }
                    }
                }
            }
        }
    }

    protected String getDescriptionForRuleId(bundle, String ruleId) {
        def resourceKey = ruleId + '.description'
        def description = "No description provided for rule id [$ruleId]"
        try {
            description = bundle.getString(resourceKey)
        } catch (MissingResourceException e) {
            LOG.warn(description + " resourceKey=[$resourceKey]")
        }
        return description
    }

    private buildVersionFooter() {
        def versionText = getClass().getClassLoader().getResourceAsStream('codenarc-version.txt').text
        return {
            p(versionText, class:'version')
        }
    }

    /**
     * Return true if the Results represents a directory that contains at least one file with one
     * or more violations.
     * @param results - the Results
     */
    protected boolean isDirectoryContainingFilesWithViolations(Results results) {
        return !results.isFile() && results.getNumberOfFilesWithViolations(false)
    }

    /**
     * Return true if the Results represents a directory that contains at least one file
     * @param results - the Results
     */
    protected boolean isDirectoryContainingFiles(Results results) {
        return !results.isFile() && results.getTotalNumberOfFiles(false)
    }

    private String buildTitle() {
        return "CodeNarc Report" + (title ? ": $title": '')
    }

}