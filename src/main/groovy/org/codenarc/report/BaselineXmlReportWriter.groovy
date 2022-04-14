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
import groovy.xml.XmlUtil
import org.codenarc.AnalysisContext
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.Violation

/**
 * ReportWriter that generates a baseline XML report.
 *
 * @author Chris Mair
 */
class BaselineXmlReportWriter extends AbstractReportWriter {

    String title
    String defaultOutputFile = 'CodeNarcBaselineViolations.xml'

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        assert writer
        assert analysisContext
        assert results

        initializeResourceBundle()
        def builder = new StreamingMarkupBuilder()
        builder.encoding = 'UTF-8'

        def xml = builder.bind {
            mkp.xmlDeclaration()
            CodeNarc(url:CODENARC_URL, version:getCodeNarcVersion()) {
                out << buildReportElement()
                out << buildProjectElement(analysisContext)
                out << buildFileElements(results)
            }
        }
        XmlUtil.serialize(xml, writer)
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    protected Closure buildReportElement() {
        return {
            Report(type:'baseline')
        }
    }

    protected Closure buildProjectElement(AnalysisContext analysisContext) {
        return {
            Project(title:title) {
                analysisContext.sourceDirectories.sort().each { sourceDirectory ->
                    SourceDirectory(sourceDirectory)
                }
            }
        }
    }

    protected Closure buildFileElements(Results results) {
        return buildFileElement(results)
    }

    protected Closure buildFileElement(Results results) {
        List children = results.children.sort { Results a, Results b ->
            int diffIsFile = a.isFile() <=> b.isFile()
            if (diffIsFile != 0) {
                return -diffIsFile // negate in order to get files first!
            }

            return a.path <=> b.path
        }

        return {
            children.each { child ->
                out << buildFileElement(child)
            }
        }
    }

    protected Closure buildFileElement(FileResults results) {
        return {
            File(path: results.path) {
                results.violations.each { violation ->
                    out << buildViolationElement(violation)
                }
            }
        }
    }

    protected Closure buildViolationElement(Violation violation) {
        def rule = violation.rule
        return {
            Violation(ruleName:rule.name) {
                out << buildMessageElement(violation)
            }
        }
    }

    protected Closure buildMessageElement(Violation violation) {
        return (violation.message) ? { Message(XmlReportUtil.cdata(XmlReportUtil.removeIllegalCharacters(violation.message))) } : null
    }

}
