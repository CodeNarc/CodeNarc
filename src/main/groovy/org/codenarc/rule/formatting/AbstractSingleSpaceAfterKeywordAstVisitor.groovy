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

import org.codehaus.groovy.ast.ASTNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

/**
 * Abstract superclass for AstVisitor classes that checks that there is exactly one space (blank) after a keyword
 * and before the opening parenthesis.
 *
 * @author Chris Mair
 */

class AbstractSingleSpaceAfterKeywordAstVisitor extends AbstractAstVisitor {

    protected void checkForSingleSpaceAndOpeningParenthesis(ASTNode node, String keyword) {
        if (AstUtil.isFromGeneratedSourceCode(node)) {
            return
        }
        def line = sourceLine(node)
        int col = node.columnNumber
        int keywordSize = keyword.size()
        if (line[col + keywordSize - 1] != ' ' || line.size() < col + keywordSize || line[col + keywordSize] != '(') {
            addViolation(node, "The $keyword keyword within class $currentClassName is not followed by a single space")
        }
    }

}
