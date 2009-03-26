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
package org.codenarc.util

import org.codenarc.test.AbstractTest
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.source.SourceString
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode

/**
 * Tests for AstUtil
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AstUtilTest extends AbstractTest {
    static final SOURCE_METHOD_CALL = '''
        class MyClass {
            def otherMethod() {
                object.print()
                if (true) {
                }
                ant.delete(dir:appBase, failonerror:false)
                "stringMethodName"(123)
                gstringMethodName = 'anotherMethod'
                "$gstringMethodName"(234)
            }
            @Before setUp() {  }
        }
    '''
    private visitor

    void testIsMethodCall_ExactMatch() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert AstUtil.isMethodCall(statement, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    void testIsMethodCall_WrongMethodName() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2')
    }

    void testIsMethodCall_WrongMethodObjectName() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print')
    }

    void testIsMethodCall_WrongNumberOfArguments() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 1)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print', 1)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    void testIsMethodCall_NamedArgumentList() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method.value == 'delete' }
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete', 2)
        assert !AstUtil.isMethodCall(methodCall, 'ant', 'delete', 1)
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete')
    }

    void testIsMethodCall_StringLiteralMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method.value == 'stringMethodName' }
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 2)
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName')
    }

    void testIsMethodCall_GStringMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> println mc.method; mc.method instanceof GStringExpression }
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 2)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod')
    }

    void testIsMethodCall_NotAMethodCall() {
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 0)
    }

    void testIsBlock_Block() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert AstUtil.isBlock(statement)
    }

    void testIsBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isBlock(statement)
    }

    void testIsEmptyBlock_NonEmptyBlock() {
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    void testIsEmptyBlock_EmptyBlock() {
        def statement = visitor.statements.find { st -> st instanceof IfStatement }
        assert AstUtil.isEmptyBlock(statement.ifBlock)
    }

    void testIsEmptyBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    void testGetAnnotation() {
        assert AstUtil.getAnnotation(visitor.methodNodes['otherMethod'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'Before') instanceof AnnotationNode
    }

    void setUp() {
        super.setUp()
        visitor = new AstUtilTestVisitor()
        applyVisitor(SOURCE_METHOD_CALL)
    }

    private void applyVisitor(String source) {
        def sourceCode = new SourceString(source)
        def ast = sourceCode.ast
        ast.classes.each { classNode -> visitor.visitClass(classNode) }
    }
}

class AstUtilTestVisitor extends ClassCodeVisitorSupport {
    def methodNodes = [:]
    def methodCallExpressions = []
    def statements = []

    void visitMethod(MethodNode methodNode) {
        println("visitMethod name=${methodNode.name}")
        methodNodes[methodNode.name] = methodNode
        super.visitMethod(methodNode)
    }

    void visitStatement(Statement statement) {
        println("visitStatement text=${statement.text}")
        this.statements << statement
        super.visitStatement(statement)
    }

    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        println("visitMethodCallExpression object=${methodCallExpression.objectExpression}")
        this.methodCallExpressions << methodCallExpression
        super.visitMethodCallExpression(methodCallExpression)
    }

    protected SourceUnit getSourceUnit() {
        return source
    }
}
