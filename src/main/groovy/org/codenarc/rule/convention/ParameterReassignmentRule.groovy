/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for a method or closure parameter being reassigned to a new value within the body of the
 * method/closure, which is a confusing, questionable practice. Use a temporary variable instead.
 *
 * @author Chris Mair
 */
class ParameterReassignmentRule extends AbstractAstVisitorRule {
    String name = 'ParameterReassignment'
    int priority = 3
    Class astVisitorClass = ParameterReassignmentAstVisitor
}

class ParameterReassignmentAstVisitor extends AbstractAstVisitor {

    private currentMethodParameterNames
    private final currentClosureParameterNames = []

    @Override
    protected void visitMethodEx(MethodNode node) {
        currentMethodParameterNames = node.parameters.name
        super.visitMethodEx(node)
    }

    @Override
    protected void visitMethodComplete(MethodNode node) {
        super.visitMethodComplete(node)
        currentMethodParameterNames = []
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        def parameterNames = expression.parameters?.name ?: []
        currentClosureParameterNames.addAll(parameterNames)
        super.visitClosureExpression(expression)
        currentClosureParameterNames.removeAll(parameterNames)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (isFirstVisit(expression) && isReassigningAParameter(expression)) {
            def name = expression.leftExpression.name
            addViolation(expression, "The method parameter [$name] in class $currentClassName was reassigned. Use a temporary variable instead.")
        }
        super.visitBinaryExpression(expression)
    }

    private boolean isReassigningAParameter(BinaryExpression expression) {
        expression.operation.text == '=' &&
            expression.leftExpression instanceof VariableExpression &&
            isCurrentParameterName(expression.leftExpression.name)
    }

    private boolean isCurrentParameterName(String name) {
        name in currentMethodParameterNames || name in currentClosureParameterNames
    }
}
