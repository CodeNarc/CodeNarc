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

import groovy.grape.GrabAnnotationTransformation
import org.apache.log4j.Logger
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codenarc.analyzer.SuppressionAnalyzer

/**
 * Abstract superclass for SourceCode implementations
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
@SuppressWarnings('AbstractClassWithoutAbstractMethod')
abstract class AbstractSourceCode implements SourceCode {
    static final LOG = Logger.getLogger(AbstractSourceCode)
    static final SEPARATOR_PROP = 'file.separator'
    private ModuleNode ast
    private List lines
    private astParsed = false
    private final initLock = new Object()
    private Map<ClassNode, List<MethodCallExpression>> methodCallExpressions
    SuppressionAnalyzer suppressionAnalyzer

    /**
     * Setter exists to avoid circular dependency.
     * @param suppressionAnalyzer suppression analyzer
     */
    protected void setSuppressionAnalyzer(SuppressionAnalyzer suppressionAnalyzer) {
        this.suppressionAnalyzer = suppressionAnalyzer
    }

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
        init()
        ast
    }

    private void init() {
        synchronized (initLock) {
            if (!astParsed) {
                SourceUnit unit = SourceUnit.create('None', getText())
                CompilationUnit compUnit = new CompilationUnit()
                compUnit.addSource(unit)
                try {
                    removeGrabTransformation(compUnit)
                    compUnit.compile(getAstCompilerPhase())
                    ast = unit.getAST()
                }
                catch (CompilationFailedException e) {
                    logCompilationError(e)
                }
                catch (NoClassDefFoundError e) {
                    logCompilationError(e)
                    LOG.info("Most likely, a lib containing $e.message is missing from CodeNarc's runtime classpath.")
                }

                methodCallExpressions = new ExpressionCollector().getMethodCalls(ast)

                astParsed = true
            }
        }
    }

    private void logCompilationError(Throwable e) {
        LOG.warn("Compilation failed for [${toString()}].")
        if (getAstCompilerPhase() <= DEFAULT_COMPILER_PHASE) {
            LOG.info("Compilation failed because of [${e.class.name}] with message: [$e.message]")
        }
    }

    /**
     * @return compiler phase (as in {@link org.codehaus.groovy.control.Phases}) up to which the AST will be processed
     */
    @SuppressWarnings('GetterMethodCouldBeProperty')
    int getAstCompilerPhase() {
        DEFAULT_COMPILER_PHASE
    }

    Map<ClassNode, List<MethodCallExpression>> getMethodCallExpressions() {
        init()
        methodCallExpressions.asImmutable()
    }

    private removeGrabTransformation(CompilationUnit compUnit) {
        compUnit.phaseOperations?.each { List xforms ->
            xforms?.removeAll { entry ->

                entry.getClass().declaredFields.any {
                    it.name == 'val$instance' &&
                            it.type == ASTTransformation
                } && entry.val$instance instanceof GrabAnnotationTransformation
            }
        }
    }

    /**
     * Return the line index for the line containing the character at the specified index within the source code.
     * @param charIndex - the index of the character within the source code (zero-based)
     * @return the line number (one-based) containing the specified character; Return -1 if charIndex is not valid.
     */
    int getLineNumberForCharacterIndex(int charIndex) {
        int lineCount = 1
        def source = getText()
        if (charIndex >= source.size() || charIndex < 0) {
            return -1
        }
        if (charIndex > 0) {
            (charIndex - 1..0).each { index ->
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
