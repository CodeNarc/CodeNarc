/*
 * Copyright 2011 the original author or authors.
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
 * Static imports should be before nonstatic imports
 *
 * @author Erik Pragt
 * @author Marcin Erdmann
 * @author Hamlet D'Arcy
 */
class MisorderedStaticImportsRule extends AbstractImportRule {
    private static final String COMES_BEFORE_MESSAGE = 'Static imports should appear before normal imports'
    private static final String COMES_AFTER_MESSAGE = 'Normal imports should appear before static imports'
    String name = 'MisorderedStaticImports'
    int priority = 3
    boolean comesBefore = true

    void applyTo(SourceCode sourceCode, List violations) {
        if (comesBefore) {
            violations.addAll addOrderingViolations(sourceCode, NON_STATIC_IMPORT_PATTERN, STATIC_IMPORT_PATTERN, COMES_BEFORE_MESSAGE)
        } else {
            violations.addAll addOrderingViolations(sourceCode, STATIC_IMPORT_PATTERN, NON_STATIC_IMPORT_PATTERN, COMES_AFTER_MESSAGE)
        }
    }

    private addOrderingViolations(SourceCode sourceCode, String earlyPattern, String latePattern, String message) {
        List violations = []
        boolean nonStaticFound = false

        eachImportLine(sourceCode) { int lineNumber, String line ->
            nonStaticFound = nonStaticFound || line =~ earlyPattern

            if (nonStaticFound && line =~ latePattern) {
                violations << new Violation(rule: this, sourceLine: line.trim(), lineNumber: lineNumber, message: message)
            }
        }
        violations
    }

}
