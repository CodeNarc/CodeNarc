/*
 * Copyright 2022 the original author or authors.
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

import static org.codenarc.test.TestUtil.assertEqualSets
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.results.DirectoryResults
import org.codenarc.results.FileResults
import org.codenarc.results.Results
import org.codenarc.rule.FakeCountRule
import org.codenarc.rule.FakePathRule
import org.codenarc.rule.MockRule
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Tests for FilesSourceAnalyzer.
 *
 * @author Nicolas Vuillamy
 * @author Chris Mair
 */
class FilesSourceAnalyzerTest extends AbstractTestCase {

    private static final BASE_DIR = 'src/test/resources/sourcewithdirs'
    private static final String SOURCE_FILE = 'SourceFile1.groovy'
    private static final String SOURCE_FILE_RELATIVE_PATH = BASE_DIR + '/' + SOURCE_FILE

    private analyzer = new FilesSourceAnalyzer()
    private testCountRule = new FakeCountRule()
    private ruleSet = new ListRuleSet([new FakePathRule(), testCountRule])

    @Nested
    class Analyze {

        @BeforeEach
        void beforeEach() {
            analyzer.baseDirectory = BASE_DIR
        }

        @Test
        void NullRuleSet() {
            analyzer.sourceFiles = SOURCE_FILE
            shouldFailWithMessageContaining('ruleSet') { analyzer.analyze(null) }
        }

        @Test
        void OneFile() {
            analyzer.sourceFiles = [SOURCE_FILE]
            def results = analyzer.analyze(ruleSet)

            def paths = resultsPaths(results)
            assertEqualSets(paths, [SOURCE_FILE])

            assertFakePathRuleViolations(results, [SOURCE_FILE_RELATIVE_PATH])
            assertNumberOfFilesAndViolations(results, 1)
        }

        @Test
        void BaseDirectory_DefaultToCurrentDirectory() {
            analyzer = new FilesSourceAnalyzer()    // use default baseDirectory
            analyzer.sourceFiles = [SOURCE_FILE_RELATIVE_PATH]
            def results = analyzer.analyze(ruleSet)

            assertFakePathRuleViolations(results, [SOURCE_FILE_RELATIVE_PATH])
            assertNumberOfFilesAndViolations(results, 1)
        }

        @Test
        void SourceFile_AbsolutePath() {
            String absoluteFilePath = new File(BASE_DIR, SOURCE_FILE).absolutePath
            analyzer.sourceFiles = [absoluteFilePath]
            def results = analyzer.analyze(ruleSet)

            assertFakePathRuleViolations(results, [absoluteFilePath])
            assertNumberOfFilesAndViolations(results, 1)
        }

        @Test
        void SourceFile_DoesNotExist() {
            analyzer.sourceFiles = ['DoesNotExist.groovy']
            shouldFailWithMessageContaining('Unable to find input file') { analyzer.analyze(ruleSet) }
        }

        @Test
        void ErrorAnalyzingFile() {
            def ruleThrowsException = new MockRule(name: 'testRule')    // throws UnsupportedOperationException
            ruleSet = new ListRuleSet([ruleThrowsException])
            analyzer.sourceFiles = [SOURCE_FILE]

            // failOnError = false
            def results = analyzer.analyze(ruleSet)
            assert results.getNumberOfFilesWithViolations(3) == 0
            assert results.totalNumberOfFiles == 0

            // failOnError = true
            analyzer.failOnError = true
            shouldFailWithMessageContaining(['Error analyzing source file', 'UnsupportedOperationException']) { analyzer.analyze(ruleSet) }
        }

        @Test
        void BaseDirectory_AbsolutePath() {
            analyzer.baseDirectory = new File(BASE_DIR).absolutePath
            analyzer.sourceFiles = [SOURCE_FILE]
            def results = analyzer.analyze(ruleSet)

            def paths = resultsPaths(results)
            assertEqualSets(paths, [SOURCE_FILE])

            assertFakePathRuleViolations(results, [
                    analyzer.baseDirectory + '/SourceFile1.groovy'
            ])
            assertNumberOfFilesAndViolations(results, 1)
        }

        @Test
        void MultipleFiles() {
            analyzer.sourceFiles = [
                    SOURCE_FILE,
                    'subdir1/Subdir1File1.groovy',
                    'subdir1/Subdir1File2.groovy'
            ]
            def results = analyzer.analyze(ruleSet)

            assertFakePathRuleViolations(results, [
                    SOURCE_FILE_RELATIVE_PATH,
                    'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                    'src/test/resources/sourcewithdirs/subdir1/Subdir1File2.groovy'
            ])
            assertNumberOfFilesAndViolations(results, 3)

            // Verify that the directory structure is properly reflected within the results
            assert childResultsClasses(results) == [DirectoryResults]
            def top = results.children[0]
            assertEqualSets(childResultsClasses(top), [FileResults, DirectoryResults, DirectoryResults])
        }

        @Test
        void MultipleFilesNestedDirs() {
            analyzer.sourceFiles = [
                    SOURCE_FILE,
                    'subdir1/Subdir1File1.groovy',
                    'subdir1/Subdir1File2.groovy',
                    'subdir2/subdir2a/Subdir2aFile1.groovy',
                    'subdir2/Subdir2File1.groovy'
            ]
            def results = analyzer.analyze(ruleSet)
            log("results=$results")

            assertFakePathRuleViolations(results, [
                    SOURCE_FILE_RELATIVE_PATH,
                    'src/test/resources/sourcewithdirs/subdir1/Subdir1File1.groovy',
                    'src/test/resources/sourcewithdirs/subdir1/Subdir1File2.groovy',
                    'src/test/resources/sourcewithdirs/subdir2/subdir2a/Subdir2aFile1.groovy',
                    'src/test/resources/sourcewithdirs/subdir2/Subdir2File1.groovy'
            ])
            assertNumberOfFilesAndViolations(results, 5)

            // Verify that the directory structure is properly reflected within the results
            assert childResultsClasses(results) == [DirectoryResults]
            def top = results.children[0]
            assertEqualSets(childResultsClasses(top), [FileResults, DirectoryResults, DirectoryResults])
        }

        @Test
        void NoViolations() {
            analyzer.sourceFiles = [
                    SOURCE_FILE,
                    'subdir1/Subdir1File1.groovy',
                    'subdir1/Subdir1File2.groovy',
                    'subdir2/subdir2a/Subdir2aFile1.groovy',
                    'subdir2/Subdir2File1.groovy'
            ]
            ruleSet = new ListRuleSet([testCountRule])
            def results = analyzer.analyze(ruleSet)
            log("results=$results")

            def paths = resultsPaths(results)
            assertEqualSets(paths, [
                    SOURCE_FILE,
                    'subdir1',
                    'subdir1/Subdir1File2.groovy',
                    'subdir1/Subdir1File1.groovy',
                    'subdir2',
                    'subdir2/subdir2a',
                    'subdir2/subdir2a/Subdir2aFile1.groovy',
                    'subdir2/Subdir2File1.groovy'])

            assert testCountRule.count == 5
            assert results.getNumberOfFilesWithViolations(3) == 0
            assert results.totalNumberOfFiles == 5
        }
    }

    private List resultsPaths(Results results, List paths=[]) {
        if (results.path) {
            paths << results.path
        }
        if (results instanceof FileResults) {
            assert results.sourceCode
        }
        results.children.each { child -> resultsPaths(child, paths) }
        log("resultsPaths=$paths")
        return paths
    }

    private List childResultsClasses(Results results) {
        return results.children*.getClass()
    }

    private void assertFakePathRuleViolations(Results results, List<String> expectedPaths) {
        def fullPaths = results.violations*.message
        assertEqualSets(fullPaths, expectedPaths)
    }

    private void assertNumberOfFilesAndViolations(Results results, int expectedNumberOfFiles) {
        assert testCountRule.count == expectedNumberOfFiles
        assert results.getNumberOfFilesWithViolations(3) == expectedNumberOfFiles
        assert results.totalNumberOfFiles == expectedNumberOfFiles
    }

}
