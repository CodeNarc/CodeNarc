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
package org.codenarc.source

import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.CompilationFailedException
import org.apache.log4j.Logger

/**
 * Abstract superclass for SourceCode implementations
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractSourceCode implements SourceCode {
    static final LOG = Logger.getLogger(AbstractSourceCode)
    static final SEPARATOR_PROP = 'file.separator'
    static final FILE_SEPARATOR = System.getProperty(SEPARATOR_PROP)
    private ModuleNode ast
    private List lines
    private astParsed = false

    /**
     * @return the List of lines of the source code (with line terminators removed)
     */
    List getLines() {
        if (lines == null) {
            lines = new StringReader(getText()).readLines()
        }
        lines
    }

    /**
     * Get the trimmed line at the specified index
     * @param lineNumber - the zero-based line number; may be negative
     * @return the trimmed line at the specified index, or null if lineNumber is not valid
     */
    String line(int lineNumber) {
        def allLines = getLines()
        (lineNumber >= 0) && lineNumber < allLines.size() ?
            allLines[lineNumber].trim() : null
    }

    /**
     * Return the Groovy AST (Abstract Syntax Tree) for this source file
     * @return the ModuleNode representing the AST for this source file
     */
    ModuleNode getAst() {
        if (!astParsed) {
            SourceUnit unit = SourceUnit.create("None", getText())
            CompilationUnit compUnit = new CompilationUnit()
            compUnit.addSource(unit)
            try {
                compUnit.compile(Phases.CONVERSION)
                ast = unit.getAST()
            }
            catch(CompilationFailedException e) {
                LOG.warn("Compilation failed for [${toString()}]")
            }
            astParsed = true
        }
        ast
    }

    /**
     * Return the line index for the line containing the character at the specified index within the source code.
     * @param charIndex - the index of the character within the source code (zero-based)
     * @return the line number (one-based) containing the specified character; Return -1 if charIndex is not valid.
     */
    int getLineNumberForCharacterIndex(int charIndex) {
        int lineCount = 0
        def source = getText()
        if (charIndex >= source.size() || charIndex < 0) {
            return -1
        }
        if (charIndex > 0) {
            (charIndex-1..0).each { index ->
                def ch = source[index]
                if (ch == '\n') {
                    lineCount++
                }
            }
        }
        lineCount
    }

    /**
     * Return true if and only if the source code can be successfully compiled
     * @return true only if the source code is valid
     */
    boolean isValid() {
        return getAst()
    }
    
    /**
     * Return the normalized value of the specified path. Convert file separator chars to standard '/'.
     * @param path - the path to normalize
     * @return the normalized value
     */
    protected String normalizePath(String path) {
        final SEP = '/' as char
        char separatorChar = System.getProperty(SEPARATOR_PROP).charAt(0)
        (separatorChar == SEP) ? path : path.replace(separatorChar, SEP)
    }

}