/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.stmt.IfStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks for empty else blocks
 *
 * @author Chris Mair
 */
class EmptyElseBlockRule extends AbstractAstVisitorRule {
    String name = 'EmptyElseBlock'
    int priority = 2
    Class astVisitorClass = EmptyElseBlockAstVisitor
}

class EmptyElseBlockAstVisitor extends AbstractAstVisitor  {
    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement) && AstUtil.isEmptyBlock(ifStatement.elseBlock)) {
            addViolation(ifStatement.elseBlock, 'The else block is empty')
        }
        super.visitIfElse(ifStatement)
    }

}
