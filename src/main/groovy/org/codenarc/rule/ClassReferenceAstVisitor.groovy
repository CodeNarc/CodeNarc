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
 package org.codenarc.rule

import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.ClassNode

/**
 * AstVisitor that check for references for a named class
 *
 * @author Chris Mair
 */
class ClassReferenceAstVisitor extends AbstractAstVisitor {

    private final className = 'java.sql.Connection'
    private final String violationMessage = "Found reference to $className"

    ClassReferenceAstVisitor(String className) {
        this.className = className
        violationMessage = "Found reference to $className"
    }

    @Override
    void visitImports(ModuleNode node) {
        def allImports = node.imports + node.staticStarImports.values()
        allImports?.each { importNode ->
            if (importNode.className == className) {
                addViolation(rule.createViolationForImport(sourceCode, importNode, violationMessage))
            }
        }
        super.visitImports(node)
    }

    @Override
    void visitFieldEx(FieldNode node) {
        checkType(node)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        checkType(expression.text, expression)
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        checkType(expression)
        super.visitClassExpression(expression)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression node) {
        if (isFirstVisit(node)) {
            checkType(node)
        }
        super.visitConstructorCallExpression(node)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        checkType(expression)
        super.visitVariableExpression(expression)
    }

    @Override
    protected void visitConstructorOrMethodEx(MethodNode node, boolean isConstructor) {
        checkType(node.returnType.name, node)

        node.parameters.each { parameter ->
            checkType(parameter)
        }
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        expression.parameters.each { parameter ->
            checkType(parameter)
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        checkType(expression)
        super.visitCastExpression(expression)
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        def superClassName = node.superClass.name
        if (superClassName != 'java.lang.Object') {
            checkType(superClassName, node)
        }
        node.interfaces.each { interfaceNode ->
            checkType(interfaceNode.name, node)
        }
        super.visitClassEx(node)
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    private void checkType(String type, node) {
        if (type == className) {
            addViolation(node, violationMessage)
        }
    }

    private void checkType(node) {
        if (node.type.name == className) {
            addViolation(node, violationMessage)
        }
    }
}
