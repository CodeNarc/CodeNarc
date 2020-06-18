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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.ImportUtil

/**
 * Makes sure there is a blank line after the imports of a source code file.
 */
class MissingBlankLineAfterImportsRule extends AbstractRule {

    String name = 'MissingBlankLineAfterImports'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        def imports = ImportUtil.getAllImports(sourceCode)
        if (imports) {
            int lastImportLineNumber = imports*.lastLineNumber.max()

            if (lastImportLineNumber > 0) {
                String nextLine = sourceCode.line(lastImportLineNumber)
                if (nextLine && !nextLine.trim().isEmpty()) {
                    violations.add(createViolation(lastImportLineNumber + 1, nextLine,
                            "Missing blank line after imports in file $sourceCode.name"))
                }
            }
        }
    }
}
