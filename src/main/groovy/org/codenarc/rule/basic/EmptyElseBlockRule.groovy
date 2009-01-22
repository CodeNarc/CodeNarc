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

import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.stmt.IfStatement

/**
 * Rule that checks for empty else blocks
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class EmptyElseBlockRule extends AbstractAstVisitorRule {
    String id = 'EmptyElseBlock'
    int priority = 2
    Class astVisitorClass = EmptyElseBlockAstVisitor
}

class EmptyElseBlockAstVisitor extends AbstractAstVisitor  {
    void visitIfElse(IfStatement ifStatement) {
        if (isEmptyBlock(ifStatement.elseBlock)) {
            addViolation(ifStatement.elseBlock)
        }
        super.visitIfElse(ifStatement)
    }

}