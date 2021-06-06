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

import org.codehaus.groovy.ast.ConstructorNode
import org.slf4j.LoggerFactory
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
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
        class OtherClass {
            int myIntField = 45 /*111*/
            String myStringField // comment

    /*999*/ def someMethod() {
                String var1 = 'abc'
                final int var2 = 99
                final var3 = 'x'

                @Field Map<String, String> var4 = new HashMap<String, String>(System.getenv())

                @Field
                Map<String, String> var5 = new HashMap<String, String>(System.getenv())

                @Field Map var6 = new HashMap<String, String>(System.getenv())

                @Field
                Map var7 = new HashMap<String, String>(System.getenv());

                @Field
                final Map var8 = new HashMap<String, String>(System.getenv())
            }

            @SuppressWarnings('bad') // comment
            void methodWithAnnotationAndComment() { }

            @SuppressWarnings
            OtherClass() { }    // constructor
        }

        @Ignore
        // Some comment
        @SuppressWarnings('Indentation')
        class SomeAnnotatedClass {

            @SuppressWarnings
            @Other('abc') // comment
            int annotatedField1

            @SuppressWarnings
            SomeAnnotatedClass() { }    // constructor
        }

        // outside of class -- script
        def scriptMethod() { 456 }

        // Annotated class as last line
        @SuppressWarnings
        class LastLineClass {}'''
    private visitor
    private sourceCode

    @Test
    void test_findClassDeclarationLineNumber() {
        def nonAnnotatedClassNode = classNamed('MyClass')
        assert AstUtil.findClassDeclarationLineNumber(nonAnnotatedClassNode, sourceCode) == 2

        def annotatedClassNode = classNamed('SomeAnnotatedClass')
        assert AstUtil.findClassDeclarationLineNumber(annotatedClassNode, sourceCode) == 57

        annotatedClassNode = classNamed('LastLineClass')
        assert AstUtil.findClassDeclarationLineNumber(annotatedClassNode, sourceCode) == 72
    }

    @Test
    void testIsFinalVariable() {
        assert !AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var1'), sourceCode)
        assert AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var2'), sourceCode)
        assert AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var3'), sourceCode)
        assert !AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var4'), sourceCode)
        assert !AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var5'), sourceCode)
        assert !AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var6'), sourceCode)
        assert !AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var7'), sourceCode)
        assert AstUtil.isFinalVariable(declarationExpressionForVariableNamed('var8'), sourceCode)
    }

    @Test
    void testIsFromGeneratedSourceCode() {
        def scriptClassNode = visitor.classNodes.find { classNode -> classNode.name == 'None' }
        assert AstUtil.isFromGeneratedSourceCode(scriptClassNode)
        assert !AstUtil.isFromGeneratedSourceCode(methodCallForMethodNamed('print'))
    }

    @Test
    void testGetNodeText() {
        assert AstUtil.getNodeText(methodCallForMethodNamed('methodCallWithinEnum'), sourceCode) == "methodCallWithinEnum(true, 'abc', 123)"
        assert AstUtil.getNodeText(methodCallForMethodNamed('multilineMethodCall'), sourceCode) == 'multilineMethodCall(1,'
    }

    @Test
    void testGetLastLineOfNodeText() {
        assert AstUtil.getLastLineOfNodeText(methodCallForMethodNamed('methodCallWithinEnum'), sourceCode) == "methodCallWithinEnum(true, 'abc', 123)"
        assert AstUtil.getLastLineOfNodeText(methodCallForMethodNamed('multilineMethodCall'), sourceCode).trim() == '2, 3)'
    }

    @Test
    void testGetDeclaration() {
        def node = fieldNamed('myIntField')
        assert AstUtil.getDeclaration(node, sourceCode).trim() == 'int myIntField = 45 /*111*/'

        node = fieldNamed('myStringField')
        assert AstUtil.getDeclaration(node, sourceCode).trim() == 'String myStringField // comment'

        node = visitor.methodNodes['otherMethod']
        assert AstUtil.getDeclaration(node, sourceCode).trim() == 'def otherMethod()'
    }

    @Test
    void test_getSourceLinesForNode() {
        def node = fieldNamed('myIntField')
        def lines = AstUtil.getSourceLinesForNode(node, sourceCode)
        assert lines == ['int myIntField = 45 /*111*/']

        node = fieldNamed('annotatedField1')
        lines = AstUtil.getSourceLinesForNode(node, sourceCode)
        log(lines)
        assert lines == ['@SuppressWarnings', "@Other(\'abc\') // comment", 'int annotatedField1']

        node = methodNamed('doStuff')
        lines = AstUtil.getSourceLinesForNode(node, sourceCode)
        log(lines)
        assert lines == ['def doStuff() {', 'println methodCallWithinEnum(true, \'abc\', 123); doStuff()', '}']
    }

    @Test
    void test_findFirstNonAnnotationLine() {
        // Methods
        assert AstUtil.findFirstNonAnnotationLine(methodNamed('otherMethod'), sourceCode) == 3
        assert AstUtil.findFirstNonAnnotationLine(methodNamed('setUp'), sourceCode) == 15
        assert AstUtil.findFirstNonAnnotationLine(methodNamed('twoAnnotationsMethod'), sourceCode) == 16
        assert AstUtil.findFirstNonAnnotationLine(methodNamed('someMethod'), sourceCode) == 28
        assert AstUtil.findFirstNonAnnotationLine(methodNamed('methodWithAnnotationAndComment'), sourceCode) == 48

        // Constructor
        log(visitor.constructors)
        def constructor = visitor.constructors.find { constructorNode -> constructorNode.declaringClass.name == 'OtherClass' }
        assert AstUtil.findFirstNonAnnotationLine(constructor, sourceCode) == 51

        // Classes
        assert AstUtil.findFirstNonAnnotationLine(classNamed('MyClass'), sourceCode) == 2
        assert AstUtil.findFirstNonAnnotationLine(classNamed('OtherClass'), sourceCode) == 24
        assert AstUtil.findFirstNonAnnotationLine(classNamed('SomeAnnotatedClass'), sourceCode) == 57

        // Fields
        assert AstUtil.findFirstNonAnnotationLine(fieldNamed('myIntField'), sourceCode) == 25
        assert AstUtil.findFirstNonAnnotationLine(fieldNamed('annotatedField1'), sourceCode) == 61
    }

    @Test
    void testGetMethodArguments_ConstructorWithinEnum() {
        def methodCall = methodCallForMethodNamed('methodCallWithinEnum')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 3
    }

    @Test
    void testGetMethodArguments_NoArgument() {
        def methodCall = methodCallForMethodNamed('print')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 0
    }

    @Test
    void testGetMethodArguments_SingleArgument() {
        def methodCall = methodCallForMethodNamed('stringMethodName')
        def args = AstUtil.getMethodArguments(methodCall)
        assert args.size() == 1
        assert args[0].value == 123
    }

    @Test
    void testGetMethodArguments_NamedArguments() {
        def methodCall = methodCallForMethodNamed('delete')
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
        def methodCall = methodCallForMethodNamed('stringMethodName')
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
        def methodCall = methodCallForMethodNamed('print')
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
        def variableExpressions = AstUtil.getVariableExpressions(visitor.declarationExpressions[0])
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

    @Test
    void test_getSourceBetweenNodes() {
        def fieldNode1 = fieldNamed('myIntField')
        def methodNode = methodNamed('someMethod')
        String sourceBetween1 = AstUtil.getSourceBetweenNodes(fieldNode1, methodNode, sourceCode)
        log("sourceBetween1=$sourceBetween1")
        if (GroovyVersion.isNotGroovyVersion2()) {
            assert sourceBetween1.contains('/*111*/')
        }
        assert sourceBetween1.contains('String myStringField')
        assert sourceBetween1.contains('/*999*/')

        def class1 = classNamed('MyClass')
        def class2 = classNamed('OtherClass')
        String sourceBetween2 = AstUtil.getSourceBetweenNodes(class1, class2, sourceCode)
        log("sourceBetween2=$sourceBetween2")
        assert sourceBetween2.contains('enum MyEnum')
        assert sourceBetween2.contains('println methodCallWithinEnum')
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

    private ClassNode classNamed(String name) {
        return visitor.classNodes.find { classNode -> classNode.name == name }
    }

    private FieldNode fieldNamed(String name) {
        return visitor.fieldNodes.find { fieldNode -> fieldNode.name == name }
    }

    private MethodNode methodNamed(String name) {
        return visitor.methodNodes[name]
    }

    private MethodCallExpression methodCallForMethodNamed(String name) {
        def methodCall = visitor.methodCallExpressions.find { mc ->
            if (mc.method instanceof GStringExpression) {
                return mc.text.startsWith(name)
            }
            mc.method.value == name
        }
        methodCall
    }

    private DeclarationExpression declarationExpressionForVariableNamed(String name) {
        return visitor.declarationExpressions.find { declarationExpression -> declarationExpression.variableExpression.name == name }
    }

}

class AstUtilTestVisitor extends ClassCodeVisitorSupport {
    static final LOG = LoggerFactory.getLogger(AstUtilTestVisitor)
    def methodNodes = [:]
    def methodCallExpressions = []
    def constructors = []
    def statements = []
    def declarationExpressions = []
    def classNodes = []
    def fieldNodes = []

    @Override
    void visitClass(ClassNode node) {
        classNodes << node
        super.visitClass(node)
    }

    @Override
    void visitMethod(MethodNode methodNode) {
        methodNodes[methodNode.name] = methodNode
        super.visitMethod(methodNode)
    }

    @Override
    void visitConstructor(ConstructorNode node) {
        this.constructors << node
        super.visitConstructor(node)
    }

    @Override
    void visitStatement(Statement statement) {
        this.statements << statement
        super.visitStatement(statement)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        this.methodCallExpressions << methodCallExpression
        super.visitMethodCallExpression(methodCallExpression)
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        declarationExpressions << declarationExpression
        super.visitDeclarationExpression(declarationExpression)
    }

    @Override
    void visitField(FieldNode node) {
        fieldNodes << node
        super.visitField(node)
    }

    @Override
    protected SourceUnit getSourceUnit() {
        source
    }
}
