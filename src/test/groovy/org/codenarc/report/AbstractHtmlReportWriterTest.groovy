/*
 * Copyright 2013 the original author or authors.
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

import org.codenarc.AnalysisContext
import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for AbstractHtmlReportWriter
 *
 * @author Chris Mair
 */
@SuppressWarnings('LineLength')
class AbstractHtmlReportWriterTest extends AbstractTestCase {

    private static final MESSAGE = 'bad stuff'
    private static final LINE1 = 111
    private static final LINE2 = 222
    private static final VIOLATION1 = new Violation(rule:new StubRule(name:'RULE1', priority:1), lineNumber:LINE1, sourceLine:'if (file) {')
    private static final VIOLATION2 = new Violation(rule:new StubRule(name:'RULE2', priority:2), lineNumber:LINE2, message:MESSAGE)

    private AbstractHtmlReportWriter reportWriter
    private analysisContext
    private results
    private ruleSet

    @Test
    void testDefaultProperties() {
        assert reportWriter.includeRuleDescriptions
        assert reportWriter.maxPriority == 3
    }

    @Test
    void testWriteReport_NullResults() {
        shouldFailWithMessageContaining('results') { reportWriter.writeReport(analysisContext, null) }
    }

    @Test
    void testWriteReport_NullAnalysisContext() {
        shouldFailWithMessageContaining('analysisContext') { reportWriter.writeReport(null, results) }
    }

    @Test
    void testIsDirectoryContainingFilesWithViolations_FileResults() {
        def results = new FileResults('', [])
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        results = new FileResults('', [VIOLATION1])
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)
    }

    @Test
    void testIsDirectoryContainingFilesWithViolations_DirectoryResults() {
        def results = new DirectoryResults('')
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        results.addChild(new FileResults('', []))
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results), 'child with no violations'

        def child = new DirectoryResults('')
        child.addChild(new FileResults('', [VIOLATION1]))
        results.addChild(child)
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results), 'grandchild with violations'

        results.addChild(new FileResults('', [VIOLATION2]))
        assert reportWriter.isDirectoryContainingFilesWithViolations(results)

        reportWriter.maxPriority = 1
        assert !reportWriter.isDirectoryContainingFilesWithViolations(results)

        reportWriter.maxPriority = 2
        assert reportWriter.isDirectoryContainingFilesWithViolations(results)

        reportWriter.maxPriority = 1
        results.addChild(new FileResults('', [VIOLATION1]))
        assert reportWriter.isDirectoryContainingFilesWithViolations(results)
    }

    @Test
    void testIsDirectoryContainingFiles() {
        def results = new FileResults('', [])
        assert !reportWriter.isDirectoryContainingFiles(results)

        results = new DirectoryResults('')
        assert !reportWriter.isDirectoryContainingFiles(results)

        results.numberOfFilesInThisDirectory = 2
        assert reportWriter.isDirectoryContainingFiles(results)
    }

    @Test
    void testFormatSourceLine() {
        assert reportWriter.formatSourceLine('') == null
        assert reportWriter.formatSourceLine('abc') == 'abc'
        assert reportWriter.formatSourceLine('abcdef' * 20) == 'abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefab..abcdefabcdef'
        assert reportWriter.formatSourceLine('abc', 2) == 'abc'
        assert reportWriter.formatSourceLine('abcdef' * 20, 2) == 'cdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcd..abcdefabcdef'
    }

    //------------------------------------------------------------------------------------
    // Setup and helper methods
    //------------------------------------------------------------------------------------

    @Before
    void setUpHtmlReportWriterTest() {
        reportWriter = new AbstractHtmlReportWriter() {
            @Override
            protected String getCssFile() { }

            @Override
            protected Closure buildBodySection(AnalysisContext analysisContext, Results results) { }
        }
        results = new DirectoryResults()

        ruleSet = new ListRuleSet([])
        analysisContext = new AnalysisContext(sourceDirectories:['/src/main'], ruleSet:ruleSet)
    }

}
