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
 package org.codenarc.rule

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codenarc.util.WildcardPattern

import java.text.MessageFormat

import org.codehaus.groovy.ast.expr.*

/**
 * AstVisitor that check for references for a named class
 *
 * @author Chris Mair
 */
class ClassReferenceAstVisitor extends AbstractAstVisitor {

    private final classNamePattern
    private final String violationMessagePattern

    /**
     * Constructor
     * @param classNames - one or more comma-separated class name patterns. Can contain wildcards (*,?)
     */
    ClassReferenceAstVisitor(String classNames) {
        this(classNames, 'Found reference to {0}')
    }

    /**
     * Constructor
     * @param classNames - one or more comma-separated class name patterns. Can contain wildcards (*,?)
     * @param violationMessagePattern - the MessageFormat String pattern used to build the violation message.
     *          The class name is passed as the single argument to the pattern. e.g. "Found reference to {0}"
     */
    ClassReferenceAstVisitor(String classNames, String violationMessagePattern) {
        this.classNamePattern = new WildcardPattern(classNames)
        this.violationMessagePattern = violationMessagePattern
    }

    @Override
    void visitImports(ModuleNode node) {
        def allImports = node.imports + node.staticStarImports.values()
        allImports?.each { importNode ->
            if (classNamePattern.matches(importNode.className)) {
                def violationMessage = formatViolationMessage(importNode.className)
                addViolation(rule.createViolationForImport(sourceCode, importNode, violationMessage))
            }
        }
        super.visitImports(node)
    }

    @Override
    void visitField(FieldNode node) {
        checkNodeType(node)
        super.visitField(node)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        checkType(expression.text, expression)
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        checkNodeType(expression)
        super.visitClassExpression(expression)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression node) {
        if (isFirstVisit(node)) {
            checkNodeType(node)
        }
        super.visitConstructorCallExpression(node)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        checkNodeType(expression)
        checkType(expression.name, expression)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        checkType(node.returnType.name, node)

        node.parameters.each { parameter ->
            checkNodeType(parameter)
        }
        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        expression.parameters.each { parameter ->
            checkNodeType(parameter)
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitCastExpression(CastExpression expression) {
        checkNodeType(expression)
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
        if (classNamePattern.matches(type)) {
            def violationMessage = formatViolationMessage(type)
            addViolation(node, violationMessage)
        }
    }

    private void checkNodeType(node) {
        if (classNamePattern.matches(node.type.name)) {
            def violationMessage = formatViolationMessage(node.type.name)
            addViolation(node, violationMessage)
        }
    }

    private String formatViolationMessage(String typeName) {
        MessageFormat.format(violationMessagePattern, typeName)
    }
}
