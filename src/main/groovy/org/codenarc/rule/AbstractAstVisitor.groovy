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
package org.codenarc.rule

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit
import org.codenarc.source.SourceCode

/**
 * Abstract superclass for Groovy AST Visitors used with Rules
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
abstract class AbstractAstVisitor extends ClassCodeVisitorSupport implements AstVisitor {
    static final MAX_SOURCE_LINE_LENGTH = 50
    List violations = []
    Rule rule
    SourceCode sourceCode
    private Set visited = new HashSet()

    /**
     * Check if the AST expression has already been visited
     * @param expression - the AST expression to check
     * @return true if the AST expression has already been visited
     */
    protected isAlreadyVisited(expression) {
        return visited.contains(expression)
    }

    /**
     * Register the AST expression as having been visited
     * @param expression - the AST expression to register
     */
    protected registerAsVisited(expression) {
        visited << expression
    }

    /**
     * Return the source line corresponding to the specified Statement
     * @param statement - the Groovy AST Statement
     */
    protected String sourceLine(ASTNode statement) {
        // TODO Handle statements that cross multiple lines?
        def line = (statement.lineNumber >= 0) ? sourceCode.lines[statement.lineNumber-1] : null
        return formatSourceLine(line, statement.columnNumber-1)
    }

    /**
     * Return the source line corresponding to the line number
     * @param lineNumber - the line number (zero-based)
     */
    protected String sourceLine(int lineNumber) {
        def line = (lineNumber >= 0) ? sourceCode.lines[lineNumber-1] : null
        return formatSourceLine(line)
    }

    /**
     * Add a new Violation to the list of violations found by this visitor.
     * Only add the violation if the node lineNumber >= 0.
     * @param node - the Groovy AST Node
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(ASTNode node, message=null) {
        def lineNumber = node.lineNumber
        if (lineNumber >= 0) {
            def sourceLine = sourceLine(node)
            addViolation(sourceLine, lineNumber, message)
        }
    }

    /**
     * Add a new Violation for the expression to the list of violations found by this visitor
     * @param node - the Groovy AST Node for the expression
     */
    protected void addViolationFromExpression(ASTNode node) {
        addViolation(formatSourceLine(node.text), node.lineNumber)
    }

    /**
     * Add a new Violation to the list of violations found by this visitor
     * @param sourceLine - the sourceLine for the violation
     * @param lineNumber - the line number of the violation
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(String sourceLine, int lineNumber, String message=null) {
        violations.add(new Violation(rule:rule, sourceLine:sourceLine, lineNumber:lineNumber, message:message))
    }

    /**
     * Add a new Violation to the list of violations found by this visitor. Include the full line of source code.
     * @param lineNumber - the line number within the source code causing the violation
     * @param message - the message for the violation; defaults to null
     */
    protected void addViolation(int lineNumber, String message=null) {
        def sourceLine = sourceCode.lines[lineNumber-1]
        def formattedSourceLine = formatSourceLine(sourceLine)
        addViolation(formattedSourceLine, lineNumber, message)
    }

    protected SourceUnit getSourceUnit() {
        return source
    }

    /**
     * Format and trim the source line. If the whole line fits, then include the whole line (trimmed).
     * Otherwise, remove characters from the middle to truncate to the max length.
     * @param sourceLine - the source line to format
     * @param startColumn - the starting column index; used to truncate the line if it's too long; defaults to 1
     * @return the formatted and trimmed source line
     */
    private String formatSourceLine(String sourceLine, int startColumn=1) {
        def source = sourceLine ? sourceLine.trim() : null
        if (source && source.size() > MAX_SOURCE_LINE_LENGTH) {
            def lengthOfFirstSegment = MAX_SOURCE_LINE_LENGTH - 12
            def startIndexOfFirstSegment = startColumn
            def endIndexOfFirstSegment = startIndexOfFirstSegment + lengthOfFirstSegment
            source = sourceLine[startIndexOfFirstSegment..endIndexOfFirstSegment] + '..' + source[-10..-1]
        }
        return source
    }

}