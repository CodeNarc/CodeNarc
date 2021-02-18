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

import org.codenarc.rule.Violation
import org.codenarc.rule.basic.EmptyCatchBlockRule
import org.codenarc.rule.imports.UnusedImportRule
import org.codenarc.rule.unused.UnusedPrivateMethodRule
import org.codenarc.test.AbstractTestCase

import java.text.DateFormat

import static org.junit.Assert.assertEquals

/**
 * Abstract superclass for tests for XML Report Writers
 *
 * @author Chris Mair
 */
abstract class AbstractXmlReportWriterTestCase extends AbstractTestCase {

    protected static final LINE1 = 111
    protected static final LINE2 = 222
    protected static final LINE3 = 333
    protected static final SOURCE_LINE1 = 'if (count < 23 && index <= 99 && name.contains(\'\u0000\')) {'
    protected static final SOURCE_LINE3 = 'throw new Exception("cdata=<![CDATA[whatever]]>") // Some very long message 1234567890123456789012345678901234567890'
    protected static final MESSAGE2 = 'bad stuff: !@#$%^&*()_+<>'
    protected static final MESSAGE3 = 'Other info'
    protected static final VIOLATION1 = new Violation(rule:new UnusedImportRule(), lineNumber:LINE1, sourceLine:SOURCE_LINE1)
    protected static final VIOLATION2 = new Violation(rule:new UnusedPrivateMethodRule(), lineNumber:LINE2, message:MESSAGE2)
    protected static final VIOLATION3 = new Violation(rule:new EmptyCatchBlockRule(), lineNumber:LINE3, sourceLine:SOURCE_LINE3, message:MESSAGE3)
    protected static final TITLE = 'My Cool Project'
    protected static final SRC_DIR1 = 'c:/MyProject/src/main/groovy'
    protected static final SRC_DIR2 = 'c:/MyProject/src/test/groovy'
    protected static final VERSION_FILE = 'src/main/resources/codenarc-version.txt'
    protected static final VERSION = new File(VERSION_FILE).text
    protected static final TIMESTAMP_DATE = new Date(1262361072497)
    protected static final FORMATTED_TIMESTAMP = DateFormat.getDateTimeInstance().format(TIMESTAMP_DATE)
    protected static final String CODENARC_URL = 'https://www.codenarc.org'

    protected reportWriter
    protected analysisContext
    protected results, srcMainDaoDirResults
    protected ruleSet
    protected stringWriter

    @SuppressWarnings('JUnitStyleAssertions')
    protected void assertXml(String actualXml, String expectedXml) {
        log(actualXml)
        assertEquals(normalizeXml(expectedXml), normalizeXml(actualXml))
    }

    protected String normalizeXml(String xml) {
        xml.replaceAll(/\>\s*\</, '><').trim()
    }

}
