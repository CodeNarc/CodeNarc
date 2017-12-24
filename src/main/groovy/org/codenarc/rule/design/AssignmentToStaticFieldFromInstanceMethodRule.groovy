/*
 * Copyright 2015 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for assignment to a static field from an instance method.
 *
 * @author Chris Mair
 */
class AssignmentToStaticFieldFromInstanceMethodRule extends AbstractAstVisitorRule {

    String name = 'AssignmentToStaticFieldFromInstanceMethod'
    int priority = 2
    Class astVisitorClass = AssignmentToStaticFieldFromInstanceMethodAstVisitor
}

class AssignmentToStaticFieldFromInstanceMethodAstVisitor extends AbstractAstVisitor {

    private Collection<String> fieldNames
    private String withinInstanceMethodName = null
    private final Set<String> localVariableNames = []

    @Override
    protected void visitClassEx(ClassNode node) {
        def staticFields = node.getFields().findAll { f -> f.isStatic() }
        this.fieldNames = staticFields.name
        super.visitClassEx(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        withinInstanceMethodName = node.isStatic() ? null : node.name
        super.visitMethodEx(node)
    }

    @Override
    protected void visitMethodComplete(MethodNode node) {
        withinInstanceMethodName = null
        localVariableNames.clear()
        super.visitMethodComplete(node)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        if (expression.leftExpression instanceof VariableExpression) {
            localVariableNames.add(expression.leftExpression.name)
        }
        super.visitDeclarationExpression(expression)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        boolean isAssignment = expression.operation.text == '='
        if (isAssignment && withinInstanceMethodName && expression.leftExpression instanceof VariableExpression) {
            String name = expression.leftExpression.name
            if (fieldNames.contains(name) && !localVariableNames.contains(name)) {
                addViolation(expression, "The instance method $withinInstanceMethodName in class $currentClassName contains an assignment to static field $name")
            }
        }
        super.visitBinaryExpression(expression)
    }
}
