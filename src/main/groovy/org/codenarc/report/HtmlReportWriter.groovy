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
import org.codenarc.results.Results
import org.codenarc.rule.Violation

/**
 * ReportWriter that generates an HTML report.
 * <p/>
 * The default localized messages, including rule descriptions, are read from the "codenarc-base-messages"
 * ResourceBundle. You can override these messages using the normal ResourceBundle mechanisms (i.e.
 * creating a locale-specific resource bundle file on the classpath, such as "codenarc-base-messages_de").
 * You can optionally add rule descriptions for custom rules by placing them within a "codenarc-messages.properties"
 * file on the classpath, with entries of the form: {rule-name}.description=..."
 * <p/>
 * Set the includeSummaryByPackage property to false to exclude the violation summary for each package
 * within the "Summary" section of the report. It defaults to true.
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
class HtmlReportWriter extends AbstractHtmlReportWriter {

    public static final String DEFAULT_OUTPUT_FILE = 'CodeNarcReport.html'
    private static final String ROOT_PACKAGE_NAME = '<Root>'

    String defaultOutputFile = DEFAULT_OUTPUT_FILE
    boolean includeSummaryByPackage = true

    @Override
    String toString() {
        "HtmlReportWriter[outputFile=$outputFile, title=$title]"
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    @Override
    protected Closure buildBodySection(AnalysisContext analysisContext, Results results) {
        return {
            body {
                // TODO: copy the image or inline it in the css
                out << buildLogo()
                h1(getResourceBundleString('htmlReport.titlePrefix'))
                out << buildReportMetadata()
                out << buildSummaryByPackage(results)

                out << buildAllPackageSections(results)
                if (includeRuleDescriptions) {
                    out << buildRuleDescriptions(analysisContext)
                }
            }
        }
    }

    private Closure buildSummaryByPackage(Results results) {
        return {
            div(class: 'summary') {
                h2(getResourceBundleString('htmlReport.summary.title'))
                table {
                    tr(class:'tableHeader') {
                        th(getResourceBundleString('htmlReport.summary.packageHeading'))
                        th(getResourceBundleString('htmlReport.summary.totalFilesHeading'))
                        th(getResourceBundleString('htmlReport.summary.filesWithViolationsHeading'))
                        (1..maxPriority).each { p ->
                            th(getResourceBundleString("htmlReport.summary.priority${p}Heading"))
                        }
                    }
                    out << buildSummaryByPackageRow(results, true)
                    if (includeSummaryByPackage) {
                        out << buildAllSummaryByPackageRowsRecursively(results)
                    }
                }
            }
        }
    }

    private Closure buildAllSummaryByPackageRowsRecursively(Results results) {
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

    private Closure buildSummaryByPackageRow(Results results, boolean allPackages) {
        def recursive = allPackages
        return {
            tr {
                if (allPackages) {
                    td(getResourceBundleString('htmlReport.summary.allPackages'), class:'allPackages')
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
                td(results.getNumberOfFilesWithViolations(maxPriority, recursive) ?: '-', class:'number')
                (1..maxPriority).each { p ->
                    td(results.getNumberOfViolationsWithPriority(p, recursive) ?: '-', class:'priority' + p)
                }
            }
        }
    }

    private Closure buildAllPackageSections(Results results) {
        return {
            results.children.each { child ->
                out << buildPackageSection(child)
            }
        }
    }

    private Closure buildPackageSection(Results results) {
        return {
            def pathName = results.path ?: ROOT_PACKAGE_NAME
            if (isDirectoryContainingFilesWithViolations(results)) {
                div(class: 'summary') {
                    a(' ', name: pathName)
                    h2("Package: ${pathName.replaceAll('/', '.')}", class:'packageHeader')
                }
            }
            results.children.each { child ->
                if (child.isFile() && child.violations.find { v -> v.rule.priority <= maxPriority }) {
                    div(class: 'summary') {
                        h3(class:'fileHeader') {
                            mkp.yieldUnescaped '&#x27A5;&nbsp;' + (child.path - "$pathName/")
                        }

                        out << buildFileSection(child)
                    }
                }
                else {
                    out << buildPackageSection(child)
                }
            }
        }
    }

    private Closure buildFileSection(Results results) {
        assert results.isFile()
        return {
            table(border:'1') {
                tr(class:'tableHeader') {
                    th(getResourceBundleString('htmlReport.violations.ruleName'))
                    th(getResourceBundleString('htmlReport.violations.priority'))
                    th(getResourceBundleString('htmlReport.violations.lineNumber'))
                    th(getResourceBundleString('htmlReport.violations.sourceLine'))
                }

                def violations = results.violations.findAll { v -> v.rule.priority <= maxPriority }
//                def violations = results.violations
                violations.sort { v -> v.rule.priority }

                violations.each { Violation violation ->
                    def moreInfo = violation.message ? violation.message : ''
                    tr {
                        td {
                            a(violation.rule.name, href:"#${violation.rule.name}")
                        }
                        td(class: "priority${violation.rule.priority}", violation.rule.priority)
                        td(violation.lineNumber, class:'number')
                        td {
                            if (violation.sourceLine) {
                                def formattedSourceLine = formatSourceLine(violation.sourceLine)
                                p(class:'violationInfo') {
                                    span('[SRC]', class:'violationInfoPrefix')
                                    span(formattedSourceLine, class:'sourceCode')
                                }
                            }
                            if (moreInfo) {
                                p(class:'violationInfo') {
                                    span('[MSG]', class:'violationInfoPrefix')
                                    span(moreInfo, class:'violationMessage')
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
