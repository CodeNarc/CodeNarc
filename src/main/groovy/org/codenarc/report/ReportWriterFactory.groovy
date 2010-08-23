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

/**
 * Factory for ReportWriter objects based on the report type (name).
 * <p>
 * The passed in <code>type</code> can either be one of the predefined type names: "html" or "xml", or
 * else it can specify the fully-qualified class name of a class (accessible on the classpath) that
 * implements the <code>org.codenarc.report.ReportWriter</code> interface.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class ReportWriterFactory {

    ReportWriter getReportWriter(String type) {
        assert type
        switch(type) {
            case 'html': return new HtmlReportWriter()
            case 'xml': return new XmlReportWriter()
            case 'text': return new TextReportWriter()
            case 'inlinexml' : return new InlineXmlReportWriter()
        }

        def reportClass = getClass().classLoader.loadClass(type)
        return reportClass.newInstance()
    }

    ReportWriter getReportWriter(String type, Map options) {
        def reportWriter = getReportWriter(type)
        options.each { name, value -> reportWriter[name] = value }
        return reportWriter
    }
}
