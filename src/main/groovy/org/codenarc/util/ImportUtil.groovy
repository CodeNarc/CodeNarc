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
 package org.codenarc.util

import org.codehaus.groovy.ast.ImportNode
import org.codenarc.source.SourceCode

/**
 * Contains static utility methods and constants related to Import statements.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 */
class ImportUtil {

    /**
     * Return the package name for the specified import statement or else an empty String
     * @param importNode - the ImportNode for the import
     * @return the name package being imported (i.e., the import minus the class name/spec)
     *      or an empty String if the import contains no package component
     */
    static String packageNameForImport(ImportNode importNode) {
        if (importNode.isStar()) {
            def packageName = importNode.packageName
            packageName.endsWith('.') ? packageName[0..-2] : packageName
        }
        else {
            def importClassName = importNode.className
            def index = importClassName.lastIndexOf('.')
            (index == -1) ? '' : importClassName.substring(0, index)
        }
    }

    /**
     * Return the source line and line number for the specified import class name and alias
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static sourceLineAndNumberForImport(SourceCode sourceCode, String className, String alias) {
        // NOTE: This won't properly handle the case of multiple imports for same class if not all are aliased
        def index = sourceCode.lines.findIndexOf { line ->
            line.contains('import') &&
                line.contains(className) &&
                line.contains(alias)
        }
        def lineNumber = index == -1 ? null : index + 1
        def sourceLine = lineNumber == null ? "import $className as $alias".toString() : sourceCode.lines[lineNumber-1].trim()
        [sourceLine:sourceLine, lineNumber:lineNumber]
    }

    /**
     * Return the source line and line number for the specified import class name and alias
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static sourceLineAndNumberForStarImport(SourceCode sourceCode, ImportNode importNode) {
        if (!importNode.isStar()) {
            return [sourceLine:-1, lineNumber:-1]
        }
        // NOTE: This won't properly handle the case of multiple imports for same class if not all are aliased
        def index = sourceCode.lines.findIndexOf { line ->
            line.contains('import') &&
                line.contains(importNode.packageName + '*')
        }
        def lineNumber = index == -1 ? null : index + 1
        def sourceLine = lineNumber == null ? "import ${importNode.packageName}*".toString() : sourceCode.lines[lineNumber-1].trim()
        [sourceLine:sourceLine, lineNumber:lineNumber]
    }

    /**
     * Return the source line and line number for the specified import
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static sourceLineAndNumberForImport(SourceCode sourceCode, ImportNode importNode) {
        if (importNode.isStar()) {
            sourceLineAndNumberForStarImport(sourceCode, importNode)
        } else {
            sourceLineAndNumberForImport(sourceCode, importNode.className, importNode.alias)
        }
    }

    static List getImportsSortedByLineNumber(sourceCode) {
        def allImports = sourceCode.ast.imports + sourceCode.ast.starImports
        allImports.sort { importNode ->
            def importInfo = ImportUtil.sourceLineAndNumberForImport(sourceCode, importNode)
            importInfo.lineNumber
        }
    }


}
