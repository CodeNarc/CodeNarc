/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.analyzer

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.FakeCountRule

import org.codenarc.ruleset.ListRuleSet
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.assertEqualSets
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import org.codenarc.rule.FakePathRule

/**
 * Tests for FilesystemSourceAnalyzer.
 *
 * @author Chris Mair
 */
class FilesystemSourceAnalyzerTest extends AbstractTestCase {
    private static final BASE_DIR = 'src/test/resources/sourcewithdirs'
    private analyzer
    private ruleSet
    private testCountRule

    @Test
    void testAnalyze_NullRuleSet() {
        analyzer.baseDirectory = BASE_DIR
        shouldFailWithMessageContaining('ruleSet') { analyzer.analyze(null) }
    }

    @Test
    void testAnalyze_BaseDirectoryNull() {
        shouldFailWithMessageContaining('baseDirectory') { analyzer.analyze(ruleSet) }
    }

    @Test
    void testAnalyze_BaseDirectoryEmpty() {
        analyzer.baseDirectory = ''
        shouldFailWithMessageContaining('baseDirectory') { analyzer.analyze(ruleSet) }
    }

    @Test
    void testAnalyze_FilesOnly() {
        final DIR = 'src/test/resources/source'
        analyzer.baseDirectory = DIR
        ruleSet = new ListRuleSet([new FakePathRule()])     // override
        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def paths = resultsPaths(results)
        log("paths=$paths")
        assertEqualSets(paths, ['SourceFile1.groovy', 'SourceFile2.groovy'])

        def fullPaths = results.violations*.message
        assertEqualSets(fullPaths, [
                'src/test/resources/source/SourceFile1.groovy',
                'src/test/resources/source/SourceFile2.groovy'
        ])
        assert results.getNumberOfFilesWithViolations(3) == 2
        assert results.totalNumberOfFiles == 2
    }

    @Test
    void testAnalyze() {
        analyzer.baseDirectory = BASE_DIR
        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def fullPaths = results.violations*.message
        assertEqualSets(fullPaths, [
                'src/test/resources/sourcewithdirs/SourceFile1.groovy',
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File2.groovy',
                'src/test/resources/sourcewithdirs/subdir2/subdir2a/Subdir2aFile1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])
        assert testCountRule.count == 5
        assert results.getNumberOfFilesWithViolations(3) == 5
        assert results.totalNumberOfFiles == 5

        // Verify that the directory structure is properly reflected within the results
        assert childResultsClasses(results) == [DirectoryResults]
        def top = results.children[0]
        assertEqualSets(childResultsClasses(top), [FileResults, DirectoryResults, DirectoryResults])
    }

    @Test
    void testAnalyze_NoViolations() {
        analyzer.baseDirectory = BASE_DIR
        ruleSet = new ListRuleSet([testCountRule])
        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def paths = resultsPaths(results)
        assertEqualSets(paths, ['subdir1', 'subdir2', 'subdir2/subdir2a'])

        assert testCountRule.count == 5
        assert results.getNumberOfFilesWithViolations(3) == 0
        assert results.totalNumberOfFiles == 5
    }

    @Test
    void testAnalyze_IncludesAndExcludes() {
        analyzer.baseDirectory = BASE_DIR
        analyzer.includes = '**ubdir*.groovy'
        analyzer.excludes = '**/*File2*'

        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def fullPaths = results.violations*.message
        assertEqualSets(fullPaths, [
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/subdir2a/Subdir2aFile1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])

        assert testCountRule.count == 3
        assert results.getNumberOfFilesWithViolations(3) == 3
        assert results.totalNumberOfFiles == 3
    }

    @Test
    void testAnalyze_IncludesAndExcludes_Lists() {
        analyzer.baseDirectory = BASE_DIR
        analyzer.includes = '**/Subdir1File1.groovy,**/Subdir2a*1.groovy,**/Sub?ir2File1.groovy'
        analyzer.excludes = '**/Subdir2aFile1.groovy,**/DoesNotExist.*'

        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def fullPaths = results.violations*.message
        assert fullPaths.containsAll([
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])
        assert fullPaths.size() == 2

        assert testCountRule.count == 2
        assert results.getNumberOfFilesWithViolations(3) == 2
        assert results.totalNumberOfFiles == 2
    }

    @Test
    void testGetSourceDirectories_ReturnsListWithBaseDirectory() {
        analyzer.baseDirectory = BASE_DIR
        assert analyzer.sourceDirectories == [BASE_DIR]
    }

    @Test
    void testMatches() {
        def source = new SourceString('def x', 'dir/file.txt')
        assertMatches(source, null, null, true)
        assertMatches(source, '', null, true)
        assertMatches(source, '**/file.txt', null, true)
        assertMatches(source, '**/file.txt', 'other', true)
        assertMatches(source, null, 'other', true)

        assertMatches(source, '**/file.txt', '**/file.txt', false)
        assertMatches(source, null, '**/file.txt', false)
        assertMatches(source, '**/OTHER.*', '', false)
    }

    @Before
    void setUpFilesystemSourceAnalyzerTest() {
        analyzer = new FilesystemSourceAnalyzer()
        testCountRule = new FakeCountRule()
        ruleSet = new ListRuleSet([new FakePathRule(), testCountRule])
    }

    private void assertMatches(source, includes, excludes, shouldMatch) {
        analyzer.includes = includes
        analyzer.excludes = excludes
        analyzer.initializeWildcardPatterns()
        assert analyzer.matches(source) == shouldMatch
    }

    private List resultsPaths(Results results, List paths=[]) {
        if (results.path) {
            paths << results.path
        }
        results.children.each { child -> resultsPaths(child, paths) }
        log("resultsPaths=$paths")
        paths
    }

    private List childResultsClasses(Results results) {
        results.children*.getClass() 
    }
}
