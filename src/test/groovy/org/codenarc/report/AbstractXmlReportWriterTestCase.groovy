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
abstract class AbstractXmlReportWriterTestCase extends AbstractReportWriterTestCase {

    protected reportWriter
    protected analysisContext
    protected results
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
