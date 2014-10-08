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

import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for ReportWriterFactory
 *
 * @author Chris Mair
 */
class ReportWriterFactoryTest extends AbstractTestCase {

    private static final TITLE = 'Custom title'
    private static final OUTPUT_FILE = 'report/CustomReport.data'
    private reportWriterFactory = new ReportWriterFactory()

    @Test
    void testGetReportWriter_Html() {
        assert reportWriterFactory.getReportWriter('html').class == HtmlReportWriter 
    }

    @Test
    void testGetReportWriter_Xml() {
        assert reportWriterFactory.getReportWriter('xml').class == XmlReportWriter 
    }

    @Test
    void testGetReportWriter_InlineXml() {
        assert reportWriterFactory.getReportWriter('inlineXml').class == InlineXmlReportWriter 
    }

    @Test
    void testGetReportWriter_Text() {
        assert reportWriterFactory.getReportWriter('text').class == TextReportWriter
    }

    @Test
    void testGetReportWriter_Console() {
        def reportWriter = reportWriterFactory.getReportWriter('console')
        assert reportWriter.class == TextReportWriter
        assert reportWriter.writeToStandardOut
    }

    @Test
    void testGetReportWriter_Ide() {
        def reportWriter = reportWriterFactory.getReportWriter('ide')
        assert reportWriter.class == IdeTextReportWriter
        assert reportWriter.writeToStandardOut
    }

    @Test
    void testGetReportWriter_SpecifyClassName() {
        assert reportWriterFactory.getReportWriter('org.codenarc.report.HtmlReportWriter').class == HtmlReportWriter
    }

    @Test
    void testGetReportWriter_ThrowsExceptionForClassThatIsNotAReportWriter() {
        shouldFailWithMessageContaining('org.codenarc.CodeNarcRunner') { reportWriterFactory.getReportWriter('org.codenarc.CodeNarcRunner') } 
    }

    @Test
    void testGetReportWriter_ThrowsExceptionForInvalidType() {
        shouldFailWithMessageContaining('xxx') { reportWriterFactory.getReportWriter('xxx') } 
    }

    @Test
    void testGetReportWriter_ThrowsExceptionForNullType() {
        shouldFailWithMessageContaining('type') { reportWriterFactory.getReportWriter(null) } 
    }

    @Test
    void testGetReportWriter_Html_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('html', [title:TITLE, maxPriority:'4'])
        assert reportWriter.class == HtmlReportWriter
        assert reportWriter.title == TITLE
        assert reportWriter.maxPriority == 4
    }

    @Test
    void testGetReportWriter_Xml_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('xml', [outputFile:OUTPUT_FILE])
        assert reportWriter.class == XmlReportWriter
        assert reportWriter.outputFile == OUTPUT_FILE
    }

    @Test
    void testGetReportWriter_Text_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('text', [outputFile:OUTPUT_FILE, maxPriority:'2'])
        assert reportWriter.class == TextReportWriter
        assert reportWriter.outputFile == OUTPUT_FILE
        assert reportWriter.maxPriority == 2
    }

    @Test
    void testGetReportWriter_WithOptions_ThrowsExceptionForInvalidType() {
        shouldFailWithMessageContaining('xxx') { reportWriterFactory.getReportWriter('xxx', [title:TITLE]) } 
    }

    @Test
    void testGetReportWriter_WithOptions_ThrowsExceptionForInvalidOption() {
        shouldFailWithMessageContaining('badOption') { reportWriterFactory.getReportWriter('html', [badOption:'abc']) } 
    }
}
