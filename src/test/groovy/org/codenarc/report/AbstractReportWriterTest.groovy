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
import org.codenarc.results.DirectoryResults
import org.codenarc.results.Results
import org.codenarc.rule.StubRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.captureSystemOut

/**
 * Tests for AbstractReportWriter
 *
 * @author Chris Mair
 */
class AbstractReportWriterTest extends AbstractTestCase {

    private static final RESULTS = new DirectoryResults()
    private static final ANALYSIS_CONTEXT = new AnalysisContext()
    private static final DEFAULT_STRING = '?'
    private static final CUSTOM_FILENAME = 'abc.txt'
    private static final NEW_OUTPUT_DIR = 'tempdir'
    private static final CONTENTS = 'abc'
    private FakeAbstractReportWriter reportWriter

    @Test
    void testWriteReport_WritesToDefaultOutputFile_IfOutputFileIsNull() {
        def defaultOutputFile = FakeAbstractReportWriter.defaultOutputFile
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(defaultOutputFile)
    }

    @Test
    void testWriteReport_WritesToOutputFile_IfOutputFileIsDefined() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(CUSTOM_FILENAME)
    }

    @Test
    void testWriteReport_CreatesOutputDirectoryIfItDoesNotExist() {
        def outputDir = new File(NEW_OUTPUT_DIR)
        outputDir.delete()
        assert !outputDir.exists()
        def filename = NEW_OUTPUT_DIR + '/' + CUSTOM_FILENAME
        reportWriter.outputFile = filename
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(filename)
        outputDir.delete()
    }

    @Test
    void testWriteReport_WritesToStandardOut_IfWriteToStandardOutIsTrue_String() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = 'true'
        def output = captureSystemOut {
            reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        }
        assertFileDoesNotExist(CUSTOM_FILENAME)
        assert output == CONTENTS
    }

    @Test
    void testWriteReport_WritesToStandardOut_IfWriteToStandardOutIsTrue() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = true
        def output = captureSystemOut {
            reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        }
        assertFileDoesNotExist(CUSTOM_FILENAME)
        assert output == CONTENTS
    }

    @Test
    void testWriteReport_WritesToStandardOut_AndResetsSystemOut() {
        def originalSystemOut = System.out
        reportWriter.writeToStandardOut = true
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assert System.out == originalSystemOut
    }

    @Test
    void testWriteReport_WritesToOutputFile_IfWriteToStandardOutIsNotTrue() {
        reportWriter.outputFile = CUSTOM_FILENAME
        reportWriter.writeToStandardOut = 'false'
        reportWriter.writeReport(ANALYSIS_CONTEXT, RESULTS)
        assertOutputFile(CUSTOM_FILENAME)
    }

    @Test
    void testInitializeResourceBundle_CustomMessagesFileExists() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix', null)   // in "codenarc-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc', null)                      // in "codenarc-messages.properties"
    }

    @Test
    void testInitializeResourceBundle_CustomMessagesFileDoesNotExist() {
        reportWriter.customMessagesBundleName = 'DoesNotExist'
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('htmlReport.titlePrefix', null)   // in "codenarc-base-messages.properties"
        assert reportWriter.getResourceBundleString('abc') == DEFAULT_STRING
    }

    @Test
    void testGetResourceBundleString() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('abc') == '123'
    }

    @Test
    void testGetResourceBundleString_ReturnsDefaultStringIfKeyNotFound() {
        reportWriter.initializeResourceBundle()
        assert reportWriter.getResourceBundleString('DoesNotExist') == DEFAULT_STRING
    }

    @Test
    void testGetDescriptionForRule_RuleDescriptionFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX')
        assert reportWriter.getDescriptionForRule(rule) == 'My Rule XX'
    }

    @Test
    void testGetDescriptionForRule_DescriptionPropertySetOnRuleObject() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX', description:'xyz')
        assert reportWriter.getDescriptionForRule(rule) == 'xyz'
    }

    @Test
    void testGetDescriptionForRule_DescriptionContainsSubstitutionParameters() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name: 'MyRuleXX', priority: 3, description: 'xyz.${rule.name}.${rule.priority}')
        assert reportWriter.getDescriptionForRule(rule) == 'xyz.MyRuleXX.3'
    }

    @Test
    void testGetDescriptionForRule_RuleDescriptionNotFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'Unknown')
        assert reportWriter.getDescriptionForRule(rule).startsWith('No description provided')
    }

    @Test
    void testGetHtmlDescriptionForRule_HtmlRuleDescriptionFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX')
        assert reportWriter.getHtmlDescriptionForRule(rule) == 'HTML Rule XX'
    }

    @Test
    void testGetHtmlDescriptionForRule_OnlyRuleDescriptionFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleYY')
        assert reportWriter.getHtmlDescriptionForRule(rule) == 'My Rule YY'
    }

    @Test
    void testGetHtmlDescriptionForRule_DescriptionPropertySetOnRuleObject() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'MyRuleXX', description:'xyz')
        assert reportWriter.getHtmlDescriptionForRule(rule) == 'xyz'
    }

    @Test
    void testGetHtmlDescriptionForRule_DescriptionContainsSubstitutionParameters() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name: 'MyRuleXX', priority: 3, description: 'xyz.${rule.name}.${rule.priority}')
        assert reportWriter.getHtmlDescriptionForRule(rule) == 'xyz.MyRuleXX.3'
    }

    @Test
    void testGetHtmlDescriptionForRule_NoRuleDescriptionNotFoundInMessagesFile() {
        reportWriter.initializeResourceBundle()
        def rule = new StubRule(name:'Unknown')
        assert reportWriter.getHtmlDescriptionForRule(rule).startsWith('No description provided')
    }

    @Test
    void testGetFormattedTimestamp() {
        def timestamp = new Date(1262361072497)
        reportWriter.getTimestamp = { timestamp }
        def expected = java.text.DateFormat.getDateTimeInstance().format(timestamp)
        assert reportWriter.getFormattedTimestamp() == expected
    }

    @Test
    void testGetSortedRules() {
        def ruleSet = new ListRuleSet([new StubRule(name:'BB'), new StubRule(name:'AA'), new StubRule(name:'DD'), new StubRule(name:'CC')])
        def analysisContext = new AnalysisContext(ruleSet:ruleSet)
        def sorted = reportWriter.getSortedRules(analysisContext)
        log(sorted)
        assert sorted.name == ['AA', 'BB', 'CC', 'DD']
    }

    @Test
    void testGetSortedRules_RemovesDisabledRules() {
        def ruleSet = new ListRuleSet([new StubRule(name:'BB', enabled:false), new StubRule(name:'AA'), new StubRule(name:'DD'), new StubRule(name:'CC', enabled:false)])
        def analysisContext = new AnalysisContext(ruleSet:ruleSet)
        def sorted = reportWriter.getSortedRules(analysisContext)
        log(sorted)
        assert sorted.name == ['AA', 'DD']
    }

    @Test
    void testGetCodeNarcVersion() {
        assert reportWriter.getCodeNarcVersion() == new File('src/main/resources/codenarc-version.txt').text
    }

    @Before
    void setUpAbstractReportWriterTest() {
        reportWriter = new FakeAbstractReportWriter()
    }

    private void assertOutputFile(String outputFile) {
        def file = new File(outputFile)
        assert file.exists(), "The output file [$outputFile] does not exist"
        def contents = file.text
        file.delete()
        assert contents == CONTENTS
    }

    private void assertFileDoesNotExist(String filename) {
        assert !new File(filename).exists()
    }

}

/**
 * Concrete subclass of AbstractReportWriter for testing
 */
class FakeAbstractReportWriter extends AbstractReportWriter {
    static defaultOutputFile = 'TestReportWriter.txt'
    String title 

    @Override
    void writeReport(Writer writer, AnalysisContext analysisContext, Results results) {
        writer.write('abc')
        writer.flush()
    }
}
