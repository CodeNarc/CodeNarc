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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.MethodPointerExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.source.SourceCode
import org.codenarc.util.AstUtil

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
 * @version $Revision$ - $Date$
 */
class UnusedPrivateMethodRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateMethod'
    int priority = 2

    @Override
    void applyTo(SourceCode sourceCode, List violations) {
        // If AST is null, skip this source code
        def ast = sourceCode.ast
        if (!ast) { return }

        def allPrivateMethods = collectAllPrivateMethods(ast)

        def visitor = new UnusedPrivateMethodAstVisitor(allPrivateMethods, ast.classes.collect { it.name })
        visitor.rule = this
        visitor.sourceCode = sourceCode
        ast.classes.each { classNode ->
            visitor.visitClass(classNode)
        }

        allPrivateMethods.each { key, value ->
            visitor.addViolation(value, "The method $key is not used within ${sourceCode.name ?: 'the class'}")
        }
        violations.addAll(visitor.violations)
    }

    @SuppressWarnings('NestedBlockDepth')
    private collectAllPrivateMethods(ast) {
        def allPrivateMethods = [:]
        ast.classes.each { classNode ->

            if (shouldApplyThisRuleTo(classNode)) {
                classNode.methods.inject(allPrivateMethods) { acc, methodNode ->
                    if ((methodNode.modifiers & FieldNode.ACC_PRIVATE) && !allPrivateMethods.containsKey(methodNode.name)) {
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
    private Map<String, MethodNode> unusedPrivateMethods
    private List<String> classNames
    private currentClassNode


    UnusedPrivateMethodAstVisitor(Map<String, MethodNode> unusedPrivateMethods, List<String> classNames) {
        this.unusedPrivateMethods = unusedPrivateMethods
        this.classNames = ['this'] + classNames
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        currentClassNode = node
    }

    @Override
    protected void visitClassComplete(ClassNode node) {
        currentClassNode = null
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

    private static boolean isMethodCall(MethodCallExpression expression, String targetName) {
        AstUtil.isMethodCallOnObject(expression, targetName) &&
                expression.method instanceof ConstantExpression
    }
}