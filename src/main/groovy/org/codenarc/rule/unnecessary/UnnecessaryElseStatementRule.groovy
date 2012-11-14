/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * When an if statement block ends with a return statement the else is unnecessary
 *
 * @author Victor Savkin
  */
class UnnecessaryElseStatementRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryElseStatement'
    int priority = 3
    Class astVisitorClass = UnnecessaryElseStatementAstVisitor
}

class UnnecessaryElseStatementAstVisitor extends AbstractAstVisitor {

    @Override
    void visitIfElse(IfStatement node) {
        if (isFirstVisit(node)) {
            def (allIfBlocks, theElseBlock) = collectIfsAndElses(node)

            if (isValidElseBlock(theElseBlock)) {

                if (allIfBlocks && allIfBlocks.every { allBranchesReturn(it.ifBlock) }) {
                    addViolation theElseBlock, 'When an if statement block ends with a return statement the else is unnecessary'
                }
            }
            visited.addAll allIfBlocks
            visited.add theElseBlock
        }

        super.visitIfElse node
    }

    private static collectIfsAndElses(IfStatement ifStatement) {
        def ifs = []
        def theElse = null
        def node = ifStatement

        while (theElse == null) {
            ifs.add node
            if (node.elseBlock instanceof IfStatement) {
                node = node.elseBlock
            } else {
                theElse = node.elseBlock
            }
        }
        [ifs, theElse]
    }

    private static isValidElseBlock(Statement elseBlock) {
        elseBlock != null && !elseBlock.empty && !(elseBlock instanceof IfStatement)
    }

    private static allBranchesReturn(Statement expr) {
        if(expr instanceof BlockStatement) {
            expr.statements.any {
                allBranchesReturn(it)
            }
        } else if (expr instanceof IfStatement) {
            allBranchesReturn(expr.ifBlock) && allBranchesReturn(expr.elseBlock)
        } else {
            isReturn expr
        }
    }

    private static isReturn(statement) {
        statement instanceof ReturnStatement
    }
}
