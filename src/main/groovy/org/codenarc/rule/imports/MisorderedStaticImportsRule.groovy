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
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class MisorderedStaticImportsRule extends AbstractImportRule {
    String name = 'MisorderedStaticImports'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        boolean nonStaticFound = false

        eachImportLine(sourceCode) { int lineNumber, String line ->
            nonStaticFound = nonStaticFound || line =~ NON_STATIC_IMPORT_PATTERN

            if (nonStaticFound && line =~ STATIC_IMPORT_PATTERN) {
                violateStaticImport(line, lineNumber, violations)
            }
        }
    }

    private void violateStaticImport(line, lineNumber, violations) {
        violations.add(new Violation(rule: this, sourceLine: line.trim(), lineNumber: lineNumber))
    }
}
