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
package org.codenarc.rule.braces

import org.codehaus.groovy.ast.stmt.ForStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks that for statements use braces rather than a single statement.
 *
 * @author Chris Mair
 */
class ForStatementBracesRule extends AbstractAstVisitorRule {
    String name = 'ForStatementBraces'
    int priority = 2
    Class astVisitorClass = ForStatementBracesAstVisitor
}

class ForStatementBracesAstVisitor extends AbstractAstVisitor  {
    void visitForLoop(ForStatement forStatement) {
        if (isFirstVisit(forStatement) && !AstUtil.isBlock(forStatement.loopBlock)) {
            addViolation(forStatement, 'The for statement lacks braces')
        }
        super.visitForLoop(forStatement)
    }
}
