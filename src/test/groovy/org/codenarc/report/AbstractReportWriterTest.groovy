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

import org.codenarc.test.AbstractTestCase
import org.codenarc.results.Results
import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.rule.StubRule

/**
 * Tests for AbstractReportWriter
 *
 * @author Chris Mair
 * @version $Revision: 27 $ - $Date: 2009-12-10 21:26:04 -0500 (Thu, 10 Dec 2009) $
 */
class AbstractReportWriterTest extends AbstractTestCase {

    private static final RESULTS = new DirectoryResults()
    private static final ANALYSIS_CONTEXT = new AnalysisContext()
    private static final DEFAULT_STRING = '?'
    private reportWriter

    void testWriteReport_WritesToDefaultOutputFile_IfOutputFileIsNull() {
        def defaultOutputFile = TestAbstractReportWriter.defaultOutputFile
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(defaultOutputFile)
    }

    void testWriteReport_WritesToOutputFile_IfOutputFileIsDefined() {
        final NAME = 'abc.txt'
        reportWriter.outputFile = NAME
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(NAME)
    }

    void testInitializeResourceBundle_CustomMessagesFileExists() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix', null)   // in "codenarc-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc', null)                      // in "codenarc-messages.properties"
    }

    void testInitializeResourceBundle_CustomMessagesFileDoesNotExist() {
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix', null)   // in "codenarc-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc') == DEFAULT_STRING
    }

    void testGetResourceBundleString() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('abc') == '123'
    }

    void testGetResourceBundleString_ReturnsDefaultStringIfKeyNotFound() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('DoesNotExist') == DEFAULT_STRING
    }

    void testGetDescriptionForRule_RuleDescriptionFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX')
        assert reportWriter.getDescriptionForRule(rule) == 'My Rule XX'
    }

    void testGetDescriptionForRule_DescriptionPropertySetOnRuleObject() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX', description:'xyz')
        assert reportWriter.getDescriptionForRule(rule) == 'xyz'
    }

    void testGetDescriptionForRule_RuleDescriptionNotFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'Unknown')
        assert reportWriter.getDescriptionForRule(rule).startsWith('No description provided')
    }

    void testGetCodeNarcVersion() {
        assert reportWriter.getCodeNarcVersion() == new File('src/main/resources/codenarc-version.txt').text
    }

    void setUp() {
        super.setUp()
        reportWriter = new TestAbstractReportWriter()
    }

    private void assertOutputFile(String outputFile) {
        def file = new File(outputFile)
        assert file.exists(), "The output file [$outputFile] does not exist"
        def contents = file.text
        file.delete()
        assert contents == 'abc'
    }
}
/**
 * Concrete subclass of AbstractReportWriter for testing
 */
class TestAbstractReportWriter extends AbstractReportWriter {
    static defaultOutputFile = 'TestReportWriter.txt'
    String title 

    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        writer.withWriter { w -> w.write('abc') }
    }
}