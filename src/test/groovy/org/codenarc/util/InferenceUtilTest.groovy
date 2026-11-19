/*
 * Copyright 2026 the original author or authors.
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

import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTestCase
import org.junit.jupiter.api.Test

import java.lang.reflect.Modifier
import java.util.concurrent.locks.ReentrantLock

/**
 * Tests for InferenceUtil
 */
class InferenceUtilTest extends AbstractTestCase {

    private static final SOURCE = '''
        import java.util.concurrent.locks.ReentrantLock

        class MyClass {
            // Declared primitive types
            boolean primitiveBoolean
            byte primitiveByte
            char primitiveChar
            short primitiveShort
            int primitiveInt
            long primitiveLong
            float primitiveFloat
            double primitiveDouble

            // Declared wrapper types
            Boolean wrapperBoolean
            Byte wrapperByte
            Character wrapperCharacter
            Short wrapperShort
            Integer wrapperInteger
            Long wrapperLong
            Float wrapperFloat
            Double wrapperDouble

            // Other declared types
            String declaredString
            ReentrantLock declaredLock
            java.util.concurrent.locks.ReentrantLock fullyQualifiedLock
            String[] stringArray
            int[] intArray
            Object[] objectArray
            Object declaredObject
            List declaredList
            Map declaredMap
            BigDecimal declaredBigDecimal
            Date declaredDate

            // Untyped fields; type inferred from the initial expression
            def untypedNoValue
            def untypedNull = null
            def untypedString = 'abc'
            def untypedGString = "abc${123}"
            def untypedTrue = true
            def untypedFalse = false
            def untypedInteger = 123
            def untypedLong = 123L
            def untypedFloat = 1.5f
            def untypedDouble = 1.5d
            def untypedBigDecimal = 1.5
            def untypedList = [1, 2, 3]
            def untypedMap = [a:1]
            def untypedNewLock = new ReentrantLock()
            def untypedNewObject = new Object()
            def untypedCastToString = (String) someValue
            def untypedCastToLong = (Long) someValue
            def untypedAsString = someValue as String
            def untypedMethodCall = someMethod()
            def untypedClosure = { 123 }

            // Declared type takes precedence over initial expression
            Object objectWithString = 'abc'
            String stringWithNull = null
            long longWithInteger = 123
        }

        class MyLock extends ReentrantLock { }
    '''

    private static final Map<String, FieldNode> FIELDS = parseFields(SOURCE)

    //------------------------------------------------------------------------------------
    // Declared primitive types are mapped to their wrapper types
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_DeclaredPrimitiveTypes() {
        assertFieldType('primitiveBoolean', Boolean)
        assertFieldType('primitiveByte', Byte)
        assertFieldType('primitiveChar', Character)
        assertFieldType('primitiveShort', Short)
        assertFieldType('primitiveInt', Integer)
        assertFieldType('primitiveLong', Long)
        assertFieldType('primitiveFloat', Float)
        assertFieldType('primitiveDouble', Double)
    }

    @Test
    void test_getFieldType_DeclaredWrapperTypes() {
        assertFieldType('wrapperBoolean', Boolean)
        assertFieldType('wrapperByte', Byte)
        assertFieldType('wrapperCharacter', Character)
        assertFieldType('wrapperShort', Short)
        assertFieldType('wrapperInteger', Integer)
        assertFieldType('wrapperLong', Long)
        assertFieldType('wrapperFloat', Float)
        assertFieldType('wrapperDouble', Double)
    }

    //------------------------------------------------------------------------------------
    // Other declared types
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_DeclaredString() {
        assertFieldType('declaredString', String)
    }

    @Test
    void test_getFieldType_DeclaredReentrantLock() {
        assertFieldType('declaredLock', ReentrantLock)
        assertFieldType('fullyQualifiedLock', ReentrantLock)
    }

    @Test
    void test_getFieldType_DeclaredSubclassOfReentrantLock() {
        def lockSubclassNode = new ClassNode('MyLock', Modifier.PUBLIC, ClassHelper.make(ReentrantLock))
        assert InferenceUtil.getFieldType(fieldNode(lockSubclassNode)) == ReentrantLock
    }

    @Test
    void test_getFieldType_DeclaredArrays() {
        assertFieldType('stringArray', Object[])
        assertFieldType('objectArray', Object[])
    }

    @Test
    void test_getFieldType_DeclaredPrimitiveArray_NotInferred() {
        // Current limitation: the ClassNode name for a primitive array is a JVM descriptor (e.g. "[I"),
        // which does not end with "[]", so it is not recognized as an array
        assertFieldType('intArray', null)
    }

    @Test
    void test_getFieldType_ArrayClassNodeNameEndingWithBrackets() {
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.make('MyType[]'))) == Object[]
    }

    @Test
    void test_getFieldType_DeclaredUnsupportedTypes() {
        assertFieldType('declaredObject', null)
        assertFieldType('declaredList', null)
        assertFieldType('declaredMap', null)
        assertFieldType('declaredBigDecimal', null)
        assertFieldType('declaredDate', null)
    }

    //------------------------------------------------------------------------------------
    // Untyped fields; type inferred from the initial expression
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_Untyped_NoInitialValue() {
        assertFieldType('untypedNoValue', null)
    }

    @Test
    void test_getFieldType_Untyped_Null() {
        assertFieldType('untypedNull', null)
    }

    @Test
    void test_getFieldType_Untyped_StringLiteral() {
        assertFieldType('untypedString', String)
    }

    @Test
    void test_getFieldType_Untyped_GString() {
        assertFieldType('untypedGString', null)
    }

    @Test
    void test_getFieldType_Untyped_BooleanLiterals() {
        assertFieldType('untypedTrue', Boolean)
        assertFieldType('untypedFalse', Boolean)
    }

    @Test
    void test_getFieldType_Untyped_NumericLiterals() {
        assertFieldType('untypedInteger', Integer)
        assertFieldType('untypedLong', Long)
        assertFieldType('untypedFloat', Float)
        assertFieldType('untypedDouble', Double)
    }

    @Test
    void test_getFieldType_Untyped_BigDecimalLiteral() {
        assertFieldType('untypedBigDecimal', null)
    }

    @Test
    void test_getFieldType_Untyped_ListAndMapLiterals() {
        assertFieldType('untypedList', null)
        assertFieldType('untypedMap', null)
    }

    @Test
    void test_getFieldType_Untyped_ConstructorCall() {
        assertFieldType('untypedNewLock', ReentrantLock)
        assertFieldType('untypedNewObject', null)
    }

    @Test
    void test_getFieldType_Untyped_Cast() {
        assertFieldType('untypedCastToString', String)
        assertFieldType('untypedCastToLong', Long)
        assertFieldType('untypedAsString', String)
    }

    @Test
    void test_getFieldType_Untyped_OtherExpressions() {
        assertFieldType('untypedMethodCall', null)
        assertFieldType('untypedClosure', null)
    }

    //------------------------------------------------------------------------------------
    // Declared type takes precedence over the initial expression
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_DeclaredTypeTakesPrecedence() {
        assertFieldType('stringWithNull', String)
        assertFieldType('longWithInteger', Long)
    }

    @Test
    void test_getFieldType_DeclaredObjectWithStringInitialValue() {
        assertFieldType('objectWithString', String)
    }

    //------------------------------------------------------------------------------------
    // Constant values whose expression type is not informative (Step 3: inspect the literal value)
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_ConstantWithObjectType_String() {
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant('abc')) == String
    }

    @Test
    void test_getFieldType_ConstantWithObjectType_Boolean() {
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(true)) == Boolean
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(false)) == Boolean
    }

    @Test
    void test_getFieldType_ConstantWithObjectType_Numbers() {
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(123)) == Integer
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(123L)) == Long
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(1.5d)) == Double
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(1.5f)) == Float
    }

    @Test
    void test_getFieldType_ConstantWithObjectType_Null() {
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(null)) == null
    }

    @Test
    void test_getFieldType_ConstantWithObjectType_UnsupportedValues() {
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(new BigDecimal('1.5'))) == null
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(new BigInteger('123'))) == null
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant((short) 7)) == null
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant((byte) 7)) == null
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant('x' as char)) == null
    }

    @Test
    void test_getFieldType_NonConstantInitialExpressionWithObjectType() {
        def initialExpression = new VariableExpression('someVariable')
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.OBJECT_TYPE, initialExpression)) == null
    }

    @Test
    void test_getFieldType_ConstructedFieldNodes_DeclaredTypes() {
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.boolean_TYPE)) == Boolean
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.int_TYPE)) == Integer
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.Integer_TYPE)) == Integer
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.long_TYPE)) == Long
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.STRING_TYPE)) == String
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.make(ReentrantLock))) == ReentrantLock
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.OBJECT_TYPE)) == null
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.DYNAMIC_TYPE)) == null
    }

    //------------------------------------------------------------------------------------
    // Helper methods
    //------------------------------------------------------------------------------------

    private static void assertFieldType(String fieldName, Class expectedType) {
        FieldNode field = FIELDS[fieldName]
        assert field, "No field named [$fieldName]"
        assert InferenceUtil.getFieldType(field) == expectedType
    }

    private static FieldNode fieldWithObjectTypedConstant(Object value) {
        def constant = new ConstantExpression(value)
        constant.type = ClassHelper.OBJECT_TYPE
        return fieldNode(ClassHelper.DYNAMIC_TYPE, constant)
    }

    private static FieldNode fieldNode(ClassNode type, Expression initialExpression = null) {
        return new FieldNode('myField', Modifier.PRIVATE, type, ClassHelper.OBJECT_TYPE, initialExpression)
    }

    private static Map<String, FieldNode> parseFields(String source) {
        def ast = new SourceString(source).ast
        def classNode = ast.classes.find { it.nameWithoutPackage == 'MyClass' }
        return classNode.fields.collectEntries { field -> [(field.name): field] }
    }
}
