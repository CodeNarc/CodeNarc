/*
 * Copyright 2019 the original author or authors.
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

import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Checks for use of the float or Float types, in fields, variables, method parameters and method return types.
 *
 * @author Chris Mair
 */
class NoFloatRule extends AbstractAstVisitorRule {

    String name = 'NoFloat'
    int priority = 2
    Class astVisitorClass = NoFloatAstVisitor
}

class NoFloatAstVisitor extends AbstractAstVisitor {

    private static final List FLOAT_TYPE_NAMES = ['float', 'Float', 'java.lang.Float']

    @Override
    void visitField(FieldNode node) {
        if (isFloatTypeName(node.type.name)) {
            addViolation(node, "The field ${node.name} in class ${currentClassName} is of type float/Float. Prefer using BigDecimal.")
        }
        super.visitField(node)
    }

    @Override
    protected void visitMethodComplete(MethodNode node) {
        if (isFloatTypeName(node.returnType.name)) {
            addViolation(node, "The method ${node.name} in class ${currentClassName} has a return type of float/Float. Prefer using BigDecimal.")
        }
        processParameters(node.parameters, node.name)
        super.visitMethodComplete(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        processParameters(node.parameters, '<init>')
        super.visitConstructor(node)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        if (isFloatTypeName(expression.type.name)) {
            addViolation(expression, "The variable ${expression.name} in class ${currentClassName} is of type float/Float. Prefer using BigDecimal.")
        }
        super.visitVariableExpression(expression)
    }

    private boolean isFloatTypeName(String typeName) {
        return typeName in FLOAT_TYPE_NAMES
    }

    private void processParameters(Parameter[] parameters, String methodName) {
        parameters.each { parameter ->
            if (isFloatTypeName(parameter.type.name)) {
                addViolation(parameter, "The parameter named ${parameter.name} in method $methodName of class $currentClassName is of type float/Float. Prefer using BigDecimal.")
            }
        }
    }

}
