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

import static org.junit.Assert.*

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule
import org.codenarc.ruleset.ListRuleSet
import org.junit.jupiter.api.BeforeEach

/**
 * Abstract superclass for tests for JSON Report Writers
 *
 * @author Nicolas Vuillamy
 * @author Chris Mair
 */
abstract class AbstractJsonReportWriterTestCase<T extends AbstractReportWriter> extends AbstractReportWriterTestCase {

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

    @BeforeEach
    void setupAbstractJsonReportWriterTestCase() {
        def srcMainDirResults = new DirectoryResults('src/main')
        def srcMainDaoDirResults = new DirectoryResults('src/main/dao')
        def srcTestDirResults = new DirectoryResults('src/test')
        def srcMainFileResults1 = new FileResults('src/main/MyAction.groovy', [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        def srcMainFileResults2 = new FileResults('src/main/MyCleanAction.groovy', [])
        def fileResultsMainDao1 = new FileResults('src/main/dao/MyDao.groovy', [VIOLATION3])
        def fileResultsMainDao2 = new FileResults('src/main/dao/MyOtherDao.groovy', [VIOLATION2])

        srcMainDirResults.addChild(srcMainFileResults1)
        srcMainDirResults.addChild(srcMainFileResults2)
        srcMainDirResults.addChild(srcMainDaoDirResults)
        srcMainDaoDirResults.addChild(fileResultsMainDao1)
        srcMainDaoDirResults.addChild(fileResultsMainDao2)

        results = new DirectoryResults()
        results.addChild(srcMainDirResults)
        results.addChild(srcTestDirResults)

        ruleSet = new ListRuleSet([
                // NOT in alphabetical order
                new UnnecessaryBooleanInstantiationRule(),
                new DuplicateImportRule(description:'Custom: Duplicate imports')
        ])
        analysisContext = new AnalysisContext(sourceDirectories:[SRC_DIR1, SRC_DIR2], ruleSet:ruleSet)
        stringWriter = new StringWriter()
    }

}
