/*
 * Copyright 2010 the original author or authors.
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
import org.codenarc.rule.Violation

/**
 * ReportWriter that generates an XML report with inline rule descriptions.
 * This makes it easy for Hudson to parse
 *
 * @author Robin Bramley
 */
@SuppressWarnings('UnnecessaryReturnKeyword')
class InlineXmlReportWriter extends XmlReportWriter {

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------
    @Override
    protected buildViolationElement(Violation violation) {
        def rule = violation.rule
        return {
            Violation(ruleName:rule.name, priority:rule.priority, lineNumber:violation.lineNumber) {
                out << buildSourceLineElement(violation)
                out << buildMessageElement(violation)
                out << buildDescriptionElement(rule) // put inline
            }
        }
    }

    @Override
    protected buildRulesElement(AnalysisContext analysisContext) {
        // No-op as we have inline rule descriptions
    }

    @SuppressWarnings('FactoryMethodName')
    private buildDescriptionElement(rule) {
        def description = this.getDescriptionForRule(rule)
        return { Description(cdata(description)) }
    }
}
