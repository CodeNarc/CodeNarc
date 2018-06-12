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

/**
 * Abstract class with common functionalities for import-related rules.
 *
 * @author Erik Pragt
 * @author Marcin Erdmann
 */
abstract class AbstractImportRule extends AbstractRule {

    public static final String NON_STATIC_IMPORT_PATTERN = /^\s*import(?!\s+static)\s+(\w+(\.\w+)*)\b.*/
    public static final String STATIC_IMPORT_PATTERN = /^\s*import\s+static\s+(\w+(\.\w+)*)\b.*/

    /**
     * Optimization: Stop checking lines for imports once a class/interface has been declared
     */
    protected int findLineNumberOfFirstClassDeclaration(SourceCode sourceCode) {
        int firstLineNumber = sourceCode.lines.size()
        def ast = sourceCode.ast
        ast?.classes.each { classNode ->
            if (classNode.lineNumber >= 0 && classNode.lineNumber < firstLineNumber) {
                firstLineNumber = classNode.lineNumber
            }
        }
        firstLineNumber
    }

    protected void eachImportLine(SourceCode sourceCode, Closure closure) {
        int firstClassDeclarationLine = findLineNumberOfFirstClassDeclaration(sourceCode)
        for(int index = 0; index < firstClassDeclarationLine; index++) {
            def line = sourceCode.lines[index]
            def lineNumber = index + 1
            closure.call(lineNumber, line)
        }
    }

}
