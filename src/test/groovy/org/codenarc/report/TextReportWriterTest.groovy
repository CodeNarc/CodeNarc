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

/**
 * Tests for TestReportWriter
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class TextReportWriterTest extends AbstractTextReportWriterTestCase<TextReportWriter> {

    private static final REPORT_TEXT = """
CodeNarc Report: My Cool Project - ${formattedTimestamp()}

Summary: TotalFiles=4 FilesWithViolations=3 P1=3 P2=0 P3=5

File: src/main/MyAction.groovy
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]
    Violation: Rule=Rule3 P=3 Line=333 Msg=[Other info c:\\\\data] Src=[throw new Exception("cdata=<![CDATA[whatever]]>") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]
    Violation: Rule=Rule3 P=3 Line=333 Msg=[Other info c:\\\\data] Src=[throw new Exception("cdata=<![CDATA[whatever]]>") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]
    Violation: Rule=Rule2 P=3 Line=222 Msg=[bad stuff: !@#\$%^&*()_+<>]

File: src/main/dao/MyDao.groovy
    Violation: Rule=Rule3 P=3 Line=333 Msg=[Other info c:\\\\data] Src=[throw new Exception("cdata=<![CDATA[whatever]]>") // c:\\\\data - Some very long message 1234567890123456789012345678901234567890]

File: src/main/dao/MyOtherDao.groovy
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]
    Violation: Rule=Rule2 P=3 Line=222 Msg=[bad stuff: !@#\$%^&*()_+<>]

[CodeNarc (${CODENARC_URL}) v${version()}]
""".trim()

    private static final REPORT_TEXT_MAX_PRIORITY = """
CodeNarc Report: My Cool Project - ${formattedTimestamp()}

Summary: TotalFiles=4 FilesWithViolations=2 P1=3

File: src/main/MyAction.groovy
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]

File: src/main/dao/MyOtherDao.groovy
    Violation: Rule=Rule1 P=1 Line=111 Src=[if (count < 23 && index <= 99 && name.contains('\u0000')) {]

[CodeNarc (${CODENARC_URL}) v${version()}]
""".trim()

    @Override
    protected TextReportWriter createReportWriter() {
        return new TextReportWriter(title:TITLE)
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
