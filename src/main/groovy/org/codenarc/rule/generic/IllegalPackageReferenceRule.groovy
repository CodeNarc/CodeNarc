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
package org.codenarc.rule.generic

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.WildcardPattern
import org.codehaus.groovy.ast.expr.*

/**
 * Checks for reference to any of the named packages.
 *
 * The <code>packageNames</code> property specifies the comma-separated list of package names to check for.
 * If null or empty, do nothing.
 * <p/>

 *
 * @author Chris Mair
 */
class IllegalPackageReferenceRule extends AbstractAstVisitorRule {
    String name = 'IllegalPackageReference'
    int priority = 2
    String packageNames = null
    Class astVisitorClass = IllegalPackageReferenceAstVisitor

    boolean isReady() {
        packageNames
    }
}

class IllegalPackageReferenceAstVisitor extends AbstractAstVisitor {

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

    @Override
    void visitFieldEx(FieldNode node) {
        checkTypeIfNotDynamicallyTyped(node)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression node) {
        if (isFirstVisit(node) && !node.superCall) {
            checkType(node.type.name, node)
        }
        super.visitConstructorCallExpression(node)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        checkTypeIfNotDynamicallyTyped(expression)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitMethodEx(MethodNode node) {
        if (!node.isDynamicReturnType()) {       // ignore 'def' which resolves to java.lang.Object
            checkType(node.returnType.name, node)
        }
        node.parameters.each { parameter ->
            checkTypeIfNotDynamicallyTyped(parameter)
        }
        super.visitMethodEx(node)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        expression.parameters.each { parameter ->
            checkTypeIfNotDynamicallyTyped(parameter)
        }
        super.visitClosureExpression(expression)
    }

    @Override
    void visitClassExpression(ClassExpression expression) {
        checkType(expression.type.name, expression)
        super.visitClassExpression(expression)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        checkType(expression.text, expression)
    }

    //--------------------------------------------------------------------------
    // Helper Methods
    //--------------------------------------------------------------------------

    private void checkTypeIfNotDynamicallyTyped(node) {
        if (!node.isDynamicTyped()) {       // ignore 'def' which resolves to java.lang.Object
            checkType(node.type.name, node)
        }
    }

    private void checkType(String typeName, node) {
        def wildcard = new WildcardPattern(rule.packageNames)
        def parentPackage = parentPackageName(typeName)
        if (wildcard.matches(parentPackage)) {
            addViolation(node, "Found reference to illegal package name $parentPackage")
        }
    }

    private String parentPackageName(String typeName) {
        if (typeName.contains('.')) {
            def lastPeriod = typeName.lastIndexOf('.')
            return typeName[0..lastPeriod-1]
        }
        else {
            return null
        }
    }
}
