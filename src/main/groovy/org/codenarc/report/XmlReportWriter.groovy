/*
 * Copyright 2009 the original author or authors.
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
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Violation
import org.codenarc.util.PathUtil

/**
 * ReportWriter that generates an XML report.
 */
class XmlReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcXmlReport.xml'

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        def xml = builder.bind {
            mkp.xmlDeclaration()
            CodeNarc(url:CODENARC_URL, version:getCodeNarcVersion()) {
                out << buildReportElement()
                out << buildProjectElement(analysisContext)
                out << buildPackageElements(results)
                out << buildRulesElement(analysisContext)
            }
        }
        writer << xml
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    protected Closure buildReportElement() {
        return {
            Report(timestamp:getFormattedTimestamp())
        }
    }

    protected Closure buildProjectElement(AnalysisContext analysisContext) {
        return {
            Project(title:title) {
                analysisContext.sourceDirectories.each { sourceDirectory ->
                    SourceDirectory(sourceDirectory)
                }
            }
        }
    }

    protected Closure buildPackageElements(Results results) {
        return buildPackageElement(results)
    }

    protected Closure buildPackageElement(Results results) {
        def elementName = isRoot(results) ? 'PackageSummary' : 'Package'
        return {
            "$elementName"(buildPackageAttributeMap(results)) {
                results.children.each { child ->
                    if (child.isFile()) {
                        out << buildFileElement(child)
                    }
                }
            }
            results.children.each { child ->
                if (!child.isFile()) {
                    out << buildPackageElement(child)
                }
            }
        }
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

    protected Closure buildFileElement(FileResults results) {
        return {
            def name = PathUtil.getName(results.path)
            File(name: name) {
                results.violations.each { violation ->
                    out << buildViolationElement(violation)
                }
            }
        }
    }

    protected Closure buildViolationElement(Violation violation) {
        def rule = violation.rule
        return {
            Violation(ruleName:rule.name, priority:rule.priority, lineNumber:violation.lineNumber) {
                out << buildSourceLineElement(violation)
                out << buildMessageElement(violation)
            }
        }
    }

    protected Closure buildSourceLineElement(Violation violation) {
        return (violation.sourceLine) ? { SourceLine(XmlReportUtil.cdata(XmlReportUtil.removeIllegalCharacters(violation.sourceLine))) } : null
    }

    protected Closure buildMessageElement(Violation violation) {
        return (violation.message) ? { Message(XmlReportUtil.cdata(XmlReportUtil.removeIllegalCharacters(violation.message))) } : null
    }

    protected Closure buildRulesElement(AnalysisContext analysisContext) {
        def sortedRules = getSortedRules(analysisContext)
        return {
            Rules {
                sortedRules.each { rule ->
                    def description = this.getDescriptionForRule(rule)
                    Rule(name:rule.name) {
                        Description(XmlReportUtil.cdata(description))
                    }
                }
            }
        }
    }

}
