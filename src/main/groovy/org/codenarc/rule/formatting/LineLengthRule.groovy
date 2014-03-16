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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.source.SourceCode

/**
 * Checks the maximum length for each line of source code. It checks for number of characters, so lines that include
 * tabs may appear longer than the allowed number when viewing the file. The maximum line length can be configured by
 * setting the length property, which defaults to 120.
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class LineLengthRule extends AbstractAstVisitorRule {
    String name = 'LineLength'
    int priority = 2
    int length = 120 // The default max line length. Can be overridden
    boolean ignoreImportStatements = true // import statements can be longer than the max line legnth
    boolean ignorePackageStatements = true // import statements can be longer than the max line legnth

    @Override
    void applyTo(SourceCode sourceCode, List violations) {

        int lineNumber = 0
        for (line in sourceCode.getLines()) {
            lineNumber++
            if (sourceViolatesLineLengthRule(line)) {
                violations << createViolation(lineNumber, line, "The line exceeds $length characters. The line is ${line.length()} characters.")
            }
        }
    }

    private Boolean sourceViolatesLineLengthRule(String line) {
        lineExceedsMaxLength(line) && (flagIfImport(line) || flagIfPackage(line) || flagIfRegularLine(line))
    }

    private Boolean flagIfImport(String line) {
        (sourceLineIsImport(line) && !ignoreImportStatements)
    }

    private Boolean flagIfPackage(String line) {
        (sourceLineIsPackage(line) && !ignorePackageStatements)
    }

    private Boolean flagIfRegularLine(String line) {
        !sourceLineIsImport(line) && !sourceLineIsPackage(line)
    }

    private Boolean sourceLineIsImport(String line) {
        line.trim().startsWith('import ')
    }

    private Boolean sourceLineIsPackage(String line) {
        line.trim().startsWith('package ')
    }

    private Boolean lineExceedsMaxLength(String line) {
        line.length() > length
    }
}
