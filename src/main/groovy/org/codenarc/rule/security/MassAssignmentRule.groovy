/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.security

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression




/**
 * Untrusted input should not be allowed to set arbitrary object fields without restriction. 
 *
 * TODO: Switch from name-based detection to type based detection. I wasn't able to get interface
 *       and type detection working
 * @author Brian Soby
 */
class MassAssignmentRule extends AbstractAstVisitorRule {
    String name = 'MassAssignment'
    int priority = 2
    Class astVisitorClass = MassAssignmentAstVisitor
}

class MassAssignmentAstVisitor extends AbstractAstVisitor {
    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {
        if (call.arguments && call.arguments.expressions && call.arguments.expressions.first().variable == 'params'){
            addViolation(call, 'Restrict mass attribute assignment')
        }
        super.visitConstructorCallExpression(call)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.leftExpression instanceof PropertyExpression) {
            if (expression.leftExpression.property.value == 'properties') {
                if (expression.rightExpression instanceof VariableExpression && expression.rightExpression.variable == 'params') {
                    addViolation(expression, 'Restrict mass attribute assignment')
                }
            }
        }
        super.visitBinaryExpression(expression)

    }
}
