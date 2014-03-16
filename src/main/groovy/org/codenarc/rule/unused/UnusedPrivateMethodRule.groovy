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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractSharedAstVisitorRule
import org.codenarc.rule.AstVisitor
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

import org.codehaus.groovy.ast.expr.*

/**
 * Rule that checks for private methods that are not referenced within the same class.
 *
 * Known limitations:
 * <ul>
 *  <li>Does not handle method reference through property access: getName() accessed as x.name</li>
 *  <li>Does not handle method invocations when method name is a GString (e.g. this."${methodName}"</li>
 *  <li>Does not handle invoking private method of another instance (i.e. other than 'this')</li>
 *  <li>Does not differentiate between multiple private methods with the same name but different parameters (i.e., overloaded)</li>
 *  <li>Does not check constructors</li>
 * </ul>
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class UnusedPrivateMethodRule extends AbstractSharedAstVisitorRule {

    String name = 'UnusedPrivateMethod'
    int priority = 2

    @Override
    protected AstVisitor getAstVisitor(SourceCode sourceCode) {
        def allPrivateMethods = collectAllPrivateMethods(sourceCode.ast)
        return new UnusedPrivateMethodAstVisitor(allPrivateMethods, sourceCode.ast.classes*.name)
    }

    @Override
    protected List<Violation> getViolations(AstVisitor visitor, SourceCode sourceCode) {
        visitor.unusedPrivateMethods.each { key, value ->
            visitor.addViolation(value, "The method $key is not used within ${sourceCode.name ?: 'the class'}")
        }
        return visitor.violations
    }

    @SuppressWarnings('NestedBlockDepth')
    private collectAllPrivateMethods(ast) {
        def allPrivateMethods = [:]
        ast.classes.each { classNode ->

            if (shouldApplyThisRuleTo(classNode)) {
                classNode.methods.inject(allPrivateMethods) { acc, methodNode ->
                    if ((Modifier.isPrivate(methodNode.modifiers)) && !allPrivateMethods.containsKey(methodNode.name)) {
                        allPrivateMethods.put(methodNode.name, methodNode)
                    }
                }
            }
        }
        allPrivateMethods
    }
}

@SuppressWarnings('DuplicateLiteral')
class UnusedPrivateMethodAstVisitor extends AbstractAstVisitor {

    private final Map<String, MethodNode> unusedPrivateMethods
    private final List<String> classNames

    UnusedPrivateMethodAstVisitor(Map<String, MethodNode> unusedPrivateMethods, List<String> classNames) {
        this.classNames = classNames.inject(['this']) { acc, value ->
            acc.add value
            if (value.contains('$') && !value.endsWith('$')) {
                acc.add value[value.lastIndexOf('$') + 1..-1]
            } else if (value.contains('.') && !value.endsWith('.')) {
                acc.add value[value.lastIndexOf('.') + 1..-1]
            }
            acc
        }
        this.unusedPrivateMethods = unusedPrivateMethods
    }

    void visitMethodCallExpression(MethodCallExpression expression) {
        classNames.each {
            if (isMethodCall(expression, it)) {
                unusedPrivateMethods.remove(expression.method.value)
            }
        }

        // Static invocation through current class name
        if (isMethodCall(expression, currentClassNode.nameWithoutPackage)) {
            unusedPrivateMethods.remove(expression.method.value)
        }
        super.visitMethodCallExpression(expression)
    }

    void visitMethodPointerExpression(MethodPointerExpression methodPointerExpression) {
        if (methodPointerExpression.expression instanceof VariableExpression &&
                classNames.contains(methodPointerExpression.expression.name) &&
                methodPointerExpression.methodName instanceof ConstantExpression) {

            unusedPrivateMethods.remove(methodPointerExpression.methodName.value)
        }
        super.visitMethodPointerExpression(methodPointerExpression)
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        if (expression.name.size() == 1) {
            unusedPrivateMethods.remove('get' + expression.name.toUpperCase())
            unusedPrivateMethods.remove('is' + expression.name.toUpperCase())
            unusedPrivateMethods.remove('set' + expression.name.toUpperCase())
        } else {
            unusedPrivateMethods.remove('get' + expression.name[0].toUpperCase() + expression.name[1..-1])
            unusedPrivateMethods.remove('is' + expression.name[0].toUpperCase() + expression.name[1..-1])
            unusedPrivateMethods.remove('set' + expression.name[0].toUpperCase() + expression.name[1..-1])
        }
        super.visitVariableExpression expression
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {

        if (isConstantString(expression.property)) {
            def propertyName = expression.property.value
            if (propertyName.size() == 1) {
                def propertyNameUpperCase = expression.property.value.toUpperCase()
                unusedPrivateMethods.remove('get' + propertyNameUpperCase)
                unusedPrivateMethods.remove('set' + propertyNameUpperCase)
            } else {
                if (propertyName) {
                    unusedPrivateMethods.remove('get' + propertyName[0].toUpperCase() + propertyName[1..-1])
                    unusedPrivateMethods.remove('set' + propertyName[0].toUpperCase() + propertyName[1..-1])
                }
            }
        }
        super.visitPropertyExpression expression
    }

    private static boolean isConstantString(Expression expression) {
        expression instanceof ConstantExpression && expression.value instanceof String
    }

    private static boolean isMethodCall(MethodCallExpression expression, String targetName) {
        AstUtil.isMethodCallOnObject(expression, targetName) &&
                expression.method instanceof ConstantExpression
    }
}
