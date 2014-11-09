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
import org.codehaus.groovy.ast.expr.*
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractSharedAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern
/**
 * Rule that checks for private fields that are only set within a constructor or field initializer.
 * Such fields can safely be made final.
 *
 * @author Chris Mair
 */
class PrivateFieldCouldBeFinalRule extends AbstractSharedAstVisitorRule {

    String name = 'PrivateFieldCouldBeFinal'
    int priority = 3
    String ignoreFieldNames
    boolean ignoreJpaEntities = false
    Class astVisitorClass = PrivateFieldCouldBeFinalAstVisitor

    @Override
    protected List<Violation> getViolations(AstVisitor visitor, SourceCode sourceCode) {
        def wildcardPattern = new WildcardPattern(ignoreFieldNames, false)

        visitor.initializedFields.each { FieldNode fieldNode ->
            boolean isIgnoredBecauseMatchesPattern = wildcardPattern.matches(fieldNode.name)
            boolean isIgnoredBecauseDefinedInJpaEntity = ignoreJpaEntities && isDefinedInJpaEntity(fieldNode)
            boolean isIgnored = isIgnoredBecauseMatchesPattern || isIgnoredBecauseDefinedInJpaEntity
            if (!isIgnored) {
                def className = fieldNode.owner.name
                def violationMessage = "Private field [${fieldNode.name}] in class $className is only set within the field initializer or a constructor, and so it can be made final."
                visitor.addViolation(fieldNode, violationMessage)
            }
        }
        return visitor.violations
    }
    
    boolean isDefinedInJpaEntity(FieldNode fieldNode) {
        return AstUtil.hasAnyAnnotation(fieldNode.owner, 'Entity', 
                                                         'MappedSuperclass', 
                                                         'javax.persistence.Entity',
                                                         'javax.persistence.MappedSuperclass')
    }
}

class PrivateFieldCouldBeFinalAstVisitor extends AbstractAstVisitor {

    private final Collection<FieldNode> initializedFields = []
    private final Collection<FieldNode> allFields = []
    private boolean withinConstructor
    private ClassNode currentClassNode

    @Override
    protected void visitClassEx(ClassNode node) {
        currentClassNode = node
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
        boolean isAssignment = expression.operation.text.endsWith('=') && expression.operation.text != '=='
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
    void visitClosureExpression(ClosureExpression expression) {
        def originalWithinConstructor = withinConstructor

        // Closures within constructor cannot set final fields, so turn off constructor context for closures
        withinConstructor = false

        super.visitClosureExpression(expression)
        withinConstructor = originalWithinConstructor
    }

    @Override
    void visitPostfixExpression(PostfixExpression expression) {
        removeExpressionVariableName(expression)
        super.visitPostfixExpression(expression)
    }

    @Override
    void visitPrefixExpression(PrefixExpression expression) {
        removeExpressionVariableName(expression)
        super.visitPrefixExpression(expression)
    }

    //------------------------------------------------------------------------------------
    // Helper Methods
    //------------------------------------------------------------------------------------

    private void removeExpressionVariableName(expression) {
        if (expression.expression instanceof VariableExpression) {
            def varName = expression.expression.name
            removeInitializedField(varName)
        }
    }

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
        def fieldNode = allFields.find { field -> isMatchingField(field, varName) }
        def alreadyInitializedFieldNode = initializedFields.find { field -> isMatchingField(field, varName) }
        if (fieldNode && !alreadyInitializedFieldNode) {
            initializedFields << fieldNode
        }
    }

    private void removeInitializedField(String varName) {
        if (varName in initializedFields.name) {
            initializedFields.removeAll { field -> isMatchingField(field, varName) }
        }
    }

    private Number isPrivate(FieldNode field) {
        return field.modifiers & FieldNode.ACC_PRIVATE
    }

    private boolean isMatchingField(FieldNode field, String name) {
        field.name == name && isOwnedByClassOrItsOuterClass(field, currentClassNode)
    }

    private boolean isOwnedByClassOrItsOuterClass(FieldNode field, ClassNode classNode) {
        if (classNode == null) {
            return false
        }
        field.owner == classNode || isOwnedByClassOrItsOuterClass(field, classNode.outerClass)
    }
}
