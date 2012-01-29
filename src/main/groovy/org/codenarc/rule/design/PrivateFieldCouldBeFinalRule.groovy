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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractSharedAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.WildcardPattern
import org.codehaus.groovy.ast.expr.*

/**
 * Rule that checks for private fields that are only set within a constructor or field initializer.
 * Such fields can safely be made final.
 *
 * @author Chris Mair
 */
class PrivateFieldCouldBeFinalRule extends AbstractSharedAstVisitorRule {

    String name = 'PrivateFieldCouldBeFinal'
    int priority = 2
    String ignoreFieldNames
    Class astVisitorClass = PrivateFieldCouldBeFinalAstVisitor

    @Override
    protected List<Violation> getViolations(AstVisitor visitor, SourceCode sourceCode) {
        def wildcardPattern = new WildcardPattern(ignoreFieldNames, false)

        visitor.initializedFields.each { FieldNode fieldNode ->
            def isIgnored = wildcardPattern.matches(fieldNode.name)
            if (!isIgnored) {
                def violationMessage = "Private field [${fieldNode.name}] is only set within the field initializer or a constructor, and so it can be made private."
                visitor.addViolation(fieldNode, violationMessage)
            }
        }
        return visitor.violations
    }
}

class PrivateFieldCouldBeFinalAstVisitor extends AbstractAstVisitor {

    private final Collection<FieldNode> initializedFields = []
    private final Collection<FieldNode> allFields = []
    private boolean withinConstructor

    @Override
    protected void visitClassEx(ClassNode node) {
        def allClassFields = node.fields.findAll { field -> isPrivate(field) && !field.isFinal() && !field.synthetic }
        allFields.addAll(allClassFields)
        def initializedClassFields = allClassFields.findAll { field -> field.initialExpression }
        initializedFields.addAll(initializedClassFields)
        super.visitClassEx(node)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        withinConstructor = true
        super.visitConstructor(node)
        withinConstructor = false
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        def matchingFieldName = extractVariableOrFieldName(expression)
        boolean isAssignment = expression.operation.text.endsWith('=')
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

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof PrefixExpression || statement.expression instanceof PostfixExpression) {
            if (statement.expression.expression instanceof VariableExpression) {
                def varName = statement.expression.expression.name
                removeInitializedField(varName)
            }
        }
        super.visitExpressionStatement(statement)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private String extractVariableOrFieldName(BinaryExpression expression) {
        def matchingFieldName
        if (expression.leftExpression instanceof VariableExpression) {
            matchingFieldName = expression.leftExpression.name
        }
        if (expression.leftExpression instanceof PropertyExpression) {
            def propertyExpression = expression.leftExpression
            boolean isMatchingPropertyExpression = propertyExpression.objectExpression instanceof VariableExpression &&
                propertyExpression.objectExpression.name == 'this' &&
                propertyExpression.property instanceof ConstantExpression
            if (isMatchingPropertyExpression) {
                matchingFieldName = propertyExpression.property.value
            }
        }
        return matchingFieldName
    }

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

    private Number isPrivate(FieldNode field) {
        return field.modifiers & FieldNode.ACC_PRIVATE
    }
}