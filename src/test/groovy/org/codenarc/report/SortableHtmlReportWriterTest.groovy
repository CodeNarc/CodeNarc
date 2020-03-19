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

import org.codenarc.results.FileResults
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Tests for SortableHtmlReportWriter
 *
 * @author Chris Mair
 */
@SuppressWarnings('LineLength')
class SortableHtmlReportWriterTest extends AbstractHtmlReportWriterTestCase {

    private static final TOP_HTML = "<img class='logo' src='$LOGO_FILE' alt='CodeNarc' align='right'/><h1>CodeNarc Report</h1><div class='metadata'><table><tr><td class='em'>Report title:</td><td/></tr><tr><td class='em'>Date:</td><td>Feb 24, 2011 9:32:38 PM</td></tr><tr><td class='em'>Generated with:</td><td><a href='${CODENARC_URL}'>CodeNarc v0.12</a></td></tr></table></div>"
    private static final SUMMARY_HTML = "<div class='summary'><h2>Summary</h2><table><thead><tr class='tableHeader'><th>Total Files</th><th>Files with Violations</th><th>Priority 1</th><th>Priority 2</th><th>Priority 3</th></tr></thead><tbody><tr><td class='number'>10</td><td class='number'>4</td><td class='priority1'>3</td><td class='priority2'>2</td><td class='priority3'>4</td></tr></tbody></table></div>"
    private static final BUTTONS_HTML = "<div class='buttons'><button type='button' onclick='sortData(sortByRuleName)'>Sort by Rule Name</button><button type='button' onclick='sortData(sortByPriority)'>Sort by Rule Priority</button><button type='button' onclick='sortData(sortByRule)'>Sort by Rule (w/Most Violations)</button><button type='button' onclick='sortData(sortByFile)'>Sort by File (w/Most Violations)</button></div>"
    private static final RULE_DESCRIPTIONS_HTML = "<div class='summary'><h2>Rule Descriptions</h2><table border='1'><tr class='tableHeader'><th class='ruleDescriptions'>#</th><th class='ruleDescriptions'>Rule Name</th><th class='ruleDescriptions'>Description</th></tr><tr class='ruleDescriptions'><td><a name='DuplicateImport'></a><span class='ruleIndex'>1</span></td><td class='ruleName priority3'>DuplicateImport</td><td>Duplicate import statements are unnecessary.</td></tr><tr class='ruleDescriptions'><td><a name='ReturnFromFinallyBlock'></a><span class='ruleIndex'>2</span></td><td class='ruleName priority2'>ReturnFromFinallyBlock</td><td>Returning from a <em>finally</em> block is confusing and can hide the original exception.</td></tr><tr class='ruleDescriptions'><td><a name='ThrowExceptionFromFinallyBlock'></a><span class='ruleIndex'>3</span></td><td class='ruleName priority2'>ThrowExceptionFromFinallyBlock</td><td>Throwing an exception from a <em>finally</em> block is confusing and can hide the original exception.</td></tr><tr class='ruleDescriptions'><td><a name='UnnecessaryBooleanInstantiation'></a><span class='ruleIndex'>4</span></td><td class='ruleName priority3'>UnnecessaryBooleanInstantiation</td><td>Use <em>Boolean.valueOf()</em> for variable values or <em>Boolean.TRUE</em> and <em>Boolean.FALSE</em> for constant values instead of calling the <em>Boolean()</em> constructor directly or calling <em>Boolean.valueOf(true)</em> or <em>Boolean.valueOf(false)</em>.</td></tr><tr class='ruleDescriptions'><td><a name='UnnecessaryStringInstantiation'></a><span class='ruleIndex'>5</span></td><td class='ruleName priority3'>UnnecessaryStringInstantiation</td><td>Use a String literal (e.g., \"...\") instead of calling the corresponding String constructor (new String(\"..\")) directly.</td></tr></table></div>"
    private static final VIOLATIONS_HEADING_HTML = '<h2>Violations</h2>'
    private static final VIOLATIONS_TABLE_HEADING_HTML = "<table id='violationsTable' border='1'><thead><tr class='tableHeader'><th>File</th><th>Rule Name</th><th>Priority</th><th>Line #</th><th>Source Line / Message</th></tr></thead>"
    private static final VIOLATIONS_HTML = "${VIOLATIONS_TABLE_HEADING_HTML}<tbody><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyActionTest.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE2'>RULE2</a></td><td class='priority2 priorityColumn'>2</td><td class='number'>222</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>bad stuff</span></p></td></tr><tr><td class='pathColumn'>src/main/MyActionTest.groovy</td><td class='ruleColumn'><a href='#RULE2'>RULE2</a></td><td class='priority2 priorityColumn'>2</td><td class='number'>222</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>bad stuff</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE3'>RULE3</a></td><td class='priority3 priorityColumn'>3</td><td class='number'>333</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>throw new Exception() // Some very long message 12345678..901234567890</span></p><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>Other info</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE3'>RULE3</a></td><td class='priority3 priorityColumn'>3</td><td class='number'>333</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>throw new Exception() // Some very long message 12345678..901234567890</span></p><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>Other info</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction2.groovy</td><td class='ruleColumn'><a href='#RULE3'>RULE3</a></td><td class='priority3 priorityColumn'>3</td><td class='number'>333</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>throw new Exception() // Some very long message 12345678..901234567890</span></p><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>Other info</span></p></td></tr><tr><td class='pathColumn'>src/main/MyOtherAction.groovy</td><td class='ruleColumn'><a href='#RULE3'>RULE3</a></td><td class='priority3 priorityColumn'>3</td><td class='number'>333</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>throw new Exception() // Some very long message 12345678..901234567890</span></p><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>Other info</span></p></td></tr></tbody></table>"

    private static jsFileContents

    @Test
    void testDefaultProperties() {
        def newReportWriter = new SortableHtmlReportWriter()
        assert newReportWriter.includeRuleDescriptions
        assert newReportWriter.defaultOutputFile == SortableHtmlReportWriter.DEFAULT_OUTPUT_FILE
    }

    @Test
    void testWriteReport() {
        final EXPECTED = """
            <html><head><title>CodeNarc Report</title><style type='text/css'>$cssFileContents</style><script>$jsFileContents</script></head>
            <body>${TOP_HTML}${SUMMARY_HTML}${VIOLATIONS_HEADING_HTML}${BUTTONS_HTML}${VIOLATIONS_HTML}${RULE_DESCRIPTIONS_HTML}</body></html>
            """
        assertReportFileContents(NEW_REPORT_FILE, EXPECTED)
    }

    @Test
    void testWriteReport_MaxPriority() {
        final MAX_PRIORITY_SUMMARY_HTML = "<div class='summary'><h2>Summary</h2><table><thead><tr class='tableHeader'><th>Total Files</th><th>Files with Violations</th><th>Priority 1</th><th>Priority 2</th></tr></thead><tbody><tr><td class='number'>10</td><td class='number'>2</td><td class='priority1'>3</td><td class='priority2'>2</td></tr></tbody></table></div>"
        final MAX_PRIORITY_VIOLATIONS_HTML = "${VIOLATIONS_TABLE_HEADING_HTML}<tbody><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyActionTest.groovy</td><td class='ruleColumn'><a href='#RULE1'>RULE1</a></td><td class='priority1 priorityColumn'>1</td><td class='number'>111</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[SRC]</span><span class='sourceCode'>if (file) {</span></p></td></tr><tr><td class='pathColumn'>src/main/MyAction.groovy</td><td class='ruleColumn'><a href='#RULE2'>RULE2</a></td><td class='priority2 priorityColumn'>2</td><td class='number'>222</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>bad stuff</span></p></td></tr><tr><td class='pathColumn'>src/main/MyActionTest.groovy</td><td class='ruleColumn'><a href='#RULE2'>RULE2</a></td><td class='priority2 priorityColumn'>2</td><td class='number'>222</td><td><p class='violationInfo'><span class='violationInfoPrefix'>[MSG]</span><span class='violationMessage'>bad stuff</span></p></td></tr></tbody></table>"
        final EXPECTED = """
            <html><head><title>CodeNarc Report</title><style type='text/css'>$cssFileContents</style><script>$jsFileContents</script></head>
            <body>${TOP_HTML}${MAX_PRIORITY_SUMMARY_HTML}${VIOLATIONS_HEADING_HTML}${BUTTONS_HTML}${MAX_PRIORITY_VIOLATIONS_HTML}${RULE_DESCRIPTIONS_HTML}</body></html>
            """
        reportWriter.maxPriority = 2
        assertReportFileContents(NEW_REPORT_FILE, EXPECTED)
    }

    @Test
    void testWriteReport_IncludeRuleDescriptions_False() {
        final EXPECTED = """
            <html><head><title>CodeNarc Report</title><style type='text/css'>$cssFileContents</style><script>$jsFileContents</script></head>
            <body>${TOP_HTML}${SUMMARY_HTML}${VIOLATIONS_HEADING_HTML}${BUTTONS_HTML}${VIOLATIONS_HTML}</body></html>
            """
        reportWriter.includeRuleDescriptions = false
        assertReportContents(EXPECTED)
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @BeforeClass
    static void initializeJavascript() {
        jsFileContents = new File('src/main/resources/js/sort-table.js').text
    }

    @Before
    void setUpHtmlReportWriterTest() {
        reportWriter = new SortableHtmlReportWriter(outputFile:NEW_REPORT_FILE)
        reportWriter.metaClass.getFormattedTimestamp << { 'Feb 24, 2011 9:32:38 PM' }
        reportWriter.metaClass.getCodeNarcVersion << { '0.12' }

        def fileResults4 = new FileResults('src/main/MyOtherAction.groovy', [VIOLATION3])
        dirResultsMain.addChild(fileResults4)
    }

}
