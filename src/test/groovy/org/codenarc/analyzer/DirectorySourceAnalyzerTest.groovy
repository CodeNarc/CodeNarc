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

import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.assertEqualSets
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import org.codenarc.rule.FakePathRule
import org.codenarc.rule.FakeCountRule

/**
 * Tests for DirectorySourceAnalyzer.
 *
 * @author Chris Mair
 */
class DirectorySourceAnalyzerTest extends AbstractTestCase {
    private static final BASE_DIR = '/usr'
    private analyzer
    private ruleSet
    private testCountRule

    @Test
    void testAnalyze_NullRuleSet() {
        analyzer.baseDirectory = BASE_DIR
        shouldFailWithMessageContaining('ruleSet') { analyzer.analyze(null) }
    }

    @Test
    void testAnalyze_BaseDirectoryNullAndSourceDirectoriesNull() {
        shouldFailWithMessageContaining(['baseDirectory', 'sourceDirectories']) { analyzer.analyze(ruleSet) }
    }

    @Test
    void testAnalyze_BaseDirectoryEmptyAndSourceDirectoriesEmpty() {
        analyzer.baseDirectory = ''
        analyzer.sourceDirectories = []
        shouldFailWithMessageContaining(['baseDirectory', 'sourceDirectories']) { analyzer.analyze(ruleSet) }
    }

    @Test
    void testAnalyze_BaseDirectory_FilesOnly() {
        final DIR = 'src/test/resources/source'
        analyzer.baseDirectory = DIR
        def ruleSet = new ListRuleSet([new FakePathRule()])
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
    void testAnalyze_BaseDirectory() {
        final DIR = 'src/test/resources/sourcewithdirs'
        analyzer.baseDirectory = DIR
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

        // Different ordering on different operating systems
//        assert childResultsClasses(top.children[1]) == [FileResults, FileResults]
//        assert childResultsClasses(top.children[2]) == [DirectoryResults, FileResults]
//        assert childResultsClasses(top.children[2].children[0]) == [FileResults]
    }

    @Test
    void testAnalyze_SourceDirectories() {
        final DIR1 = 'src/test/resources/source'
        final DIR2 = 'src/test/resources/sourcewithdirs'
        analyzer.sourceDirectories = [DIR1, DIR2]
        def results = analyzer.analyze(ruleSet)
        log("results=$results")
        def fullPaths = results.violations*.message
        log("fullPaths=$fullPaths")
        assertEqualSets(fullPaths, [
                'src/test/resources/source/SourceFile1.groovy',
                'src/test/resources/source/SourceFile2.groovy',
                'src/test/resources/sourcewithdirs/SourceFile1.groovy',
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File2.groovy',
                'src/test/resources/sourcewithdirs/subdir2/subdir2a/Subdir2aFile1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])
        assert testCountRule.count == 7
        assert results.totalNumberOfFiles == 7
        assert results.getNumberOfFilesWithViolations(3) == 7
    }

    @Test
    void testAnalyze_BaseDirectoryAndSourceDirectories() {
        final SOURCE_DIRS = ['source', 'sourcewithdirs']
        analyzer.baseDirectory = 'src/test/resources'
        analyzer.sourceDirectories = SOURCE_DIRS
        def results = analyzer.analyze(ruleSet)

        def paths = resultsPaths(results)
        log("paths=$paths")

        assertEqualSets(paths, [
                'source',
                'source/SourceFile1.groovy',
                'source/SourceFile2.groovy',
                'sourcewithdirs',
                'sourcewithdirs/SourceFile1.groovy',
                'sourcewithdirs/subdir1',
                'sourcewithdirs/subdir1/Subdir1File1.groovy',
                'sourcewithdirs/subdir1/Subdir1File2.groovy',
                'sourcewithdirs/subdir2',
                'sourcewithdirs/subdir2/subdir2a',
                'sourcewithdirs/subdir2/subdir2a/Subdir2aFile1.groovy',
                'sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])
        assert testCountRule.count == 7
        assertEqualSets(childResultsClasses(results), [DirectoryResults, DirectoryResults, DirectoryResults])
        assert results.totalNumberOfFiles == 7
        assert results.getNumberOfFilesWithViolations(3) == 7
    }

    @Test
    void testAnalyze_BaseDirectory_NoViolations() {
        final DIR = 'src/test/resources/sourcewithdirs'
        analyzer.baseDirectory = DIR
        ruleSet = new ListRuleSet([testCountRule])
        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def paths = resultsPaths(results)
        log("paths=$paths")
        assertEqualSets(paths, ['subdir1', 'subdir2', 'subdir2/subdir2a'])

        assert testCountRule.count == 5
        assert results.getNumberOfFilesWithViolations(3) == 0
        assert results.totalNumberOfFiles == 5
    }

    @Test
    void testAnalyze_BaseDirectory_ApplyToFilesMatching() {
        final DIR = 'src/test/resources/sourcewithdirs'
        analyzer.baseDirectory = DIR
        analyzer.applyToFilesMatching = /.*ubdir.*\.groovy/
        analyzer.doNotApplyToFilesMatching = /.*File2.*/
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
    void testAnalyze_BaseDirectory_ApplyToFileNames() {
        final DIR = 'src/test/resources/sourcewithdirs'
        analyzer.baseDirectory = DIR
        analyzer.applyToFileNames = 'Subdir1File1.groovy,Subdir2a*1.groovy,Sub?ir2File1.groovy'
        analyzer.doNotApplyToFileNames = 'Subdir2aFile1.groovy'
        def results = analyzer.analyze(ruleSet)
        log("results=$results")

        def fullPaths = results.violations*.message
        assertEqualSets(fullPaths, [
                'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
        ])

        assert testCountRule.count == 2
        assert results.getNumberOfFilesWithViolations(3) == 2
        assert results.totalNumberOfFiles == 2
    }

    @Before
    void setUpDirectorySourceAnalyzerTest() {
        analyzer = new DirectorySourceAnalyzer()
        testCountRule = new FakeCountRule()
        ruleSet = new ListRuleSet([new FakePathRule(), testCountRule])
    }

    private List resultsPaths(Results results, List paths=[]) {
        if (results.path) {
            paths << results.path
        }
        results.children.each { child -> resultsPaths(child, paths) }
        paths
    }

    private List childResultsClasses(Results results) {
        results.children*.getClass() 
    }
}
