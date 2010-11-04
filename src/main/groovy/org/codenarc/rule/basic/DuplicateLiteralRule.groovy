/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.MethodNode

/**
 * Code containing duplicate String literals can usually be improved by declaring the String as a constant field.
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class DuplicateLiteralRule extends AbstractAstVisitorRule {
    static final RULE_NAME = 'DuplicateLiteral'
    String name = RULE_NAME
    int priority = 2
    Class astVisitorClass = DuplicateLiteralAstVisitor
    String doNotApplyToFilesMatching = DEFAULT_TEST_FILES
}

class DuplicateLiteralAstVisitor extends AbstractAstVisitor {

    List<String> constants = []

    private static boolean suppressionIsPresent(AnnotatedNode node, String ruleName) {
        def annos = node.annotations?.findAll { it.classNode?.name == 'SuppressWarnings' }
        for (AnnotationNode annotation: annos) {
            if (annotation.members?.value instanceof ConstantExpression && annotation.members?.value.value == ruleName) {
                return true
            }
        }
        return false
    }

    def void visitClass(ClassNode node) {
        constants.clear()
        if (!suppressionIsPresent(node, DuplicateLiteralRule.RULE_NAME)) {
            super.visitClass node
        }
    }

    def void visitMethod(MethodNode node) {
        if (!suppressionIsPresent(node, DuplicateLiteralRule.RULE_NAME)) {
            super.visitMethod node
        }
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

    def void visitField(FieldNode node) {
        addViolationIfDuplicate(node.initialValueExpression, node.isStatic())
        super.visitField node
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
        if (isFirstVisit(node) && (node instanceof ConstantExpression) && (node.value instanceof String)) {
            def literal = node.value
            if (constants.contains(literal) && !isStatic) {
                addViolation node, 'Duplicate Literal: ' + literal
            } else {
                constants.add literal
            }
        }
    }
}
