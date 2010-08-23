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

/**
 * Tests for ReportWriterFactory
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ReportWriterFactoryTest extends AbstractTestCase {

    private static final TITLE = 'Custom title'
    private static final OUTPUT_FILE = 'report/CustomReport.data'
    private reportWriterFactory = new ReportWriterFactory()

    void testGetReportWriter_Html() {
        assert reportWriterFactory.getReportWriter('html').class == HtmlReportWriter 
    }

    void testGetReportWriter_Xml() {
        assert reportWriterFactory.getReportWriter('xml').class == XmlReportWriter 
    }

    void testGetReportWriter_InlineXml() {
        assert reportWriterFactory.getReportWriter('inlineXml').class == InlineXmlReportWriter 
    }

    void testGetReportWriter_Text() {
        assert reportWriterFactory.getReportWriter('text').class == TextReportWriter
    }

    void testGetReportWriter_SpecifyClassName() {
        assert reportWriterFactory.getReportWriter('org.codenarc.report.HtmlReportWriter').class == HtmlReportWriter
    }

    void testGetReportWriter_ThrowsExceptionForClassThatIsNotAReportWriter() {
        shouldFailWithMessageContaining('org.codenarc.CodeNarcRunner') { reportWriterFactory.getReportWriter('org.codenarc.CodeNarcRunner') } 
    }

    void testGetReportWriter_ThrowsExceptionForInvalidType() {
        shouldFailWithMessageContaining('xxx') { reportWriterFactory.getReportWriter('xxx') } 
    }

    void testGetReportWriter_ThrowsExceptionForNullType() {
        shouldFailWithMessageContaining('type') { reportWriterFactory.getReportWriter(null) } 
    }

    void testGetReportWriter_Html_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('html', [title:TITLE])
        assert reportWriter.class == HtmlReportWriter
        assert reportWriter.title == TITLE
    }

    void testGetReportWriter_Xml_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('xml', [outputFile:OUTPUT_FILE])
        assert reportWriter.class == XmlReportWriter
        assert reportWriter.outputFile == OUTPUT_FILE
    }

    void testGetReportWriter_Text_WithOptions() {
        def reportWriter = reportWriterFactory.getReportWriter('text', [outputFile:OUTPUT_FILE])
        assert reportWriter.class == TextReportWriter
        assert reportWriter.outputFile == OUTPUT_FILE
    }

    void testGetReportWriter_WithOptions_ThrowsExceptionForInvalidType() {
        shouldFailWithMessageContaining('xxx') { reportWriterFactory.getReportWriter('xxx', [title:TITLE]) } 
    }

    void testGetReportWriter_WithOptions_ThrowsExceptionForInvalidOption() {
        shouldFailWithMessageContaining('badOption') { reportWriterFactory.getReportWriter('html', [badOption:'abc']) } 
    }
}