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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Method contains a pointless self-assignment to a variable or property.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessarySelfAssignmentRule extends AbstractAstVisitorRule {
    String name = 'UnnecessarySelfAssignment'
    int priority = 3
    Class astVisitorClass = UnnecessarySelfAssignmentAstVisitor
}

class UnnecessarySelfAssignmentAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (AstUtil.isBinaryExpressionType(expression, '=')) {
            Expression left = expression.leftExpression
            Expression right = expression.rightExpression
            if (left instanceof VariableExpression && right instanceof VariableExpression) {
                if (left.name == right.name) {
                    addViolation(expression, 'Assignment a variable to itself should be unnecessary. Remove this dead code')
                }
            } else if (propertyExpressionsAreEqual(left, right)) {
                if (left.text == right.text) {
                    addViolation(expression, 'Assignment a variable to itself should be unnecessary. Remove this dead code')
                }
            }
        }
        super.visitBinaryExpression(expression)
    }

    private static boolean propertyExpressionsAreEqual(Expression left, Expression right) {

        def stack = [[left, right]] as Stack
        while (stack) {
            (left, right) = stack.pop()
            if (!(left instanceof PropertyExpression) || !(right instanceof PropertyExpression)) {
                return false
            }
            if (left.text != right.text) {
                return false
            }
            if (left.spreadSafe != right.spreadSafe || left.safe != right.safe) {
                return false
            }
            if (left.objectExpression instanceof PropertyExpression && right.objectExpression instanceof PropertyExpression) {
                // recursive alternative
                stack << [left.objectExpression, right.objectExpression]
            }
        }
        true
    }
}
