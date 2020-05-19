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

import org.codehaus.groovy.ast.PackageNode
import org.codenarc.rule.AbstractRule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Makes sure there is a blank line after the package statement of a source code file.
 *
 * @author Joe Sondow
 */
class MissingBlankLineAfterPackageRule extends AbstractRule {

    String name = 'MissingBlankLineAfterPackage'
    int priority = 3

    @Override
    void applyTo(SourceCode sourceCode, List<Violation> violations) {
        PackageNode packageNode = sourceCode.ast?.package
        if (packageNode) {
            String sourceCodeLine = sourceCode.line(packageNode.lastLineNumber)
            if (sourceCodeLine != null && !sourceCodeLine.isEmpty()) {
                violations.add(createViolation(packageNode.lineNumber, sourceCodeLine,
                    "Missing blank line after package statement in file $sourceCode.name"))
            }
        }
    }
}
