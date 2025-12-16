/*
 * Copyright 2025 the original author or authors.
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

import static org.codenarc.test.TestUtil.*

import org.junit.jupiter.api.Test

/**
 * Tests for SarifReportWriter
 */
class SarifReportWriterTest extends AbstractJsonReportWriterTestCase<SarifReportWriter> {

    private static final NEW_REPORT_FILE = 'target/NewSarifReport.sarif.json'

    @SuppressWarnings('LineLength')
    private static final REPORT_JSON = """
        {
            "\$schema": "https://json.schemastore.org/sarif-2.1.0.json",
            "version": "2.1.0",
            "runs": [
                {
                    "tool": {
                        "driver": {
                            "name": "CodeNarc",
                            "version": "${VERSION}",
                            "informationUri": "https://codenarc.org",
                            "rules": [
                                { "id": "DuplicateImport", "name": "DuplicateImportRule", "shortDescription": { "text": "Custom: Duplicate imports" }, "helpUri": "https://codenarc.org/codenarc-rules-imports.html#duplicateimport-rule", "properties": { "priority": 3 } },
                                { "id": "Rule1", "name": "StubRule", "shortDescription": { "text": "No description provided for rule named [Rule1]" }, "helpUri": "https://codenarc.org/codenarc-rules-rule.html#rule1-rule", "properties": { "priority": 1 } },
                                { "id": "Rule2", "name": "StubRule", "shortDescription": { "text": "No description provided for rule named [Rule2]" }, "helpUri": "https://codenarc.org/codenarc-rules-rule.html#rule2-rule", "properties": { "priority": 3 } },
                                { "id": "Rule3", "name": "StubRule", "shortDescription": { "text": "No description provided for rule named [Rule3]" }, "helpUri": "https://codenarc.org/codenarc-rules-rule.html#rule3-rule", "properties": { "priority": 3 } },
                                { "id": "UnnecessaryBooleanInstantiation", "name": "UnnecessaryBooleanInstantiationRule", "shortDescription": { "text": "Use Boolean.valueOf() for variable values or Boolean.TRUE and Boolean.FALSE for constant values instead of calling the Boolean() constructor directly or calling Boolean.valueOf(true) or Boolean.valueOf(false)." }, "helpUri": "https://codenarc.org/codenarc-rules-unnecessary.html#unnecessarybooleaninstantiation-rule", "properties": { "priority": 3 } }
                            ]
                        }
                    },
                    "results": [
                        { "ruleId": "Rule1", "level": "error", "message": { "text": "No description provided for rule named [Rule1]" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/MyAction.groovy" }, "region": { "startLine": 111, "snippet": { "text": "if (count < 23 && index <= 99 && name.contains('\\\\u0000')) {" } } } } ] },
                        { "ruleId": "Rule3", "level": "note", "message": { "text": "Other info c:\\\\\\\\data" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/MyAction.groovy" }, "region": { "startLine": 333, "snippet": { "text": "throw new Exception(\\\"cdata=<![CDATA[whatever]]>\\\") // c:\\\\\\\\data - Some very long message 1234567890123456789012345678901234567890" } } } } ] },
                        { "ruleId": "Rule3", "level": "note", "message": { "text": "Other info c:\\\\\\\\data" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/MyAction.groovy" }, "region": { "startLine": 333, "snippet": { "text": "throw new Exception(\\\"cdata=<![CDATA[whatever]]>\\\") // c:\\\\\\\\data - Some very long message 1234567890123456789012345678901234567890" } } } } ] },
                        { "ruleId": "Rule1", "level": "error", "message": { "text": "No description provided for rule named [Rule1]" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/MyAction.groovy" }, "region": { "startLine": 111, "snippet": { "text": "if (count < 23 && index <= 99 && name.contains('\\\\u0000')) {" } } } } ] },
                        { "ruleId": "Rule2", "level": "note", "message": { "text": "bad stuff: !@#\$%^&*()_+<>" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/MyAction.groovy" }, "region": { "startLine": 222 } } } ] },
                        { "ruleId": "Rule3", "level": "note", "message": { "text": "Other info c:\\\\\\\\data" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/dao/MyDao.groovy" }, "region": { "startLine": 333, "snippet": { "text": "throw new Exception(\\\"cdata=<![CDATA[whatever]]>\\\") // c:\\\\\\\\data - Some very long message 1234567890123456789012345678901234567890" } } } } ] },
                        { "ruleId": "Rule2", "level": "note", "message": { "text": "bad stuff: !@#\$%^&*()_+<>" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/dao/MyOtherDao.groovy" }, "region": { "startLine": 222 } } } ] },
                        { "ruleId": "Rule1", "level": "error", "message": { "text": "No description provided for rule named [Rule1]" }, "locations": [ { "physicalLocation": { "artifactLocation": { "uri": "src/main/dao/MyOtherDao.groovy" }, "region": { "startLine": 111, "snippet": { "text": "if (count < 23 && index <= 99 && name.contains('\\\\u0000')) {" } } } } ] }
                    ]
                }
            ]
        }
    """

    @Test
    void testWriteReport_Writer() {
        reportWriter.writeReport(stringWriter, analysisContext, results)
        def jsonAsString = stringWriter.toString()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToStandardOut() {
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(analysisContext, results)
        }
        assert output != null && output != ''
        assertJson(output, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToDefaultReportFile() {
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File('CodeNarcSarifReport.sarif.json')
        def jsonAsString = reportFile.text
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_WritesToConfiguredReportFile() {
        reportWriter.outputFile = NEW_REPORT_FILE
        reportWriter.writeReport(analysisContext, results)
        def reportFile = new File(NEW_REPORT_FILE)
        def jsonAsString = reportFile.text
        reportFile.delete()
        assertJson(jsonAsString, REPORT_JSON)
    }

    @Test
    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    @Test
    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    @Test
    void testDefaultOutputFile_SarifReport() {
        assert reportWriter.defaultOutputFile == 'CodeNarcSarifReport.sarif.json'
    }

    @Override
    protected SarifReportWriter createReportWriter() {
        return new SarifReportWriter()
    }

}
