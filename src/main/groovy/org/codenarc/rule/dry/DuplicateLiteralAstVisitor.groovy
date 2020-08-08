/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.dry

import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor

/**
 * Abstract superclass for rule AstVisitor classes that detect duplicate literal constants
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 * @author Nicolas Vuillamy
  */
class DuplicateLiteralAstVisitor extends AbstractAstVisitor {

    List<String> constants = []
    private final List<Class> constantTypes
    private final Set ignoreValuesSet
    private final Closure additionalCheckClosure
    private boolean isEnum

    DuplicateLiteralAstVisitor(Class constantType, Set ignoreValuesSet) {
        assert constantType
        this.constantTypes = [constantType]
        this.ignoreValuesSet = ignoreValuesSet
    }

    DuplicateLiteralAstVisitor(List<Class> constantTypes, Set ignoreValuesSet) {
        assert constantTypes
        this.constantTypes = constantTypes
        this.ignoreValuesSet = ignoreValuesSet
    }

    DuplicateLiteralAstVisitor(Class constantType, Set ignoreValuesSet, Closure addlCheckClosure) {
        assert constantType
        this.constantTypes = [constantType]
        this.ignoreValuesSet = ignoreValuesSet
        this.additionalCheckClosure = addlCheckClosure
    }

    DuplicateLiteralAstVisitor(List<Class> constantTypes, Set ignoreValuesSet, Closure addlCheckClosure) {
        assert constantTypes
        this.constantTypes = constantTypes
        this.ignoreValuesSet = ignoreValuesSet
        this.additionalCheckClosure = addlCheckClosure
    }

    @Override
    void visitClassEx(ClassNode node) {
        constants.clear()
        isEnum = node.isEnum()
    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression expression) {
        expression.expressions.each {
            addViolationIfDuplicate(it)
        }
        super.visitArgumentlistExpression expression
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        addViolationIfDuplicate(call.objectExpression)
        super.visitMethodCallExpression call
    }

    @Override
    void visitListExpression(ListExpression expression) {
        expression.expressions.findAll {
            addViolationIfDuplicate it
        }
        super.visitListExpression expression
    }

    @Override
    void visitField(FieldNode node) {
        if (node.type == node.owner) {
            ignoreValuesSet.add node.name
        }
        addViolationIfDuplicate(node.initialValueExpression, node.isStatic())
        super.visitField node
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        addViolationIfDuplicate expression.leftExpression
        addViolationIfDuplicate expression.rightExpression
        super.visitBinaryExpression expression
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        addViolationIfDuplicate expression.booleanExpression
        addViolationIfDuplicate expression.trueExpression
        addViolationIfDuplicate expression.falseExpression
        super.visitShortTernaryExpression expression
    }

    @Override
    void visitReturnStatement(ReturnStatement statement) {
        addViolationIfDuplicate(statement.expression)
        super.visitReturnStatement statement
    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        call.arguments.each {
            addViolationIfDuplicate(it)
        }
        super.visitStaticMethodCallExpression call
    }

    @Override
    void visitMapEntryExpression(MapEntryExpression expression) {
        addViolationIfDuplicate expression.valueExpression
        super.visitMapEntryExpression expression
    }

    @Override
    void visitAnnotations(AnnotatedNode node) {
    // Skip annotation.
    }

    private void addViolationIfDuplicate(Expression node, boolean isStatic = false) {
        if (!isFirstVisit(node)) { return }
        if (!(node instanceof ConstantExpression)) { return }
        if (node.value == null) { return }
        if (!node.type.isResolved()) { return }
        if (isEnum && node.value instanceof Long) { return }     // ignore Long values within Enums; may match generated ids

        def literal = String.valueOf(node.value)

        for (Class constantType: constantTypes) {
            if ((constantType.isAssignableFrom(node.value.class) || node.value.class == constantType || node.type.typeClass == constantType)) {
                if (constants.contains(literal) && !isStatic && !ignoreValuesSet.contains(literal) && checkAdditional(node)) {
                    addViolation node, "Duplicate ${constantType.simpleName} Literal: $literal"
                    return
                }
            }
        }
        constants.add literal
    }

    private boolean checkAdditional(Expression node) {
        if (additionalCheckClosure != null) {
            return additionalCheckClosure.call(node)
        }
        true
    }
}
