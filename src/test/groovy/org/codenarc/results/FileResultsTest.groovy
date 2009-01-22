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
package org.codenarc.results

import org.codenarc.results.FileResults
import org.codenarc.rule.StubRule
import org.codenarc.rule.Violation
import org.codenarc.test.AbstractTest

/**
 * Tests for the FileResults class
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class FileResultsTest extends AbstractTest {

    static final PATH = '/src/main/MyFile.groovy'
    static final VIOLATION1 = new Violation(rule:new StubRule(1))
    static final VIOLATION2 = new Violation(rule:new StubRule(2))
    static final VIOLATION3 = new Violation(rule:new StubRule(3))

    void testWithNoViolations() {
        def results = new FileResults(PATH, [])
        assert results.path == PATH
        assert results.children == []
        assert results.getViolationsWithPriority(1) == []
        assert results.getViolationsWithPriority(2) == []
        assert results.getViolationsWithPriority(3) == []

        assert results.getNumberOfViolationsWithPriority(1) == 0
        assert results.getNumberOfViolationsWithPriority(2) == 0
        assert results.getNumberOfViolationsWithPriority(3) == 0

        assert results.numberOfFilesWithViolations == 0
        assert results.totalNumberOfFiles == 1
        assert results.isFile()
    }

    void testWithViolations() {
        def results = new FileResults(PATH, [VIOLATION1, VIOLATION3, VIOLATION3, VIOLATION1, VIOLATION2])
        assert results.children == []
        assert results.getViolationsWithPriority(1) == [VIOLATION1, VIOLATION1]
        assert results.getViolationsWithPriority(2) == [VIOLATION2]
        assert results.getViolationsWithPriority(3) == [VIOLATION3, VIOLATION3]

        assert results.getNumberOfViolationsWithPriority(1) == 2
        assert results.getNumberOfViolationsWithPriority(2) == 1
        assert results.getNumberOfViolationsWithPriority(3) == 2

        assert results.numberOfFilesWithViolations == 1
        assert results.totalNumberOfFiles == 1
    }

    void testFindResultsForPath() {
        def results = new FileResults(PATH, [])
        assert results.findResultsForPath(null) == null
        assert results.findResultsForPath('xx/yy') == null
        assert results.findResultsForPath(PATH) == results
    }

}