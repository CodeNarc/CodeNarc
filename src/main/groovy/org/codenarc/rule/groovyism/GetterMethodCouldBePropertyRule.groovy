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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * If a class defines a public method that follows the Java getter notation and returns a constant,
 * then it is cleaner to provide a Groovy property for the value rather than a Groovy method.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class GetterMethodCouldBePropertyRule extends AbstractAstVisitorRule {

    String name = 'GetterMethodCouldBeProperty'
    int priority = 3
    boolean ignoreMethodsWithOverrideAnnotation = false
    Class astVisitorClass = GetterMethodCouldBePropertyAstVisitor
}

class GetterMethodCouldBePropertyAstVisitor extends AbstractAstVisitor {

    private static final String CONSTANT_NAME_REGEX = /[A-Z].*/

    private final List staticFinalFieldNames = []

    @Override
    protected void visitClassEx(ClassNode node) {
        staticFinalFieldNames.addAll(node.fields.findAll { it.isStatic() && it.isFinal() }*.name)
        super.visitClassEx(node)
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        staticFinalFieldNames.clear()
        super.visitClassComplete(node)
    }

    @Override
    protected void visitMethodEx(MethodNode node) {
        if (isAGetterMethod(node) &&
                node.isPublic() &&
                isNotAnIgnoredOverrideMethod(node) &&
                AstUtil.isOneLiner(node.code)) {
            def statement = node.code.statements[0]
            if (statement instanceof ExpressionStatement || statement instanceof ReturnStatement) {
                checkStatementExpression(node, statement.expression)
            }
        }
        super.visitMethodEx(node)
    }

    private void checkStatementExpression(MethodNode node, Expression statementExpression) {
        boolean isViolation =
                statementExpression instanceof ConstantExpression ||
                isNamedLikeAConstant(statementExpression) ||
                isAStaticFinalFieldValue(statementExpression)
        if (isViolation) {
            addViolation(node, createMessage(node))
        }
    }

    private boolean isAGetterMethod(MethodNode node) {
        AstUtil.isMethodNode(node, 'get[A-Z].*', 0)
    }

    private boolean isNamedLikeAConstant(Expression statementExpression) {
        statementExpression instanceof VariableExpression && statementExpression.text ==~ CONSTANT_NAME_REGEX
    }

    private boolean isAStaticFinalFieldValue(Expression statementExpression) {
        statementExpression instanceof VariableExpression && staticFinalFieldNames.contains(statementExpression.name)
    }

    private boolean isNotAnIgnoredOverrideMethod(MethodNode node) {
        return !rule.ignoreMethodsWithOverrideAnnotation || !AstUtil.hasAnnotation(node, 'Override')
    }

    private String createMessage(MethodNode node) {
        def constant = node.code.statements[0].expression
        def methodName = node.name
        def methodType = node.returnType.nameWithoutPackage
        def staticModifier = node.isStatic() ? 'static ' : ''
        def propertyName = node.name[3].toLowerCase() + (node.name.length() == 4 ? '' : node.name[4..-1])

        def constantValue
        switch(constant) {
            case ConstantExpression:
                constantValue = constant.value instanceof String ? "'" + constant.value + "'" : constant.value
                break
            case ClassExpression:
                constantValue = constant.text
                break
            case VariableExpression:
                constantValue = constant.name
                break
            default:
                constantValue = '<UNKNOWN>'
        }

        "The method '$methodName ' in class $currentClassName can be expressed more simply as the field declaration\n" +
            "${staticModifier}final $methodType $propertyName = $constantValue"
    }
}
