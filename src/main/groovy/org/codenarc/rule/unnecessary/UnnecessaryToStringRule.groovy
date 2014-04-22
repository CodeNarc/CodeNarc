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

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for unnecessary calls to toString().
 *
 * Only check for calls to toString() for the value assigned to a String field or variable
 * if checkAssignments is true.
 *
 * @author Chris Mair
 */
class UnnecessaryToStringRule extends AbstractAstVisitorRule {

    String name = 'UnnecessaryToString'
    int priority = 2
    Class astVisitorClass = UnnecessaryToStringAstVisitor

    // If true, then check for calls to toString() for the value assigned to a String field or variable.
    boolean checkAssignments = true
}

class UnnecessaryToStringAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call) && AstUtil.isMethodCall(call, 'toString', 0) && isStringType(call.objectExpression)) {
            addViolation(call, "Calling toString() on the String expression in class $currentClassName is unnecessary")
        }
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitField(FieldNode node) {
        if (isStringType(node) && AstUtil.isMethodCall(node.initialExpression, 'toString', 0) && rule.checkAssignments) {
            addViolation(node, "Calling toString() when assigning to String field \"${node.name}\" in class $currentClassName is unnecessary")
        }
        super.visitField(node)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        if (expression.leftExpression instanceof VariableExpression && isStringType(expression.leftExpression)) {
            if (AstUtil.isMethodCall(expression.rightExpression, 'toString', 0) && rule.checkAssignments) {
                def varName = expression.leftExpression.name
                addViolation(expression, "Calling toString() when assigning to String variable \"$varName\" in class $currentClassName is unnecessary")
            }
        }
        super.visitDeclarationExpression(expression)
    }

    private boolean isStringType(node) {
        node.type.name in ['String', 'java.lang.String']
    }
}
