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
import org.apache.log4j.Logger
import org.codenarc.AnalysisContext
import org.codenarc.results.Results

/**
 * ReportWriter that generates an HTML report.
 * <p/>
 * The default localized messages, including rule descriptions, are read from the "codenarc-base-messages"
 * ResourceBundle. You can override these messages using the normal ResourceBundle mechanisms (i.e.
 * creating a locale-specific resource bundle file on the classpath, such as "codenarc-base-messages_de").
 * You can optionally add rule descriptions for custom rules by placing them within a "codenarc-messages.properties"
 * file on the classpath, with entries of the form: {rule-name}.description=..."
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class HtmlReportWriter implements ReportWriter {

    public static final DEFAULT_OUTPUT_FILE = 'CodeNarcReport.html'
    static final CSS_FILE = 'codenarc-htmlreport.css'
    static final BASE_MESSSAGES_BUNDLE = "codenarc-base-messages"
    static final CUSTOM_MESSSAGES_BUNDLE = "codenarc-messages"
    static final ROOT_PACKAGE_NAME = '<Root>'
    static final MAX_SOURCE_LINE_LENGTH = 70
    static final SOURCE_LINE_LAST_SEGMENT_LENGTH = 12
    static final LOG = Logger.getLogger(HtmlReportWriter)

    String title
    String outputFile = DEFAULT_OUTPUT_FILE
    protected customMessagesBundleName = CUSTOM_MESSSAGES_BUNDLE

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

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private buildCSS() {
        return {
            def cssInputStream = getClass().getClassLoader().getResourceAsStream(CSS_FILE)
            assert cssInputStream, "CSS File [$CSS_FILE] not found"
            def css = cssInputStream.text
            unescaped << css
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
            def dateFormat = java.text.DateFormat.getDateTimeInstance()
            def timestamp = dateFormat.format(new Date())
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
                    th('Rule Name')
                    th('Priority')
                    th('Line #')
                    th('Source Line / Message')
                }

                def violations = results.getViolationsWithPriority(1) +
                    results.getViolationsWithPriority(2) +
                    results.getViolationsWithPriority(3)

                violations.each { violation ->
                    def priorityCssClass = "priority${violation.rule.priority}"
                    def moreInfo = violation.message ? violation.message : ""
                    tr {
                        td(class:priorityCssClass) {
                            a(violation.rule.name, href:"#${violation.rule.name}")
                        }
                        td(violation.rule.priority, class:priorityCssClass)
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
                                    span(moreInfo, class:'moreInfo')
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    protected ResourceBundle getMessagesBundle() {
        def baseBundle = ResourceBundle.getBundle(BASE_MESSSAGES_BUNDLE);
        def bundle = baseBundle
        try {
            bundle = ResourceBundle.getBundle(customMessagesBundleName);
            LOG.info("Using custom message bundle [$customMessagesBundleName]")
            bundle.setParent(baseBundle)
        }
        catch(MissingResourceException) {
            LOG.info("No custom message bundle found for [$customMessagesBundleName]. Using default messages.")
        }
        return bundle
    }

    private buildRuleDescriptions(AnalysisContext analysisContext) {
        def bundle = getMessagesBundle();
        def ruleNames = analysisContext.ruleSet.rules.collect { rule -> rule.name }
        def sortedRuleNames = ruleNames.sort()

        return {
            h2("Rule Descriptions")
            table(border:'1') {
                tr(class:'tableHeader') {
                    th('Rule Name')
                    th('Description')
                }

                sortedRuleNames.each { ruleName ->
                    tr {
                        a(name:ruleName)
                        td(ruleName, class:'ruleName')
                        td { unescaped << getDescriptionForRuleName(bundle, ruleName) }
                    }
                }
            }
        }
    }

    protected String getDescriptionForRuleName(bundle, String ruleName) {
        def resourceKey = ruleName + '.description'
        def description = "No description provided for rule named [$ruleName]"
        try {
            description = bundle.getString(resourceKey)
        } catch (MissingResourceException e) {
            LOG.warn(description + " resourceKey=[$resourceKey]")
        }
        return description
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
            def firstSegment = source[0..lengthOfFirstSegment-1]
            def lastSegment = source[-SOURCE_LINE_LAST_SEGMENT_LENGTH..-1]
            source = firstSegment + '..' + lastSegment
        }
        return source
    }

    private buildVersionFooter() {
        def versionText = getClass().getClassLoader().getResourceAsStream('codenarc-version.txt').text
        return {
            p(class:'version') {
                a(versionText, href:"http://www.codenarc.org")
            }
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