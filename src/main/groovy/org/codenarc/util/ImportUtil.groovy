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

import org.codehaus.groovy.ast.ClassNode
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
        if (importNode.className) {
            def importClassName = importNode.className
            def index = importClassName.lastIndexOf('.')
            (index == -1) ? '' : importClassName[0..index - 1]
        }
        else {
            def packageName = importNode.packageName
            packageName.endsWith('.') ? packageName[0..-2] : packageName
        }
    }

    /**
     * Return the source line and line number for the specified import class name and alias
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static Map sourceLineAndNumberForImport(SourceCode sourceCode, String className, String alias) {
        // NOTE: This won't properly handle the case of multiple imports for same class if not all are aliased
        def index = sourceCode.lines.findIndexOf { line ->
            if (!line.contains('import')) { return false }
            if (!line.contains(alias)) { return false }
            def classNameIndex = line.indexOf(className)
            if (classNameIndex == -1) { return false }
            def afterIndex = classNameIndex + className.size()
            if (afterIndex >= line.size()) { return true }    // the className is the last thing on the line
            if (line[afterIndex..-1].startsWith('.' + alias)) { return true }   // className.<member>
            return line[afterIndex] =~ /[\/\s\;]/
        }
        def lineNumber = index == -1 ? null : index + 1
        def sourceLine = lineNumber == null ? "import $className as $alias".toString() : sourceCode.lines[lineNumber - 1].trim()
        [sourceLine: sourceLine, lineNumber: lineNumber]
    }

    /**
     * Return the source line and line number for the specified import class name and alias
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static Map sourceLineAndNumberForStarImport(SourceCode sourceCode, ImportNode importNode) {
        if (!importNode.isStar()) {
            return [sourceLine: -1, lineNumber: -1]
        }
        // NOTE: This won't properly handle the case of multiple imports for same class if not all are aliased
        def index = sourceCode.lines.findIndexOf { line ->
            line.contains('import') &&
                (line.contains(importNode.packageName + '*') || (importNode.className && line.contains(importNode.className)))
        }
        def lineNumber = index == -1 ? null : index + 1
        def sourceLine = lineNumber == null ? "import ${importNode.packageName}*".toString() : sourceCode.lines[lineNumber - 1].trim()
        [sourceLine: sourceLine, lineNumber: lineNumber]
    }

    static Map sourceLineAndNumberForNonStarImport(SourceCode sourceCode, ImportNode importNode) {
        ClassNode importClassNode = importNode.type
        if (importClassNode && importClassNode.lineNumber && importClassNode.lineNumber != -1) {
            int lineNumber = importClassNode.lineNumber
            String sourceLine = sourceCode.line(lineNumber - 1) ?: importNode.text
            return [sourceLine: sourceLine, lineNumber: lineNumber]
        }
        return sourceLineAndNumberForImport(sourceCode, importNode.className, importNode.alias)
    }

    /**
     * Return the source line and line number for the specified import
     * @param sourceCode - the SourceCode being processed
     * @param importNode - the ImportNode representing the import
     * @return an object that has 'sourceLine' and 'lineNumber' fields
     */
    static Map sourceLineAndNumberForImport(SourceCode sourceCode, ImportNode importNode) {
        if (importNode.isStar()) {
            sourceLineAndNumberForStarImport(sourceCode, importNode)
        } else {
            sourceLineAndNumberForNonStarImport(sourceCode, importNode)
        }
    }

    static List getImportsSortedByLineNumber(sourceCode) {
        def staticImports = sourceCode.ast.staticImports.values() + sourceCode.ast.staticStarImports.values()
        def allImports = sourceCode.ast.imports + sourceCode.ast.starImports + staticImports
        return sortImportsByLineNumber(allImports, sourceCode)
    }

    static List getNonStaticImportsSortedByLineNumber(sourceCode) {
        def allImports = sourceCode.ast.imports + sourceCode.ast.starImports
        return sortImportsByLineNumber(allImports, sourceCode)
    }

    private static List sortImportsByLineNumber(List imports, sourceCode) {
        imports.sort { importNode ->
            def importInfo = ImportUtil.sourceLineAndNumberForImport(sourceCode, importNode)
            importInfo.lineNumber
        }
    }

}
