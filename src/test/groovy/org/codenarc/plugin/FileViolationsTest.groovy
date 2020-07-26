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
package org.codenarc.plugin

import org.codenarc.results.FileResults
import org.codenarc.rule.Violation
import org.codenarc.source.SourceString
import org.junit.Test

/**
 * Unit tests for FileViolations
 */
class FileViolationsTest {

    private static final String SOURCE = 'println 123'
    private static final String PATH = '/some/path/File.txt'
    private static final String NAME = 'File.txt'
    private static final SourceString SOURCE_CODE = new SourceString(SOURCE, PATH, NAME)
    private static final List<Violation> VIOLATIONS = [new Violation(lineNumber:7)]

    @Test
    void test_Constructor() {
        FileResults fileResults = new FileResults(PATH, VIOLATIONS, SOURCE_CODE)
        FileViolations fileViolations = new FileViolations(fileResults)

        assert fileViolations.path == PATH
        assert fileViolations.fileName == NAME
        assert fileViolations.sourceText == SOURCE
        assert fileViolations.violations == VIOLATIONS
    }

    @Test
    void test_Constructor_NullSourceCode() {
        FileResults fileResults = new FileResults(PATH, VIOLATIONS)
        FileViolations fileViolations = new FileViolations(fileResults)

        assert fileViolations.path == PATH
        assert fileViolations.fileName == ''
        assert fileViolations.sourceText == ''
        assert fileViolations.violations == VIOLATIONS
    }
}
