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
package org.codenarc.rule.imports

import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Rule that checks for a duplicate import
 *
 * NOTE: Does not distinguish between multiple duplicate imports of the same class.
 * Thus, it may produce multiple violations with the same line number in that case.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class DuplicateImportRule extends AbstractImportRule {
    String name = 'DuplicateImport'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        def importNames = [] as Set
        def staticImportNames = [] as Set

        eachImportLine(sourceCode) { int lineNumber, String line ->
            checkImport(line, lineNumber, importNames, violations)
            checkStaticImport(line, lineNumber, staticImportNames, violations)
        }
    }

    private void checkImport(line, lineNumber, importNames, violations) {
        def importMatcher = line =~ NON_STATIC_IMPORT_PATTERN
        if (importMatcher) {
            def importName = importMatcher[0][1]
            if (importNames.contains(importName)) {
                violations.add(new Violation(rule: this, sourceLine: line.trim(), lineNumber: lineNumber))
            }
            else {
                importNames.add(importName)
            }
        }
    }

    private void checkStaticImport(line, lineNumber, staticImportNames, violations) {
        def importMatcher = line =~ STATIC_IMPORT_PATTERN
        if (importMatcher) {
            def staticImportName = importMatcher[0][1]
            if (staticImportNames.contains(staticImportName)) {
                violations.add(new Violation(rule: this, sourceLine: line.trim(), lineNumber: lineNumber))
            }
            else {
                staticImportNames.add(staticImportName)
            }
        }
    }
}
