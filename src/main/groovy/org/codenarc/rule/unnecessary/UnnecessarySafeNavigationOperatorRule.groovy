/*
 * Copyright 2014 the original author or authors.
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

import static org.codenarc.util.AstUtil.*

import org.codehaus.groovy.ast.expr.ConstructorCallExpression

import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Check for safe navigation operator (?.) applied to constants and literals, or "this" or
 * "super", which can never be null.
 *
 * @author Chris Mair
 */
class UnnecessarySafeNavigationOperatorRule extends AbstractAstVisitorRule {

    String name = 'UnnecessarySafeNavigationOperator'
    int priority = 3
    Class astVisitorClass = UnnecessarySafeNavigationOperatorAstVisitor
}

class UnnecessarySafeNavigationOperatorAstVisitor extends AbstractAstVisitor {

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        checkExpression(expression, expression.objectExpression)
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression expression) {
        checkExpression(expression, expression.objectExpression)
        super.visitMethodCallExpression(expression)
    }

    private void checkExpression(Expression expression, Expression objExpr) {
        // TODO Could expand this to also check for class expressions, e.g. String?.toString(), but not parsed consistently in Groovy 1.7
        if (expression.safe && (isConstantOrLiteral(objExpr) || isThisReference(objExpr) || isSuperReference(objExpr) || isConstructorCall(objExpr))) {
            def expressionText = '"' + objExpr.text + '"'
            addViolation(expression, "The safe navigation operator (?.) is unnecessary for $expressionText in class $currentClassName")
        }
    }

    private boolean isConstructorCall(Expression objExpr) {
        return objExpr instanceof ConstructorCallExpression
    }

}
