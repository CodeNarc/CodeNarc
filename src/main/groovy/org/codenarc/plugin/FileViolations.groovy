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

import groovy.transform.ToString
import org.codenarc.results.FileResults
import org.codenarc.rule.Violation

/**
 * Represents the violations and associated results metadata for a single file
 */
@ToString(includeNames=true)
class FileViolations {

    String path
    String fileName
    String sourceText
    List<Violation> violations

    FileViolations(FileResults fileResults) {
        this.path = fileResults.path
        this.violations = fileResults.rawViolations
        this.fileName = fileResults.sourceCode?.name ?: ''
        this.sourceText = fileResults.sourceCode?.text ?: ''
    }

}
