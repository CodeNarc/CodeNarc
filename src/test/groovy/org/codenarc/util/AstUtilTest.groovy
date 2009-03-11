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
            }
        }
    '''
    private visitor

    void testIsMethodCall_ExactMatch() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert AstUtil.isMethodCall(statement, 'object', 'print', 0)
    }

    void testIsMethodCall_WrongMethodName() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print2', 0)
    }

    void testIsMethodCall_WrongMethodObjectName() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object2', 'print', 0)
    }

    void testIsMethodCall_WrongNumberOfArguments() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 1)
    }

    void testIsMethodCall_NotAMethodCall() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 0)
    }

    void testIsBlock_Block() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert AstUtil.isBlock(statement)
    }

    void testIsBlock_NotABlock() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isBlock(statement)
    }

    void testIsEmptyBlock_NonEmptyBlock() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    void testIsEmptyBlock_EmptyBlock() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof IfStatement }
        assert AstUtil.isEmptyBlock(statement.ifBlock)
    }

    void testIsEmptyBlock_NotABlock() {
        applyVisitor(SOURCE_METHOD_CALL)
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    void setUp() {
        super.setUp()
        visitor = new AstUtilTestVisitor()
    }

    private void applyVisitor(String source) {
        def sourceCode = new SourceString(source)
        def ast = sourceCode.ast
        ast.classes.each { classNode -> visitor.visitClass(classNode) }
    }
}

class AstUtilTestVisitor extends ClassCodeVisitorSupport {
    def methodNode
    def methodCallExpressions = []
    def statements = []

    void visitMethod(MethodNode methodNode) {
        println("visitMethod name=${methodNode.name}")
        this.methodNode = methodNode
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
