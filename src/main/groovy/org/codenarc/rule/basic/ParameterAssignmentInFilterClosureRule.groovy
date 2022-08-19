/*
 * Copyright 2021 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * An assignment operator was used on a parameter in a filtering closure.
 * This is usually a typo, and the comparison operator (==) was intended.
 *
 * @author Morten Kristiansen
 */
class ParameterAssignmentInFilterClosureRule extends AbstractAstVisitorRule {

    String name = 'ParameterAssignmentInFilterClosure'
    int priority = 2
    Class astVisitorClass = ParameterAssignmentInFilterClosureAstVisitor
}

class ParameterAssignmentInFilterClosureAstVisitor extends AbstractAstVisitor {

    private static final String FILTER_METHOD_NAMES_MATCHER = [
            'find', 'findAll', 'findIndexOf', 'every', 'any', 'filter', 'grep', 'dropWhile', 'takeWhile'
    ].join('|')

    public static final String ERROR_MESSAGE = 'An assignment operator was used on a parameter in a filtering closure.' +
            ' This is usually a typo, and the comparison operator (==) was intended.'

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodNamed(call, FILTER_METHOD_NAMES_MATCHER)) {
            def arguments = AstUtil.getMethodArguments(call)
            if (arguments.size() > 0 && arguments.last() instanceof ClosureExpression) {
                ClosureExpression last = arguments.last()
                last.code?.visit(new AssignmentTracker(parent: this, errorMessage: ERROR_MESSAGE, codeText: last.code.text, parameterNames: createParameterNameList(last)))
            }
        }
        super.visitMethodCallExpression(call)
    }

    private List<String> createParameterNameList(ClosureExpression last) {
        last.parameters*.name ?: ['it']
    }
}

class AssignmentTracker extends AbstractAstVisitor {

    AbstractAstVisitor parent
    String errorMessage
    String codeText
    List<String> parameterNames

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        addViolationIfAssignment(expression)
        super.visitBinaryExpression(expression)
    }

    private void addViolationIfAssignment(Expression node) {
        if (node instanceof BinaryExpression) {
            String parameterNamesMatchString = parameterNames.join('|')
            if (node.operation.text == '=') {
                Expression leftExpression = node.leftExpression
                if (AstUtil.isVariable(leftExpression, parameterNamesMatchString)) {
                    parent.addViolation(node, errorMessage)
                } else if (isPropertyOrSubPropertyOfVariableName(leftExpression, parameterNamesMatchString)) {
                    parent.addViolation(node, errorMessage)
                }
            } else {
                if (node.operation.text in ['&&', '||']) {
                    addViolationIfAssignment(node.leftExpression)
                    addViolationIfAssignment(node.rightExpression)
                }
            }
        }
    }

    private boolean isPropertyOrSubPropertyOfVariableName(Expression expression, String pattern) {
        if(expression instanceof PropertyExpression) {
            if (expression.objectExpression instanceof PropertyExpression) {
                return isPropertyOrSubPropertyOfVariableName(expression.objectExpression, pattern)
            } else if (AstUtil.isVariable(expression.objectExpression, pattern)) {
                return true
            }
        }
        return false
    }
}

