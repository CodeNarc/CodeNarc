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
package org.codenarc.rule.exceptions

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Returning null from a catch block often masks errors and requires the client to handle error codes.
 * In some coding styles this is discouraged. This rule ignores methods with void return type.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class ReturnNullFromCatchBlockRule extends AbstractAstVisitorRule {
    String name = 'ReturnNullFromCatchBlock'
    int priority = 2
    Class astVisitorClass = ReturnNullFromCatchBlockAstVisitor
}

class ReturnNullFromCatchBlockAstVisitor extends AbstractAstVisitor {

    @Override
    protected boolean shouldVisitMethod(MethodNode node) {
        // Ignore void methods
        return node.returnType.toString() != 'void'
    }

    void visitCatchStatement(CatchStatement node) {
        def lastStatement = getLastStatement(node)
        if (lastStatement instanceof ReturnStatement) {
            if (AstUtil.isNull(lastStatement.expression)) {
                addViolation lastStatement, 'Do not return null from a catch block'
            }
        }
        super.visitCatchStatement node
    }

    /**
     * This is not a good general function for AstUtils.
     * It is too specific and may not work across different ASTNode subtypes.
     * @param node
     *      node
     */
    private static getLastStatement(node) {
        if (node.code instanceof BlockStatement && node.code.statements) {
            return node.code.statements.last()
        }
        null
    }
}
