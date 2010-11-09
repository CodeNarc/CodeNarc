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

import org.codenarc.rule.AbstractAstVisitor
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * Abstract superclass for rule AstVisitor classes that detect duplicate literal constants
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 * @version $Revision: 428 $ - $Date: 2010-11-06 16:36:05 -0400 (Sat, 06 Nov 2010) $
 */
class DuplicateLiteralAstVisitor extends AbstractAstVisitor {

    List<String> constants = []
    private Class constantType

    DuplicateLiteralAstVisitor(Class constantType) {
        assert constantType
        this.constantType = constantType
    }

    def void visitClassEx(ClassNode node) {
        constants.clear()
    }

    def void visitArgumentlistExpression(ArgumentListExpression expression) {
        expression.expressions.each {
            addViolationIfDuplicate(it)
        }
        super.visitArgumentlistExpression expression
    }

    def void visitMethodCallExpression(MethodCallExpression call) {
        addViolationIfDuplicate(call.objectExpression)
        super.visitMethodCallExpression call
    }

    def void visitListExpression(ListExpression expression) {
        expression.expressions.findAll {
            addViolationIfDuplicate it
        }
        super.visitListExpression expression
    }

    def void visitFieldEx(FieldNode node) {
        addViolationIfDuplicate(node.initialValueExpression, node.isStatic())
        super.visitFieldEx node
    }

    def void visitBinaryExpression(BinaryExpression expression) {
        addViolationIfDuplicate expression.leftExpression
        addViolationIfDuplicate expression.rightExpression
        super.visitBinaryExpression expression
    }

    def void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        addViolationIfDuplicate expression.booleanExpression
        addViolationIfDuplicate expression.trueExpression
        addViolationIfDuplicate expression.falseExpression
        super.visitShortTernaryExpression expression
    }

    def void visitReturnStatement(ReturnStatement statement) {
        addViolationIfDuplicate(statement.expression)
        super.visitReturnStatement statement
    }

    def void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
        call.arguments.each {
            addViolationIfDuplicate(it)
        }
        super.visitStaticMethodCallExpression call
    }

    def void visitMapEntryExpression(MapEntryExpression expression) {
        //addViolationIfDuplicate expression.keyExpression
        addViolationIfDuplicate expression.valueExpression
        super.visitMapEntryExpression expression
    }

    def addViolationIfDuplicate(node, boolean isStatic = false) {
        if (isFirstVisit(node) && (node instanceof ConstantExpression) && node.value != null && (constantType.isAssignableFrom(node.value.class))) {
            def literal = node.value
            if (constants.contains(literal) && !isStatic) {
                addViolation node, "Duplicate ${constantType.simpleName} Literal: $literal"
            } else {
                constants.add literal
            }
        }
    }
}
