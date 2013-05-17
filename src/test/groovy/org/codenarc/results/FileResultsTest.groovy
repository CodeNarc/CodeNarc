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
package org.codenarc.results

import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for the FileResults class
 *
 * @author Chris Mair
 */
class FileResultsTest extends AbstractTestCase {

    private static final PATH = '/src/main/MyFile.groovy'
    private static final VIOLATION1 = new Violation(rule:new StubRule(1))
    private static final VIOLATION2 = new Violation(rule:new StubRule(2))
    private static final VIOLATION3 = new Violation(rule:new StubRule(3))
    private static final VIOLATION4 = new Violation(rule:new StubRule(4))
    private static final VIOLATION7 = new Violation(rule:new StubRule(7))

    @Test
    void testWithNoViolations() {
        def results = new FileResults(PATH, [])
        assert results.path == PATH
        assert results.children == []
        assert results.violations == []

        assert results.getNumberOfViolationsWithPriority(1) == 0
        assert results.getNumberOfViolationsWithPriority(2) == 0
        assert results.getNumberOfViolationsWithPriority(3) == 0

        assert results.getNumberOfFilesWithViolations(1) == 0
        assert results.totalNumberOfFiles == 1
        assert results.isFile()
    }

    @Test
    void testWithViolations() {
        def results = new FileResults(PATH, [VIOLATION1, VIOLATION3, VIOLATION7, VIOLATION3, VIOLATION1, VIOLATION2, VIOLATION4])
        assert results.children == []
        assert results.getViolations() == [VIOLATION1, VIOLATION3, VIOLATION7, VIOLATION3, VIOLATION1, VIOLATION2, VIOLATION4]

        assert results.violations.findAll { v -> v.rule.priority == 1 } == [VIOLATION1, VIOLATION1]
        assert results.violations.findAll { v -> v.rule.priority == 2 } == [VIOLATION2]
        assert results.violations.findAll { v -> v.rule.priority == 3 } == [VIOLATION3, VIOLATION3]

        assert results.getNumberOfViolationsWithPriority(1) == 2
        assert results.getNumberOfViolationsWithPriority(2) == 1
        assert results.getNumberOfViolationsWithPriority(3) == 2
        assert results.getNumberOfViolationsWithPriority(4) == 1
        assert results.getNumberOfViolationsWithPriority(7) == 1

        assert results.getNumberOfFilesWithViolations(1) == 1
        assert results.totalNumberOfFiles == 1
    }

    @Test
    void testGetNumberOfFilesWithViolations_IgnoresViolationsWithHigherPriority() {
        def results = new FileResults(PATH, [VIOLATION3])
        assert results.getNumberOfFilesWithViolations(3) == 1
        assert results.getNumberOfFilesWithViolations(2) == 0
        assert results.getNumberOfFilesWithViolations(1) == 0
    }

    @Test
    void testFindResultsForPath() {
        def results = new FileResults(PATH, [])
        assert results.findResultsForPath(null) == null
        assert results.findResultsForPath('xx/yy') == null
        assert results.findResultsForPath(PATH) == results
    }

    @Test
    void testGetViolations_ReturnsDefensiveCopy() {
        def results = new FileResults(PATH, [VIOLATION1, VIOLATION3])
        results.getViolations() << 123
        assert results.getViolations() == [VIOLATION1, VIOLATION3]
    }

}
