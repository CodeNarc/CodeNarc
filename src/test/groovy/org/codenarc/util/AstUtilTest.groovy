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

import org.apache.log4j.Logger
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

/**
 * Tests for AstUtil
 *
 * @author Chris Mair
  */
class AstUtilTest extends AbstractTestCase {

    private static final SOURCE = '''
        class MyClass {
            def otherMethod() {
                object.print()
                if (true) {
                }
                ant.delete(dir:appBase, failonerror:false)
                "stringMethodName"(123)
                gstringMethodName = 'anotherMethod'
                "$gstringMethodName"(234)
                int myVariable = 99
                multilineMethodCall(1,
                    2, 3)
            }
            @Before setUp() {  }
            @First @Second def twoAnnotationsMethod() { }
        }
        enum MyEnum {
            READ, WRITE
            def doStuff() {
                println methodCallWithinEnum(true, 'abc', 123); doStuff()
            }
        }

        // outside of class -- script
        def scriptMethod() { 456 }
    '''
    private visitor
    private sourceCode

    @Test
    void testIsFromGeneratedSourceCode() {
        def scriptClassNode = visitor.classNodes.find { classNode -> classNode.name == 'None' }
        assert AstUtil.isFromGeneratedSourceCode(scriptClassNode)
        assert !AstUtil.isFromGeneratedSourceCode(methodNamed('print'))
    }

    @Test
    void testGetNodeTest() {
        assert AstUtil.getNodeText(methodNamed('methodCallWithinEnum'), sourceCode) == "methodCallWithinEnum(true, 'abc', 123)"
        assert AstUtil.getNodeText(methodNamed('multilineMethodCall'), sourceCode) == 'multilineMethodCall(1,'
    }

    @Test
    void testGetMethodArguments_ConstructorWithinEnum() {
        def methodCall = methodNamed('methodCallWithinEnum')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 3
    }

    @Test
    void testGetMethodArguments_NoArgument() {
        def methodCall = methodNamed('print')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 0
    }

    @Test
    void testGetMethodArguments_SingleArgument() {
        def methodCall = methodNamed('stringMethodName')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 1
        assert args[0].value == 123
    }

    @Test
    void testGetMethodArguments_NamedArguments() {
        def methodCall = methodNamed('delete')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 1
        assert args[0].mapEntryExpressions[1].keyExpression.value == 'failonerror'
        assert !args[0].mapEntryExpressions[1].valueExpression.value
    }

    @Test
    void testIsMethodCall_ExactMatch() {
        def statement = expressionStatementForMethodNamed('print')
        assert AstUtil.isMethodCall(statement, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print', 0)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    @Test
    void testIsMethodCall_WrongMethodName() {
        def statement = expressionStatementForMethodNamed('print')
        assert !AstUtil.isMethodCall(statement, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print2')
    }

    @Test
    void testIsMethodCall_WrongMethodObjectName() {
        def statement = expressionStatementForMethodNamed('print')
        assert !AstUtil.isMethodCall(statement, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print', 0)
        assert !AstUtil.isMethodCall(statement.expression, 'object2', 'print')
    }

    @Test
    void testIsMethodCall_WrongNumberOfArguments() {
        def statement = expressionStatementForMethodNamed('print')
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 1)
        assert !AstUtil.isMethodCall(statement.expression, 'object', 'print', 1)
        assert AstUtil.isMethodCall(statement.expression, 'object', 'print')
    }

    @Test
    void testIsMethodCall_NamedArgumentList() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method.value == 'delete' }
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete', 1)
        assert !AstUtil.isMethodCall(methodCall, 'ant', 'delete', 2)
        assert AstUtil.isMethodCall(methodCall, 'ant', 'delete')
    }

    @Test
    void testIsMethodCall_StringLiteralMethodName() {
        def methodCall = methodNamed('stringMethodName')
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName', 2)
        assert AstUtil.isMethodCall(methodCall, 'this', 'stringMethodName')
    }

    @Test
    void testIsMethodCall_GStringMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> log(mc.method); mc.method instanceof GStringExpression }
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 1)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod', 2)
        assert !AstUtil.isMethodCall(methodCall, 'this', 'anotherMethod')
    }

    @Test
    void testIsMethodCall_NotAMethodCall() {
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert !AstUtil.isMethodCall(statement, 'object', 'print', 0)
    }

    @Test
    void testIsMethodNamed() {
        def methodCall = methodNamed('print')
        assert AstUtil.isMethodNamed(methodCall, 'print')
        assert !AstUtil.isMethodNamed(methodCall, 'other')
    }

    @Test
    void testIsMethodNamed_GStringMethodName() {
        def methodCall = visitor.methodCallExpressions.find { mc -> mc.method instanceof GStringExpression }
        assert !AstUtil.isMethodNamed(methodCall, 'print')
    }

    @Test
    void testIsBlock_Block() {
        applyVisitor(SOURCE)
        def statement = visitor.statements.find { st -> st instanceof BlockStatement }
        assert AstUtil.isBlock(statement)
    }

    @Test
    void testIsBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isBlock(statement)
    }

    @Test
    void testIsEmptyBlock_NonEmptyBlock() {
        def statement = expressionStatementForMethodNamed('setUp')
        assert !AstUtil.isEmptyBlock(statement)
    }

    @Test
    void testIsEmptyBlock_EmptyBlock() {
        def statement = visitor.statements.find { st -> st instanceof IfStatement }
        assert AstUtil.isEmptyBlock(statement.ifBlock)
    }

    @Test
    void testIsEmptyBlock_NotABlock() {
        def statement = visitor.statements.find { st -> st instanceof ExpressionStatement }
        assert !AstUtil.isEmptyBlock(statement)
    }

    @Test
    void testGetAnnotation() {
        assert AstUtil.getAnnotation(visitor.methodNodes['otherMethod'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'doesNotExist') == null
        assert AstUtil.getAnnotation(visitor.methodNodes['setUp'], 'Before') instanceof AnnotationNode
    }

    @Test
    void testHasAnnotation() {
        assert !AstUtil.hasAnnotation(visitor.methodNodes['setUp'], 'doesNotExist')
        assert AstUtil.hasAnnotation(visitor.methodNodes['setUp'], 'Before')
    }

    @Test
    void testHasAnyAnnotation() {
        assert !AstUtil.hasAnyAnnotation(visitor.methodNodes['twoAnnotationsMethod'], 'doesNotExist')
        assert AstUtil.hasAnyAnnotation(visitor.methodNodes['twoAnnotationsMethod'], 'First')
        assert AstUtil.hasAnyAnnotation(visitor.methodNodes['twoAnnotationsMethod'], 'doesNotExist', 'First')
        assert AstUtil.hasAnyAnnotation(visitor.methodNodes['twoAnnotationsMethod'], 'doesNotExist', 'First', 'Second')
    }

    @Test
    void testGetVariableExpressions_SingleDeclaration() {
        log("declarationExpressions=${visitor.declarationExpressions}")
        def variableExpressions = AstUtil.getVariableExpressions(visitor.declarationExpressions[0])
        log("variableExpressions=$variableExpressions")
        assert variableExpressions.size() == 1
        assert variableExpressions.name == ['myVariable']
    }

    @Test
    void testGetVariableExpressions_MultipleDeclarations() {
        final NEW_SOURCE = '''
            class MyClass {
                def otherMethod() {
                    String name1, name2 = 'abc'
                }
            }
        '''
        applyVisitor(NEW_SOURCE)
        def names = []
        visitor.declarationExpressions.collect { declarationExpression ->
            names += AstUtil.getVariableExpressions(declarationExpression).name
        }
        assert names.contains('name1') && names.contains('name2')
    }

    @Before
    void setUpAstUtilTest() {
        visitor = new AstUtilTestVisitor()
        applyVisitor(SOURCE)
    }

    private void applyVisitor(String source) {
        sourceCode = new SourceString(source)
        def ast = sourceCode.ast
        ast.classes.each { classNode -> visitor.visitClass(classNode) }
    }

    private ExpressionStatement expressionStatementForMethodNamed(String methodName) {
        return visitor.statements.find { st ->
            st instanceof ExpressionStatement &&
            st.expression instanceof MethodCallExpression &&
            st.expression.methodAsString == methodName }
    }

    private MethodCallExpression methodNamed(String name) {
        def methodCall = visitor.methodCallExpressions.find { mc ->
            if (mc.method instanceof GStringExpression) {
                return mc.text.startsWith(name)
            }
            mc.method.value == name
        }
        methodCall
    }
}

class AstUtilTestVisitor extends ClassCodeVisitorSupport {
    static final LOG = Logger.getLogger(AstUtilTestVisitor)
    def methodNodes = [:]
    def methodCallExpressions = []
    def statements = []
    def declarationExpressions = []
    def classNodes = []

    @Override
    void visitClass(ClassNode node) {
        classNodes << node
        super.visitClass(node)
    }

    void visitMethod(MethodNode methodNode) {
        methodNodes[methodNode.name] = methodNode
        super.visitMethod(methodNode)
    }

    void visitStatement(Statement statement) {
        this.statements << statement
        super.visitStatement(statement)
    }

    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        this.methodCallExpressions << methodCallExpression
        super.visitMethodCallExpression(methodCallExpression)
    }

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        declarationExpressions << declarationExpression
        super.visitDeclarationExpression(declarationExpression)
    }

    protected SourceUnit getSourceUnit() {
        source
    }
}
