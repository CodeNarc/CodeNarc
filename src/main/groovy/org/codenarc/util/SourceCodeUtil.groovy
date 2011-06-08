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
package org.codenarc.util

import org.codehaus.groovy.ast.ASTNode
import org.codenarc.source.SourceCode

/**
 * Contains source related static utility methods
 *
 * @author Marcin Erdmann
 */
class SourceCodeUtil {
    static List<String> nodeSourceLines(SourceCode source, ASTNode node) {
        sourceLinesBetween(source, node.lineNumber, node.columnNumber, node.lastLineNumber, node.lastColumnNumber)
    }

    static List<String> sourceLinesBetweenNodes(SourceCode source, ASTNode startNode, ASTNode endNode) {
        sourceLinesBetween(source, startNode.lastLineNumber, startNode.lastColumnNumber, endNode.lineNumber, endNode.columnNumber)
    }

    /**
     * Retrieves source lines between the start line and column and end line and column. Lines and columns are counted
     * starting at one as do the lines and columns describing a beginning and end of ASTNode.
     *
     * @param source Source from which the lines are to be extracted
     * @param startLine has to be greater than zero
     * @param startColumn has to be greater than zero
     * @param endLine has to be greater than zero
     * @param endColumn has to be greater than zero
     * @return a List of string containing the extracted code
     * @throws IllegalArgumentException when start is not before end, and any of the start/end parameters is lower than one
     */
    static List<String> sourceLinesBetween(SourceCode source, int startLine, int startColumn, int endLine, int endColumn) {
        if (startLine < 1 || startColumn < 1 || endLine < 1 || endColumn < 1) {
            throw new IllegalArgumentException('Start and end indexes are one based and have to be greater than zero')
        }
        if (endLine < startLine || (endLine == startLine && endColumn < startColumn)) {
            throw new IllegalArgumentException('End line/column has to be after start line/column')
        }
        def nodeLines = source.lines[(startLine - 1)..(endLine - 1)] as ArrayList
        if (nodeLines.size() > 1) {
            nodeLines[0] = startColumn - 1 == nodeLines.first().size() ? '' : nodeLines.first()[(startColumn - 1)..-1]
            nodeLines[-1] = nodeLines.last()[0..(endColumn - 2)]
            return nodeLines
        }
        return [nodeLines.first()[(startColumn - 1)..(endColumn - 2)]]
    }
}
