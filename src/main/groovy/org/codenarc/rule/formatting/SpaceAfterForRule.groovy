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

import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.stmt.ForStatement

/**
 * Check that there is exactly one space (blank) after the for keyword and before the opening parenthesis.
 *
 * @author Chris Mair
 */
class SpaceAfterForRule extends AbstractAstVisitorRule {

    String name = 'SpaceAfterFor'
    int priority = 3
    Class astVisitorClass = SpaceAfterForAstVisitor
}

class SpaceAfterForAstVisitor extends AbstractSingleSpaceAfterKeywordAstVisitor {

    @Override
    void visitForLoop(ForStatement forLoop) {
        checkForSingleSpaceAndOpeningParenthesis(forLoop, 'for')
        super.visitForLoop(forLoop)
    }

}
