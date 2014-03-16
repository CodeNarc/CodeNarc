/*
 * Copyright 2014 the original author or authors.
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
 * Wildcard imports, static or otherwise, are not used.
 *
 * @author Kyle Boon
 */
class NoWildcardImportsRule extends AbstractImportRule {
    String name = 'NoWildcardImports'
    int priority = 3

    void applyTo(SourceCode sourceCode, List violations) {
        eachImportLine(sourceCode) { int lineNumber, String line ->
            if (line.trim().endsWith('.*')) {
                violations.add(new Violation(rule: this, sourceLine: line.trim(), lineNumber: lineNumber))
            }
        }
    }
}
