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

import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks that else blocks use braces rather than a single statement.
 * <p>
 * By default, braces are not required for an <code>else</code> if it is followed immediately
 * by an <code>if</code>. Set the <code>bracesRequiredForElseIf<code> property to true to
 * require braces is that situation as well. 
 *
 * @author Chris Mair
 */
class ElseBlockBracesRule extends AbstractAstVisitorRule {
    String name = 'ElseBlockBraces'
    int priority = 2
    boolean bracesRequiredForElseIf = false
    Class astVisitorClass = ElseBlockBracesAstVisitor
}

class ElseBlockBracesAstVisitor extends AbstractAstVisitor  {
    void visitIfElse(IfStatement ifStatement) {
        if (isFirstVisit(ifStatement) && !(ifStatement.elseBlock instanceof EmptyStatement) && 
                !AstUtil.isBlock(ifStatement.elseBlock)) {

            if (!(ifStatement.elseBlock instanceof IfStatement) || rule.bracesRequiredForElseIf) {
                addViolation(ifStatement, 'The else block lacks braces')
            }
        }
        super.visitIfElse(ifStatement)
    }

}
