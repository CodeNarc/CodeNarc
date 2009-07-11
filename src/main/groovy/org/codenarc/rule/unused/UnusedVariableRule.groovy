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
package org.codenarc.rule.unused

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codenarc.util.AstUtil

/**
 * Rule that checks for variables that are not referenced.
 *
 * @author Chris Mair
 * @version $Revision: 177 $ - $Date: 2009-06-27 08:05:52 -0400 (Sat, 27 Jun 2009) $
 */
class UnusedVariableRule extends AbstractAstVisitorRule {
    String name = 'UnusedVariable'
    int priority = 2
    Class astVisitorClass = UnusedVariableAstVisitor
}

class UnusedVariableAstVisitor extends AbstractAstVisitor  {
    private unusedVariables = []

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            varExpressions.each { varExpression ->
                unusedVariables << varExpression
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    void visitBlockStatement(BlockStatement block) {
        super.visitBlockStatement(block)
        unusedVariables.each { unusedVariableExpression ->
            addViolation(unusedVariableExpression)
        }
    }

    void visitVariableExpression(VariableExpression expression) {
        def referencedVariable = unusedVariables.find { var ->
            var.name == expression.name && var.lineNumber != expression.lineNumber
        }
        if (referencedVariable) {
            unusedVariables.remove(referencedVariable)
        }
        
        super.visitVariableExpression(expression)
    }
}