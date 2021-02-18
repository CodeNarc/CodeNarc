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
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.util.AstUtil

/**
 * AST Visitor that searches for references to the fields specified on the constructor
 *
 * @author Chris Mair
 */
class FieldReferenceAstVisitor extends AbstractAstVisitor {

    protected final Map<String, FieldNode> unreferencedFieldMap = [:]

    FieldReferenceAstVisitor(Collection<FieldNode> fields) {
        fields.each { fieldNode ->
            // Cannot use map syntax here because the name might be a property on Map, such as "metaClass"
            unreferencedFieldMap.put(fieldNode.name, fieldNode)
        }
    }

    Collection<FieldNode> getUnreferencedFields() {
        return unreferencedFieldMap.values()
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        fieldReferenced(expression.name)
        super.visitVariableExpression(expression)
    }

    @Override
    void visitProperty(PropertyNode node) {
        fieldReferenced(node.name)
        super.visitProperty(node)
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        if (expression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.name in ['this', currentClassNode.nameWithoutPackage] + outerClassNames &&
            expression.property instanceof ConstantExpression) {
            fieldReferenced(expression.property.value)
        } else if (expression.objectExpression instanceof PropertyExpression &&
            expression.objectExpression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.property instanceof ConstantExpression &&
            expression.objectExpression.objectExpression.name  == currentClassNode.outerClass?.name &&
            expression.objectExpression.property.value == 'this') {
            fieldReferenced(expression.property.value)
        }
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitMethodEx(MethodNode node) {
        if (node.parameters) {
            node.parameters.each { parameter ->
                def initialExpression = parameter.initialExpression
                if (initialExpression && AstUtil.respondsTo(initialExpression, 'getName')) {
                    fieldReferenced(initialExpression.name)
                }
            }
        }
        super.visitMethodEx(node)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        // If there happens to be a method call on a method with the same name as the field.
        // This handles the case of defining a closure and then executing it, e.g.:
        //      private myClosure = { println 'ok' }
        //      ...
        //      myClosure()
        // But this could potentially "hide" some unused fields (i.e. false negatives).
        if (AstUtil.isMethodCallOnObject(call, 'this') && call.method instanceof ConstantExpression) {
            fieldReferenced(call.method.value)
        } else if (call.objectExpression instanceof PropertyExpression &&
            call.objectExpression.objectExpression instanceof VariableExpression &&
            call.objectExpression.property instanceof ConstantExpression &&
            call.method instanceof ConstantExpression &&
            call.objectExpression.objectExpression.name == currentClassNode.outerClass?.name &&
            call.objectExpression.property.value == 'this') {
            fieldReferenced(call.method.value)
        }
        super.visitMethodCallExpression(call)
    }

    protected void fieldReferenced(String name) {
        unreferencedFieldMap.remove(name)
    }

    private List<String> getOuterClassNames() {
        List<String> outerClassNames = []
        ClassNode classNode = currentClassNode
        while (classNode.outerClass != null) {
            outerClassNames += classNode.outerClass.nameWithoutPackage
            classNode = classNode.outerClass
        }
        outerClassNames
    }
}
