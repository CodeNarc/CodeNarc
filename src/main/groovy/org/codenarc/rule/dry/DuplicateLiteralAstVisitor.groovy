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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.expr.*

/**
 * Abstract superclass for rule AstVisitor classes that detect duplicate literal constants
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class DuplicateLiteralAstVisitor extends AbstractAstVisitor {

    List<String> constants = []
    private final List<Class> constantTypes
    private final Set ignoreValuesSet

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

    @Override
    void visitClassEx(ClassNode node) {
        constants.clear()
    }

    void visitArgumentlistExpression(ArgumentListExpression expression) {
        expression.expressions.each {
            addViolationIfDuplicate(it)
        }
        super.visitArgumentlistExpression expression
    }

    void visitMethodCallExpression(MethodCallExpression call) {
        addViolationIfDuplicate(call.objectExpression)
        super.visitMethodCallExpression call
    }

    void visitListExpression(ListExpression expression) {
        expression.expressions.findAll {
            addViolationIfDuplicate it
        }
        super.visitListExpression expression
    }

    void visitField(FieldNode node) {
        if (node.type == node.owner) {
            ignoreValuesSet.add node.name
        }
        addViolationIfDuplicate(node.initialValueExpression, node.isStatic())
        super.visitField node
    }

    void visitBinaryExpression(BinaryExpression expression) {
        addViolationIfDuplicate expression.leftExpression
        addViolationIfDuplicate expression.rightExpression
        super.visitBinaryExpression expression
    }

    void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        addViolationIfDuplicate expression.booleanExpression
        addViolationIfDuplicate expression.trueExpression
        addViolationIfDuplicate expression.falseExpression
        super.visitShortTernaryExpression expression
    }

    void visitReturnStatement(ReturnStatement statement) {
        addViolationIfDuplicate(statement.expression)
        super.visitReturnStatement statement
    }

    void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        call.arguments.each {
            addViolationIfDuplicate(it)
        }
        super.visitStaticMethodCallExpression call
    }

    void visitMapEntryExpression(MapEntryExpression expression) {
        addViolationIfDuplicate expression.valueExpression
        super.visitMapEntryExpression expression
    }

    private addViolationIfDuplicate(node, boolean isStatic = false) {
        if (!isFirstVisit(node)) { return }
        if (!(node instanceof ConstantExpression)) { return }
        if (node.value == null) { return }
        if (!node.type.isResolved()) { return }

        def literal = String.valueOf(node.value)

        for (Class constantType: constantTypes) {
            if ((constantType.isAssignableFrom(node.value.class) || node.value.class == constantType || node.type.typeClass == constantType)) {
                if (constants.contains(literal) && !isStatic && !ignoreValuesSet.contains(literal)) {
                    addViolation node, "Duplicate ${constantType.simpleName} Literal: $literal"
                    return
                }
            }
        }
        constants.add literal
    }
}
