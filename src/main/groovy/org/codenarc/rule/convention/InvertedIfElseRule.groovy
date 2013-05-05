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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * An inverted if-else statement is one in which there is a single if statement with a single else branch and the
 * boolean test of the if is negated. For instance "if (!x) false else true". It is usually clearer to write this as
 * "if (x) true else false".
 *
 * @author Hamlet D'Arcy
 */
class InvertedIfElseRule extends AbstractAstVisitorRule {

    String name = 'InvertedIfElse'
    int priority = 3
    Class astVisitorClass = InvertedIfElseAstVisitor
}

class InvertedIfElseAstVisitor extends AbstractAstVisitor {

    void visitIfElse(IfStatement ifElse) {

        if (ifElse.booleanExpression.expression instanceof NotExpression) {
            if (ifElse.elseBlock instanceof BlockStatement) {
                addViolation ifElse.booleanExpression, 'Testing the negative condition first can make an if statement confusing'
            }
        }

        dispatchToIfWithoutSuper ifElse
        
    }

    private void dispatchToIfWithoutSuper(IfStatement ifElse) {
        // no need to visit boolean expression

        // if block might have another instance of this error
        ifElse.ifBlock.visit(this)

        Statement elseBlock = ifElse.elseBlock

        if (elseBlock instanceof IfStatement) {
            // uh-oh found an if-elseif-else
            dispatchToIfWithoutSuper elseBlock
        } else {
            elseBlock.visit(this)
        }
    }
}
