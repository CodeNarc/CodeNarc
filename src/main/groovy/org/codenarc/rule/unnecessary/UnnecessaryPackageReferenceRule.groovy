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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.*
import static org.codenarc.util.AstUtil.*

/**
 * Checks for explicit package reference for classes that Groovy imports by default, such as java.lang.String,
 * java.util.Map and groovy.lang.Closure.
 *
 * @author Chris Mair
 */
class UnnecessaryPackageReferenceRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryPackageReference'
    int priority = 2
    Class astVisitorClass = UnnecessaryPackageReferenceAstVisitor
}

class UnnecessaryPackageReferenceAstVisitor extends AbstractAstVisitor {

    // TODO Only ignore java.util.Object superclass if it was not explicitly specified

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
        if (isNotAutoBoxed(expression)) {
            checkTypeIfNotDynamicallyTyped(expression)
        }
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
        if (typeName in AUTO_IMPORTED_CLASSES || parentPackageName(typeName) in AUTO_IMPORTED_PACKAGES) {
            addViolation(node, "Specifying the package name is not necessary for $typeName")
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

    private boolean isNotAutoBoxed(VariableExpression expression) {
        return expression.type.name == expression.originType.name
    }
}
