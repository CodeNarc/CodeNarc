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

import groovy.xml.StreamingMarkupBuilder
import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import org.codenarc.rule.Rule
import org.codenarc.rule.Violation
import org.codenarc.util.io.ClassPathResource

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
 * Set the maxPriority property to control the maximum priority level for violations in
 * the report. For instance, setting maxPriority to 2 will result in the report containing
 * only priority 1 and 2 violations (and omitting violations with priority 3). The
 * maxPriority property defaults to 3.
 *
 * @author Chris Mair
 */
@SuppressWarnings(['DuplicateMapLiteral', 'UnnecessaryReturnKeyword', 'FactoryMethodName'])
class HtmlReportWriter extends AbstractReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'CodeNarcReport.html'
    private static final CSS_FILE = 'codenarc-htmlreport.css'
    private static final ROOT_PACKAGE_NAME = '<Root>'
    private static final MAX_SOURCE_LINE_LENGTH = 70
    private static final SOURCE_LINE_LAST_SEGMENT_LENGTH = 12

    String title
    String defaultOutputFile = DEFAULT_OUTPUT_FILE
    boolean includeSummaryByPackage = true
    int maxPriority = 3

    /**
     * Write out a report to the specified Writer for the analysis results
     * @param analysisContext - the AnalysisContext containing the analysis configuration information
     * @param results - the analysis results
     */
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        def html = builder.bind {
            html {
                out << buildHeaderSection()
                out << buildBodySection(analysisContext, results)
            }
        }
        writer << html
    }

    String toString() {
        "HtmlReportWriter[outputFile=$outputFile, title=$title]"
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private buildCSS() {
        return {
            def cssInputStream = ClassPathResource.getInputStream(CSS_FILE)
            assert cssInputStream, "CSS File [$CSS_FILE] not found"
            def css = cssInputStream.text
            style(type: 'text/css') {
                unescaped << css
            }
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
                // TODO: copy the image or inline it in the css
                out << buildLogo()
                h1(getResourceBundleString('htmlReport.titlePrefix'))
                out << buildReportMetadata()
                out << buildSummaryByPackage(results)

                out << buildAllPackageSections(results)
                out << buildRuleDescriptions(analysisContext)
            }
        }
    }

    private buildReportMetadata() {
        return {
            div(class: 'metadata') {
                table {
                    tr {
                        td(class: 'em', getResourceBundleString('htmlReport.reportTitle.title'))
                        td title
                    }
                    tr {
                        td(class: 'em', getResourceBundleString('htmlReport.reportTimestamp.label'))
                        td getFormattedTimestamp()
                    }
                    tr {
                        td(class: 'em', getResourceBundleString('htmlReport.reportVersion.label'))
                        td { a("CodeNarc v${getCodeNarcVersion()}", href:CODENARC_URL) }
                    }
                }
            }
        }
    }

    private buildLogo() {
        return {
            img(class: 'logo', src: 'http://codenarc.sourceforge.net/images/codenarc-logo.png', alt: 'CodeNarc', align: 'right')
       }
    }

    private buildSummaryByPackage(results) {
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

    private buildAllPackageSections(results) {
        return {
            results.children.each { child ->
                out << buildPackageSection(child)
            }
        }
    }

    private buildPackageSection(results) {
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

    private buildFileSection(results) {
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

    private buildRuleDescriptions(AnalysisContext analysisContext) {
        def sortedRules = getSortedRules(analysisContext)

        return {
            div(class: 'summary') {
                h2(getResourceBundleString('htmlReport.ruleDescriptions.title'))
                table(border:'1') {
                    tr(class:'tableHeader') {
                        th('#', class:'ruleDescriptions')
                        th(getResourceBundleString('htmlReport.ruleDescriptions.ruleNameHeading'), class:'ruleDescriptions')
                        th(getResourceBundleString('htmlReport.ruleDescriptions.descriptionHeading'), class:'ruleDescriptions')
                    }

                    sortedRules.eachWithIndex { Rule rule, index ->
                        def ruleName = rule.name
                        def priority = rule.priority
                        tr(class:'ruleDescriptions') {
                            td {
                                a(name:ruleName) { }
                                span(index + 1,class:'ruleIndex')    
                            }
                            td(ruleName, class:"ruleName priority${priority}")
                            td { unescaped << getHtmlDescriptionForRule(rule) }
                        }
                    }
                }
            }
        }
    }

    /**
     * Format and trim the source line. If the whole line fits, then include the whole line (trimmed).
     * Otherwise, remove characters from the middle to truncate to the max length.
     * @param sourceLine - the source line to format
     * @param startColumn - the starting column index; used to truncate the line if it's too long; defaults to 0
     * @return the formatted and trimmed source line
     */
    protected String formatSourceLine(String sourceLine, int startColumn=0) {
        def source = sourceLine ? sourceLine.trim() : null
        if (source && source.size() > MAX_SOURCE_LINE_LENGTH) {
            source = startColumn ? sourceLine[startColumn..-1] : sourceLine.trim()
            def lengthOfFirstSegment = MAX_SOURCE_LINE_LENGTH - SOURCE_LINE_LAST_SEGMENT_LENGTH - 2
            def firstSegment = source[0..lengthOfFirstSegment - 1]
            def lastSegment = source[-SOURCE_LINE_LAST_SEGMENT_LENGTH..-1]
            source = firstSegment + '..' + lastSegment
        }
        return source
    }

    /**
     * Return true if the Results represents a directory that contains at least one file with one
     * or more violations.
     * @param results - the Results
     */
    protected boolean isDirectoryContainingFilesWithViolations(Results results) {
        return !results.isFile() && results.getNumberOfFilesWithViolations(maxPriority, false)
    }

    /**
     * Return true if the Results represents a directory that contains at least one file
     * @param results - the Results
     */
    protected boolean isDirectoryContainingFiles(Results results) {
        return !results.isFile() && results.getTotalNumberOfFiles(false)
    }

    private String buildTitle() {
        getResourceBundleString('htmlReport.titlePrefix')  + (title ? ": $title" : '')
    }

}
