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

/**
 * Parser for the BaselineXmlReportWriter report
 */
class BaselineXmlReportParser {

    Map<String,Collection<BaselineViolation>> parseBaselineXmlReport(InputStream inputStream) {
        assert inputStream
        inputStream.withStream { input ->
            String xml = input.text
            return parseBaselineXmlReport(xml)
        }
    }

    Map<String,Collection<BaselineViolation>> parseBaselineXmlReport(String xmlString) {
        assert xmlString
        def xmlSlurper = new XmlSlurper()
        def xml = xmlSlurper.parseText(xmlString)

        def resultsMap = [:]
        xml.File.each { file ->
            String path = file.@path
            List violations = []
            file.Violation.each { v ->
                String messageText = unescapeXml(v.Message.text())
                violations << new BaselineViolation(ruleName:v.@ruleName, message:messageText)
            }
            resultsMap[path] = violations
        }
        return resultsMap
    }

    protected String unescapeXml(String string) {
        def resultString = string
        if (string) {
            resultString = resultString.replaceAll('&quot;', '"')
            resultString = resultString.replaceAll('&amp;', '&')
            resultString = resultString.replaceAll('&apos;', "'")
            resultString = resultString.replaceAll('&lt;', '<')
            resultString = resultString.replaceAll('&gt;', '>')
        }
        return resultString
    }
}
