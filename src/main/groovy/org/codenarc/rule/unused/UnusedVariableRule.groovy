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
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ClosureExpression

/**
 * Rule that checks for variables that are not referenced.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedVariableRule extends AbstractAstVisitorRule {
    String name = 'UnusedVariable'
    int priority = 2
    Class astVisitorClass = UnusedVariableAstVisitor
}

class UnusedVariableAstVisitor extends AbstractAstVisitor  {
    private static final VARIABLES = 'variables'
    private static final CLOSURE_VARIABLES = 'closureVariables'
    private allVariables = [] as Stack
    private currentVariables

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            varExpressions.each { varExpression ->
                if (declarationExpression.rightExpression && declarationExpression.rightExpression instanceof ClosureExpression) {
                    currentVariables[CLOSURE_VARIABLES][varExpression] = false
                }
                else {
                    currentVariables[VARIABLES][varExpression] = false
                }
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    void visitBlockStatement(BlockStatement block) {
        currentVariables = [(VARIABLES):[:], (CLOSURE_VARIABLES):[:]]
        allVariables.push(currentVariables)

        super.visitBlockStatement(block)

        def allCurrentVariables = currentVariables[VARIABLES] + currentVariables[CLOSURE_VARIABLES]
        allCurrentVariables.each { varExpression, isUsed ->
            if (!isUsed) {
                addViolation(varExpression)
            }
        }

        allVariables.pop()
        currentVariables = allVariables.empty() ? null : allVariables.peek()
    }
    
    void visitVariableExpression(VariableExpression expression) {
        markVariableAsReferenced(VARIABLES, expression.name, expression.lineNumber)
        super.visitVariableExpression(expression)
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        // If there happens to be a method call on a method with the same name as the variable.
        // This handles the case of defining a closure and then executing it, e.g.:
        //      def myClosure = { println 'ok' }
        //      myClosure()
        // But this could potentially "hide" some unused variables (i.e. false negatives).
        if (call.objectExpression instanceof VariableExpression &&
            call.objectExpression.name == 'this' &&
            call.method instanceof ConstantExpression) {
            markVariableAsReferenced(CLOSURE_VARIABLES, call.method.value, call.method.lineNumber)
        }
        super.visitMethodCallExpression(call)
    }

    private void markVariableAsReferenced(String varType, String varName, Integer lineNumber) {
        for(blockVariables in allVariables) {
            for(var in blockVariables[varType].keySet()) {
                if (var.name == varName && var.lineNumber != lineNumber) {
                    blockVariables[varType][var] = true
                    return
                }
            }
        }
    }
}