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

import static org.codenarc.test.TestUtil.*
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for BaselineXmlReportParser
 */
class BaselineXmlReportParserTest extends AbstractTestCase {

    private static final XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='src/main/MyAction.groovy'>
                <Violation ruleName='UnusedImport'/>
                <Violation ruleName='EmptyCatchBlock'>
                    <Message><![CDATA[Message 1]]></Message>
                </Violation>
                <Violation ruleName='EmptyCatchBlock'>
                    <Message><![CDATA[Message 2]]></Message>
                </Violation>
                <Violation ruleName='UnusedPrivateMethod'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*(joe&apos;s)_+&lt;&gt;&quot;ABC&quot;]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """
    private static final EXPECTED_RESULTS = ['src/main/MyAction.groovy':[
            v('UnusedImport', ''),
            v('EmptyCatchBlock', 'Message 1'),
            v('EmptyCatchBlock', 'Message 2'),
            v('UnusedPrivateMethod', 'bad stuff: !@#$%^&*(joe\'s)_+<>"ABC"') ]]

    private BaselineXmlReportParser parser = new BaselineXmlReportParser()

    @Test
    void test_parseBaselineXmlReport_InputStream() {
        def inputStream = new ByteArrayInputStream(XML.bytes)
        def results = parser.parseBaselineXmlReport(inputStream)
        log(results)
        assert results == EXPECTED_RESULTS
    }

    @Test
    void test_parseBaselineXmlReport() {
        def results = parser.parseBaselineXmlReport(XML)
        log(results)
        assert results == EXPECTED_RESULTS
    }

    @Test
    void test_parseBaselineXmlReport_MultipleFiles() {
        final XML = """<?xml version='1.0'?>
        <CodeNarc>
            <File path='src/main/MyAction.groovy'>
                <Violation ruleName='UnusedImport'/>
                <Violation ruleName='EmptyCatchBlock'>
                    <Message><![CDATA[Message 1]]></Message>
                </Violation>
                <Violation ruleName='EmptyCatchBlock'>
                    <Message><![CDATA[Message 2]]></Message>
                </Violation>
                <Violation ruleName='UnusedPrivateMethod'>
                    <Message><![CDATA[bad stuff: !@#\$%^&amp;*()_+&lt;&gt;]]></Message>
                </Violation>
            </File>

            <File path='src/main/dao/MyDao.groovy'>
                <Violation ruleName='EmptyCatchBlock'>
                    <Message><![CDATA[Message 3]]></Message>
                </Violation>
            </File>
            <File path='src/main/dao/MyOtherDao.groovy'>
                <Violation ruleName='UnusedPrivateMethod'>
                    <Message><![CDATA[Message 4]]></Message>
                </Violation>
            </File>
        </CodeNarc>
        """

        def results = parser.parseBaselineXmlReport(XML)
        log(results)
        def expected = [
            'src/main/MyAction.groovy':[
                v('UnusedImport', ''),
                v('EmptyCatchBlock', 'Message 1'),
                v('EmptyCatchBlock', 'Message 2'),
                v('UnusedPrivateMethod', 'bad stuff: !@#$%^&*()_+<>') ],
            'src/main/dao/MyDao.groovy':[
                v('EmptyCatchBlock', 'Message 3') ],
            'src/main/dao/MyOtherDao.groovy':[
                v('UnusedPrivateMethod', 'Message 4') ],
        ]
        log(expected)
        assert results == expected
    }

    @Test
    void test_parseBaselineXmlReport_Null_String() {
        shouldFailWithMessageContaining('xmlString') { parser.parseBaselineXmlReport((String)null) }
    }

    @Test
    void test_parseBaselineXmlReport_Null_InputStream() {
        shouldFailWithMessageContaining('inputStream') { parser.parseBaselineXmlReport((InputStream)null) }
    }

    @Test
    void test_unescapeXml() {
        assert parser.unescapeXml(null) == null
        assert parser.unescapeXml('') == ''
        assert parser.unescapeXml('abc') == 'abc'
        assert parser.unescapeXml('&quot;bread&quot; &amp; &quot;butter&quot;, &apos;water&apos;, &lt;1&gt;') ==
                /"bread" & "butter", 'water', <1>/
    }

    private static BaselineViolation v(String ruleName, String message) {
        return new BaselineViolation(ruleName:ruleName, message:message)
    }

}
