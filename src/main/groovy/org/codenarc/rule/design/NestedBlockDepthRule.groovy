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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Rule that checks for blocks or closures nested more than a configured maximum number.
 * Blocks include if, for, while, switch, try, catch, finally and synchronized
 * blocks/statements, as well as closures. 
 * <p/>
 * The <code>maxNestedBlockDepth</code> property holds the threshold value for the maximum number of
 * nesting levels. A block or closures nested deeper than that numer of levels is considered a
 * violation. The <code>maxNestedBlockDepth</code> property defaults to 3.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class NestedBlockDepthRule extends AbstractAstVisitorRule {
    String name = 'NestedBlockDepth'
    int priority = 2
    int maxNestedBlockDepth = 3
    Class astVisitorClass = NestedBlockDepthAstVisitor
}

class NestedBlockDepthAstVisitor extends AbstractAstVisitor  {

    private finallyBlocks = [] as Set
    private nestedBlockDepth = 0

    void visitBlockStatement(BlockStatement block) {
        if (isFirstVisit(block)) {
            if (isPhantomFinallyBlock(block) || isFromGeneratedSourceCode(block)) {
                super.visitBlockStatement(block)
            }
            else {
                handleNestedBlockStatement(block)
            }
        }
    }

    // NOTE: finally blocks require special handling. The visitBlockStatement() callback will be invoked
    // twice for a finally block. We need to filter out one of them (the first one) to avoid duplicate violations.
    void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {
        finallyBlocks << tryCatchStatement.finallyStatement
        super.visitTryCatchFinally(tryCatchStatement)
    }

    void visitClosureExpression(ClosureExpression expression) {
        handleNestedClosureExpression(expression)
    }

    private void handleNestedBlockStatement(BlockStatement block) {
        handleNestedNode(block) { super.visitBlockStatement(block) } 
    }

    private void handleNestedClosureExpression(expression) {
        handleNestedNode(expression) { super.visitClosureExpression(expression) } 
    }

    private void handleNestedNode(node, Closure callVisitorMethod) {
        nestedBlockDepth++
        if (nestedBlockDepth > rule.maxNestedBlockDepth) {
            addViolation(node)
        }
        callVisitorMethod()
        nestedBlockDepth--
    }

    // The "phantom" finally block contains the "real" finally block as its first statement
    private boolean isPhantomFinallyBlock(BlockStatement block) {
        if (block.statements) {
            boolean found = finallyBlocks.find {
                !it.empty && it.statements[0] == block.statements[0]
            }
            return found
        }
        else {
            return false
        }
    }

}