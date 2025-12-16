/*
 * Copyright 2021 the original author or authors.
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
 * Tests for CompactTextReportWriter
 *
 * @author Luis Zimmermann
 * @author Chris Mair
 */
class CompactTextReportWriterTest extends AbstractTextReportWriterTestCase<CompactTextReportWriter> {

    private static final REPORT_TEXT = """
        |src/main/MyAction.groovy:111:Rule1 null
        |src/main/MyAction.groovy:111:Rule1 null
        |src/main/MyAction.groovy:333:Rule3 Other info c:\\\\data
        |src/main/MyAction.groovy:333:Rule3 Other info c:\\\\data
        |src/main/MyAction.groovy:222:Rule2 bad stuff: !@#\$%^&*()_+<>
        |src/main/dao/MyDao.groovy:333:Rule3 Other info c:\\\\data
        |src/main/dao/MyOtherDao.groovy:111:Rule1 null
        |src/main/dao/MyOtherDao.groovy:222:Rule2 bad stuff: !@#\$%^&*()_+<>
    """.trim().stripMargin()

    private static final REPORT_TEXT_MAX_PRIORITY = '''
        |src/main/MyAction.groovy:111:Rule1 null
        |src/main/MyAction.groovy:111:Rule1 null
        |src/main/dao/MyOtherDao.groovy:111:Rule1 null
    '''.trim().stripMargin()

    @Override
    protected CompactTextReportWriter createReportWriter() {
        return new CompactTextReportWriter()
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
