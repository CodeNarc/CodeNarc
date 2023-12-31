/*
 * Copyright 2020 the original author or authors.
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

import static org.junit.Assert.assertEquals

import org.codenarc.rule.Violation
import org.codenarc.rule.basic.EmptyCatchBlockRule
import org.codenarc.rule.imports.UnusedImportRule
import org.codenarc.rule.unused.UnusedPrivateMethodRule
import org.codenarc.test.AbstractTestCase

import java.text.DateFormat

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Abstract superclass for tests for JSON Report Writers
 *
 * @author Nicolas Vuillamy
 */
abstract class AbstractJsonReportWriterTestCase extends AbstractReportWriterTestCase {

    protected reportWriter
    protected analysisContext
    protected results
    protected ruleSet
    protected stringWriter

    @SuppressWarnings('JUnitStyleAssertions')
    protected void assertJson(String actualJson, String expectedJson) {
        log(actualJson)
        log(expectedJson)
        assertEquals(normalizeJson(actualJson), normalizeJson(expectedJson))
    }

    // Remove first "{", last "}", "]", ",", "\n" from expected partial json in case there is more elements in actual Json
    protected void assertContainsJson(String actualJson, String expectedPartialJson) {
        assert expectedPartialJson.length() > 0
        String[] removeLs = ['}', ']', ', ', '\n', '\r\n']
        String normalizedExpectedJson = normalizeJson(expectedPartialJson)
        String truncatedPartialExpectedJson = normalizedExpectedJson[((1..(normalizedExpectedJson.length() - 1)))]
        while (true) {
            String lastChar = truncatedPartialExpectedJson.charAt(truncatedPartialExpectedJson.length() - 1)
            if (removeLs.contains(lastChar)) {
                truncatedPartialExpectedJson = truncatedPartialExpectedJson[0 .. (truncatedPartialExpectedJson.length() - 2)]
                continue
            }
            break
        }
        assert normalizeJson(actualJson).contains(truncatedPartialExpectedJson)
    }

    protected String normalizeJson(String json) {
        def jsonMap = new JsonSlurper().parseText(json)
        jsonMap = jsonMap.sort()*.key // Sort by key name
        return JsonOutput.toJson(jsonMap).replaceAll('\\n|\\r\\n', System.getProperty('line.separator'))
    }

}
