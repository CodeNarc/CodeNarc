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
    static final FILE_SEPARATOR = System.getProperty("file.separator")
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
        return lines
    }

    /**
     * Get the trimmed line at the specified index
     * @param lineNumber - the line number; may be negative
     * @return the trimmed line at the specified index, or null if lineNumber is not valid
     */
    String line(int lineNumber) {
        def allLines = getLines()
        return (lineNumber >= 0) && lineNumber < allLines.size() ? 
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
        return ast
    }

    /**
     * Return the normalized value of the specified path. Convert file separator chars to standard '/'.
     * @param path - the path to normalize
     * @return the normalized value
     */
    protected String normalizePath(String path) {
        final SEP = '/' as char
        char separatorChar = System.getProperty("file.separator").charAt(0)
        return (separatorChar != SEP) ? path.replace(separatorChar, SEP) : path
    }

}