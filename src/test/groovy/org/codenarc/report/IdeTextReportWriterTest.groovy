/*
 * Copyright 2014 the original author or authors.
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

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.junit.Test

/**
 * Tests for IdeTestReportWriter
 *
 * @author Chris Mair
 */
class IdeTextReportWriterTest extends AbstractTextReportWriterTestCase {

    private static final REPORT_TEXT = """
CodeNarc Report: My Cool Project - ${formattedTimestamp()}

Summary: TotalFiles=6 FilesWithViolations=3 P1=3 P2=2 P3=3

File: src/main/MyAction.groovy
    Violation: Rule=Rule1 P=1 Loc=.(MyAction.groovy:11) Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=Rule1 P=1 Loc=.(MyAction.groovy:11) Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=AnotherRule P=2 Loc=.(MyAction.groovy:2) Msg=[bad stuff: !@#\$%^&*()_+<>]
    Violation: Rule=BadStuff P=3 Loc=.(MyAction.groovy:333) Msg=[Other info] Src=[throw new Exception() // Something bad happened]
    Violation: Rule=BadStuff P=3 Loc=.(MyAction.groovy:333) Msg=[Other info] Src=[throw new Exception() // Something bad happened]

File: src/main/dao/MyDao.groovy
    Violation: Rule=BadStuff P=3 Loc=.(MyDao.groovy:333) Msg=[Other info] Src=[throw new Exception() // Something bad happened]

File: src/main/dao/MyOtherDao.groovy
    Violation: Rule=Rule1 P=1 Loc=.(MyOtherDao.groovy:11) Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=AnotherRule P=2 Loc=.(MyOtherDao.groovy:2) Msg=[bad stuff: !@#\$%^&*()_+<>]

[CodeNarc (${CODENARC_URL}) v${version()}]
""".trim()
    private static final REPORT_TEXT_MAX_PRIORITY = """
CodeNarc Report: My Cool Project - ${formattedTimestamp()}

Summary: TotalFiles=6 FilesWithViolations=2 P1=3

File: src/main/MyAction.groovy
    Violation: Rule=Rule1 P=1 Loc=.(MyAction.groovy:11) Src=[if (count < 23 && index <= 99) {]
    Violation: Rule=Rule1 P=1 Loc=.(MyAction.groovy:11) Src=[if (count < 23 && index <= 99) {]

File: src/main/dao/MyOtherDao.groovy
    Violation: Rule=Rule1 P=1 Loc=.(MyOtherDao.groovy:11) Src=[if (count < 23 && index <= 99) {]

[CodeNarc (${CODENARC_URL}) v${version()}]
""".trim()

    @Test
    void testWriteReport_NullLineNumberInViolation() {
        final REPORT_TEXT = """
CodeNarc Report: My Cool Project - ${formattedTimestamp()}

Summary: TotalFiles=0 FilesWithViolations=1 P1=0 P2=0 P3=1

File: src/main/dao/MyDao.groovy
    Violation: Rule=BadStuff P=3 Loc=.(MyDao.groovy:0) Msg=[Other info] Src=[throw new Exception() // Something bad happened]

[CodeNarc (${CODENARC_URL}) v${version()}]
""".trim()

        final VIOLATION = new Violation(rule:new StubRule(name:'BadStuff', priority:3), lineNumber:null, sourceLine:SOURCE_LINE3, message:MESSAGE3)
        def fileResults = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION])
        results = new DirectoryResults()
        results.addChild(fileResults)

        reportWriter.writeReport(stringWriter, analysisContext, results)
        def reportText = stringWriter.toString()
        assertReportText(reportText, REPORT_TEXT)
    }

    @Override
    protected TextReportWriter createReportWriter() {
        return new IdeTextReportWriter(title:TITLE)
    }

    @Override
    protected String getReportTextMaxPriority() {
        return REPORT_TEXT_MAX_PRIORITY
    }

    @Override
    protected String getReportText() {
        return REPORT_TEXT
    }

}
