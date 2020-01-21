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
package org.codenarc.report

import groovy.xml.StreamingMarkupBuilder
import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import org.codenarc.rule.Rule
import org.codenarc.util.io.ClassPathResource

/**
 * Abstract superclass for HTML ReportWriter classes.
 *
 * @author Chris Mair
 */
@SuppressWarnings('DuplicateMapLiteral')
abstract class AbstractHtmlReportWriter extends AbstractReportWriter {

    protected static final int MAX_SOURCE_LINE_LENGTH = 70
    protected static final int SOURCE_LINE_LAST_SEGMENT_LENGTH = 12
    protected static final String CSS_FILE = 'codenarc-htmlreport.css'
    protected static final String LOGO_FILE = 'http://codenarc.github.io/CodeNarc/images/codenarc-logo.png'

    String title
    boolean includeRuleDescriptions = true
    int maxPriority = 3

    protected String getCssFile() {
        return CSS_FILE
    }

    abstract protected Closure buildBodySection(AnalysisContext analysisContext, Results results)

    /**
     * Write out a report to the specified Writer for the analysis results
     * @param analysisContext - the AnalysisContext containing the analysis configuration information
     * @param results - the analysis results
     */
    @Override
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

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    protected Closure buildCSS() {
        return {
            def cssInputStream = ClassPathResource.getInputStream(getCssFile())
            assert cssInputStream, "CSS File [$getCssFile()] not found"
            def css = cssInputStream.text
            style(type: 'text/css') {
                unescaped << css
            }
        }
    }

    protected Closure buildScript() {
        return { }
    }

    protected Closure buildHeaderSection() {
        return {
            head {
                title(buildTitle())
                out << buildCSS()
                out << buildScript()
            }
        }
    }

    protected Closure buildReportMetadata() {
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

    protected Closure buildLogo() {
        return {
            img(class: 'logo', src: LOGO_FILE, alt: 'CodeNarc', align: 'right')
       }
    }

    protected Closure buildRuleDescriptions(AnalysisContext analysisContext) {
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

    protected String buildTitle() {
        getResourceBundleString('htmlReport.titlePrefix')  + (title ? ": $title" : '')
    }

}
