/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.rule.AbstractAstVisitor

/**
 * Abstract superclass for AstVisitor classes dealing with space around braces
 *
 * @author Chris Mair
 */
abstract class AbstractSpaceAroundBraceAstVisitor extends AbstractAstVisitor {

    protected String sourceLineOrEmpty(node) {
        node.lineNumber == -1 ? '' : sourceLine(node)
    }

    protected String lastSourceLineOrEmpty(node) {
        node.lastLineNumber == -1 ? '' : lastSourceLine(node)
    }

    /**
     * Return true if the specified (1-based) index is valid and the character at that index is not a whitespace character
     * @param line - the source line to be checked
     * @param index - the 1-based index of the character to be checked
     * @return true only if the character is not a whitespace character
     */
    protected boolean isNotWhitespace(String line, int index) {
        index >= 1 && index <= line.size() && !Character.isWhitespace(line[index - 1] as char)
    }

    protected boolean isNotCharacter(String line, char c, int index) {
        index >= 1 && index <= line.size() && line[index - 1] as char != c
    }

    protected int indexOfClosingBrace(String line, int blockLastColumn) {
        int index = blockLastColumn - 2
        while(index >= 0) {
            if (line[index] == '}') {
                return index
            }
            index--
        }
        return index
    }

}
