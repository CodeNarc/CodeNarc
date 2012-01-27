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
 package org.codenarc.rule.design

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Rule that checks for private fields that are only set within a constructor or field initializer.
 * Such fields can safely be made final.
 *
 * @author Chris Mair
 */
class PrivateFieldCouldBeFinalRule extends AbstractAstVisitorRule {

    String name = 'PrivateFieldCouldBeFinal'
    int priority = 2
//    String ignoreFieldNames = 'serialVersionUID'
    Class astVisitorClass = PrivateFieldCouldBeFinalAstVisitor

}

class PrivateFieldCouldBeFinalAstVisitor extends AbstractAstVisitor {

    private Collection<FieldNode> initializedFields
    private Collection<FieldNode> allFields
    private boolean withinConstructor

    @Override
    protected void visitClassEx(ClassNode node) {
        allFields = node.getFields().findAll { field -> isPrivate(field) && !field.isFinal() && !field.synthetic }
        initializedFields = allFields.findAll { field ->
            field.initialExpression
        }
        super.visitClassEx(node)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        initializedFields.each { field ->
            addViolationForField(field)
        }
        super.visitClassComplete(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        withinConstructor = true
        super.visitConstructor(node)
        withinConstructor = false
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        def matchingFieldName
        if (expression.leftExpression instanceof VariableExpression) {
            matchingFieldName = expression.leftExpression.name
        }
        if (expression.leftExpression instanceof PropertyExpression) {
            def propertyExpression = expression.leftExpression
            boolean isMatchingPropertyExpression = propertyExpression.objectExpression instanceof VariableExpression &&
//              expression.objectExpression.name in ['this', currentClassNode.nameWithoutPackage] &&
                    propertyExpression.objectExpression.name in ['this'] &&
                    propertyExpression.property instanceof ConstantExpression
            if (isMatchingPropertyExpression) {
                matchingFieldName = propertyExpression.property.value
            }
        }
        boolean isAssignment = expression.operation.text == '='
        if (isAssignment && matchingFieldName) {
            if (withinConstructor) {
                addInitializedField(matchingFieldName)
            }
            else {
                removeInitializedField(matchingFieldName)
            }
        }

        super.visitBinaryExpression(expression)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void addInitializedField(varName) {
        def fieldNode = allFields.find { field -> field.name == varName }
        def alreadyInitializedFieldNode = initializedFields.find { field -> field.name == varName }
        if (fieldNode && !alreadyInitializedFieldNode) {
            initializedFields << fieldNode
        }
    }

    private void removeInitializedField(String varName) {
        if (varName in initializedFields.name) {
            initializedFields.removeAll { field -> field.name == varName }
        }
    }

    private void addViolationForField(FieldNode node) {
        def violationMessage = "Private field [${node.name}] is only set within the field initializer or a constructor, and so it can be made private."
        addViolation(node, violationMessage)
    }

    private Number isPrivate(FieldNode field) {
        return field.modifiers & FieldNode.ACC_PRIVATE
    }
}