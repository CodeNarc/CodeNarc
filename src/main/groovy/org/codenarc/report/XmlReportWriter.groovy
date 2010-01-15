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

import org.codenarc.AnalysisContext
import org.codenarc.results.Results
import groovy.xml.StreamingMarkupBuilder
import org.codenarc.results.FileResults
import org.codenarc.rule.Violation
import org.codenarc.util.PathUtil

/**
 * ReportWriter that generates an XML report.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcXmlReport.xml'

    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert analysisContext
        assert results

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        writer.withWriter { w ->
            def xml = builder.bind {
                mkp.xmlDeclaration()
                CodeNarc(url:CODENARC_URL, version:getCodeNarcVersion()) {
                    out << buildReportElement()
                    out << buildProjectElement(analysisContext)
                    out << buildPackageElements(results)
                    out << buildRulesElement(analysisContext)
                }
            }
            w << xml
        }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private buildReportElement() {
        return {
            Report(timestamp:getFormattedTimestamp())
        }
    }

    private buildProjectElement(AnalysisContext analysisContext) {
        return {
            Project(title:title) {
                analysisContext.sourceDirectories.each { sourceDirectory ->
                    SourceDirectory(sourceDirectory)
                }
            }
        }
    }

    private buildPackageElements(results) {
        return buildPackageElement(results)
    }

    private buildPackageElement(results) {
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

    private Map buildPackageAttributeMap(results) {
        def attributeMap = [
            totalFiles: results.getTotalNumberOfFiles(),
            filesWithViolations: results.getNumberOfFilesWithViolations(),
            priority1:results.getNumberOfViolationsWithPriority(1),
            priority2:results.getNumberOfViolationsWithPriority(2),
            priority3:results.getNumberOfViolationsWithPriority(3)
        ]
        if (!isRoot(results)) {
            attributeMap = [path:results.path] + attributeMap
        }
        return attributeMap
    }

    private boolean isRoot(results) {
        results.path == null
    }

    private buildFileElement(FileResults results) {
        return {
            def name = PathUtil.getName(results.path)
            File(name: name) {
                results.violations.each { violation ->
                    out << buildViolationElement(violation)
                }
            }
        }
    }

    private buildViolationElement(Violation violation) {
        def rule = violation.rule
        return {
            Violation(ruleName:rule.name, priority:rule.priority, lineNumber:violation.lineNumber) {
                out << buildSourceLineElement(violation)
                out << buildMessageElement(violation)
            }
        }
    }

    private buildSourceLineElement(Violation violation) {
        return (violation.sourceLine) ? { SourceLine(cdata(violation.sourceLine)) } : null
    }

    private buildMessageElement(Violation violation) {
        return (violation.message) ? { Message(cdata(violation.message)) } : null
    }

    private buildRulesElement(AnalysisContext analysisContext) {
        def rules = analysisContext.ruleSet.rules
        def sortedRules = rules.toList().sort { rule -> rule.name }
        return {
            Rules() {
                sortedRules.each { rule ->
                    def description = this.getDescriptionForRule(rule)
                    Rule(name:rule.name) {
                        Description(cdata(description))
                    }
                }
            }
        }
    }

    private cdata(String text) {
        return { unescaped << "<![CDATA[" + text + "]]>" }
    }

}