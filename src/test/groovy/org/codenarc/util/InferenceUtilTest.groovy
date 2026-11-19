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
            long[] longArray
            boolean[] booleanArray
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

    private static final NESTED_CLASSES_SOURCE = '''
        import java.util.concurrent.locks.ReentrantLock

        class Outer {
            String outerString
            int outerInt
            ReentrantLock outerLock
            Object[] outerArray
            def outerUntypedLong = 123L
            List outerList
            String shadowed
            String innerShadowsWithUnknownType

            class Inner {
                boolean innerBoolean
                Long shadowed
                List innerShadowsWithUnknownType

                class Innermost {
                    double innermostDouble
                }
            }

            static class StaticNested {
            }

            def anonymous = new Runnable() {
                String anonymousString
                void run() { }
            }
        }

        class Subclass extends Outer {
        }
    '''

    private static final Map<String, ClassNode> CLASSES = parseClasses(NESTED_CLASSES_SOURCE)

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
    void test_getFieldType_DeclaredPrimitiveArray() {
        assertFieldType('intArray', Object[])
        assertFieldType('longArray', Object[])
        assertFieldType('booleanArray', Object[])
        assert InferenceUtil.getFieldType(fieldNode(ClassHelper.int_TYPE.makeArray())) == Object[]
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
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(1.5G)) == null
        assert InferenceUtil.getFieldType(fieldWithObjectTypedConstant(123G)) == null
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
    // getFieldType(ClassNode, String): find the named field in the class or its outer classes
    //------------------------------------------------------------------------------------

    @Test
    void test_getFieldType_ClassNode_FieldDeclaredInClass() {
        def outer = classNamed('Outer')
        assert InferenceUtil.getFieldType(outer, 'outerString') == String
        assert InferenceUtil.getFieldType(outer, 'outerInt') == Integer
        assert InferenceUtil.getFieldType(outer, 'outerLock') == ReentrantLock
        assert InferenceUtil.getFieldType(outer, 'outerArray') == Object[]
        assert InferenceUtil.getFieldType(outer, 'outerUntypedLong') == Long
    }

    @Test
    void test_getFieldType_ClassNode_FieldTypeCannotBeInferred() {
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'outerList') == null
    }

    @Test
    void test_getFieldType_ClassNode_NoSuchField() {
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'noSuchField') == null
        assert InferenceUtil.getFieldType(classNamed('Outer$Inner'), 'noSuchField') == null
    }

    @Test
    void test_getFieldType_ClassNode_FieldNameIsCaseSensitive() {
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'OUTERSTRING') == null
    }

    @Test
    void test_getFieldType_ClassNode_NullClassNode() {
        assert InferenceUtil.getFieldType((ClassNode) null, 'outerString') == null
    }

    @Test
    void test_getFieldType_ClassNode_FieldDeclaredInOuterClass() {
        assert InferenceUtil.getFieldType(classNamed('Outer$Inner'), 'outerString') == String
        assert InferenceUtil.getFieldType(classNamed('Outer$StaticNested'), 'outerLock') == ReentrantLock
    }

    @Test
    void test_getFieldType_ClassNode_FieldDeclaredTwoLevelsOut() {
        def innermost = classNamed('Outer$Inner$Innermost')
        assert InferenceUtil.getFieldType(innermost, 'outerInt') == Integer
        assert InferenceUtil.getFieldType(innermost, 'innerBoolean') == Boolean
        assert InferenceUtil.getFieldType(innermost, 'innermostDouble') == Double
    }

    @Test
    void test_getFieldType_ClassNode_InnerFieldShadowsOuterField() {
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'shadowed') == String
        assert InferenceUtil.getFieldType(classNamed('Outer$Inner'), 'shadowed') == Long
        assert InferenceUtil.getFieldType(classNamed('Outer$Inner$Innermost'), 'shadowed') == Long
    }

    @Test
    void test_getFieldType_ClassNode_FirstMatchingFieldDeterminesResult() {
        // The inner field is found first, so the outer field is not used even though its type could be inferred
        assert InferenceUtil.getFieldType(classNamed('Outer$Inner'), 'innerShadowsWithUnknownType') == null
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'innerShadowsWithUnknownType') == String
    }

    @Test
    void test_getFieldType_ClassNode_FieldOfInnerClassNotVisibleFromOuterClass() {
        assert InferenceUtil.getFieldType(classNamed('Outer'), 'innerBoolean') == null
    }

    @Test
    void test_getFieldType_ClassNode_AnonymousInnerClass() {
        def anonymousClass = CLASSES.values().find { it.name.startsWith('Outer$') && it.name ==~ /.*\$\d+/ }
        assert anonymousClass
        assert InferenceUtil.getFieldType(anonymousClass, 'anonymousString') == String
        assert InferenceUtil.getFieldType(anonymousClass, 'outerInt') == Integer
    }

    @Test
    void test_getFieldType_ClassNode_DoesNotSearchSuperclass() {
        assert InferenceUtil.getFieldType(classNamed('Subclass'), 'outerString') == null
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

    private static ClassNode classNamed(String name) {
        ClassNode classNode = CLASSES[name]
        assert classNode, "No class named [$name]"
        return classNode
    }

    private static Map<String, ClassNode> parseClasses(String source) {
        def ast = new SourceString(source).ast
        return ast.classes.collectEntries { classNode -> [(classNode.name): classNode] }
    }

    private static Map<String, FieldNode> parseFields(String source) {
        def ast = new SourceString(source).ast
        def classNode = ast.classes.find { it.nameWithoutPackage == 'MyClass' }
        return classNode.fields.collectEntries { field -> [(field.name): field] }
    }
}
