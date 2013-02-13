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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The Collections.sort() method mutates the list and returns the list as a value. If you are assigning the result of
 * sort() to a variable, then you probably don't realize that you're also modifying the original list as well. This is
 * frequently the cause of subtle bugs.
 *
 * @author Hamlet D'Arcy
 */
class AssignCollectionSortRule extends AbstractAstVisitorRule {
    String name = 'AssignCollectionSort'
    int priority = 2
    Class astVisitorClass = AssignCollectionSortAstVisitor
}

class AssignCollectionSortAstVisitor extends AbstractAstVisitor {
    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {

        Expression right = expression.rightExpression

        if (right instanceof MethodCallExpression) {
            if (isChainedSort(right) || (isChainedSort(right.objectExpression))) {
                addViolation(expression, 'Violation in $currentClassName. sort() mutates the original list, but the return value is being assigned')
            }
        }
        super.visitDeclarationExpression expression
    }

    private static boolean isChainedSort(Expression expression) {
        if (AstUtil.isMethodCall(expression, 'sort', 0..2)) {
            def arguments = AstUtil.getMethodArguments(expression)
            def isMutateFalse = arguments && AstUtil.isFalse(arguments[0])
            if (expression.objectExpression instanceof VariableExpression && !isMutateFalse) {
                return true
            }
        }
        false
    }
}
