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

import org.codenarc.rule.AbstractRule
import org.codenarc.source.SourceCode
import org.codenarc.rule.Violation

/**
 * Rule that checks for a duplicate import
 *
 * NOTE: Does not distinguish between multiple duplicate imports of the same class.
 * Thus, it may produce multiple violations with the same line number in that case.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class DuplicateImportRule extends AbstractRule {
    String name = 'DuplicateImport'
    int priority = 3

    private static final IMPORT_PATTERN = /\s*import\s+(\w+(\.\w+)*)\b.*/

    void applyTo(SourceCode sourceCode, List violations) {
        def importNames = [] as Set

        def firstClassDeclarationLine = findLineNumberOfFirstClassDeclaration(sourceCode)

        for(int index=0; index < firstClassDeclarationLine; index++) {

            def line = sourceCode.lines[index]
            def lineNumber = index + 1
            def importMatcher = line =~ IMPORT_PATTERN
            if (importMatcher) {
                def importName = importMatcher[0][1]
                if (importNames.contains(importName)) {
                    violations.add(new Violation(rule:this, sourceLine:line.trim(), lineNumber:lineNumber))
                }
                else {
                    importNames.add(importName)
                }
            }
        }
    }

    /**
     * Optimization: Stop checking lines for imports once a class/interface has been declared
     */
    private findLineNumberOfFirstClassDeclaration(SourceCode sourceCode) {
       int firstLineNumber = sourceCode.lines.size()
       def ast = sourceCode.ast
        ast?.classes.each { classNode ->
            if (classNode.lineNumber >= 0 && classNode.lineNumber < firstLineNumber) {
                firstLineNumber = classNode.lineNumber
            }
        }
        firstLineNumber
    }
}