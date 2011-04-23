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

import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.source.SourceCode
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import java.util.concurrent.locks.ReentrantLock

/**
 * Contains static utility methods and constants related to Groovy AST.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
@SuppressWarnings(['DuplicateLiteral', 'MethodCount'])
class AstUtil {

    public static final AUTO_IMPORTED_PACKAGES = ['java.lang', 'java.io', 'java.net', 'java.util', 'groovy.lang', 'groovy.util']
    public static final AUTO_IMPORTED_CLASSES = ['java.math.BigDecimal', 'java.math.BigInteger']

    public static final COMPARISON_OPERATORS = ['==', '!=', '<', '<=', '>', '>=', '<=>']
    private static final PREDEFINED_CONSTANTS = ['Boolean': ['FALSE', 'TRUE']]

    /**
     * Tells you if an expression is a constant or literal. Basically, is it a map, list, constant, or a predefined
     * constant like true/false.
     * @param expression
     *     any expression
     * @return
     * as described
     */
    static boolean isConstantOrLiteral(Expression expression) {
        expression.class in [ConstantExpression, ListExpression, MapExpression] || isPredefinedConstant(expression)
    }

    /**
     * Returns true if an expression is a constant or else a literal that contains only constant values.
     * Basically, is it a constant, or else a map like [a:1, b:99, c:true], or a list like ['abc', 99.0, false]
     * @param expression - any expression
     */
    static boolean isConstantOrConstantLiteral(Expression expression) {
        expression instanceof ConstantExpression ||
            isPredefinedConstant(expression) ||
            isMapLiteralWithOnlyConstantValues(expression) ||
            isListLiteralWithOnlyConstantValues(expression)
    }

    /**
     * Returns true if a Map literal that contains only entries where both key and value are constants.
     * @param expression - any expression
     */
    static boolean isMapLiteralWithOnlyConstantValues(Expression expression) {
        if (expression instanceof MapExpression) {
            return expression.mapEntryExpressions.every { mapEntryExpression ->
                isConstantOrConstantLiteral(mapEntryExpression.keyExpression) &&
                isConstantOrConstantLiteral(mapEntryExpression.valueExpression)
            }
        }
    }

    /**
     * Returns true if a List literal that contains only entries that are constants.
     * @param expression - any expression
     */
    static boolean isListLiteralWithOnlyConstantValues(Expression expression) {
        if (expression instanceof ListExpression) {
            return expression.expressions.every { listExpression -> isConstantOrConstantLiteral(listExpression) }
        }
    }

    /**
     * Tells you if an expression is the expected constant.
     * @param expression
     *     any expression
     * @param expected
     *     the expected int or String
     * @return
     * as described
     */
    static boolean isConstant(Expression expression, expected) {
        (expression instanceof ConstantExpression && expression.value == expected)
    }

    static boolean isPropertyNamed(Expression property, expectedName) {
        return (property instanceof PropertyExpression && AstUtil.isConstant(property.property, expectedName))
    }

    /**
     * Tells you if the expression is a predefined constant like TRUE or FALSE.
     * @param expression
     *      any expression
     * @return
     * as described
     */
    private static boolean isPredefinedConstant(Expression expression) {
        if (expression instanceof PropertyExpression) {
            def object = expression.objectExpression
            def property = expression.property

            if (object instanceof VariableExpression) {
                def predefinedConstantNames = PREDEFINED_CONSTANTS[object.name]
                return property.text in predefinedConstantNames
            }
        }
        false
    }

    /**
     * Return true if the Statement is a block
     * @param statement - the Statement to check
     * @return true if the Statement is a block
     */
    static boolean isBlock(Statement statement) {
        statement instanceof BlockStatement
    }

    /**
     * Return true if the Statement is a block and it is empty (contains no "meaningful" statements).
     * This implementation also addresses some "weirdness" around some statement types (specifically finally)
     * where the BlockStatement answered false to isEmpty() even if it was.
     * @param statement - the Statement to check
     * @return true if the BlockStatement is empty
     */
    static boolean isEmptyBlock(Statement origStatement) {
        def stack = [origStatement] as Stack
        while (stack) {
            def statement = stack.pop()
            if (!(statement instanceof BlockStatement)) {
                return false
            }
            if (statement.empty) {
                return true
            }
            if (statement.statements.size() != 1) {
                return false
            }
            stack.push(statement.statements[0])
        }
        false
    }

    static ASTNode getEmptyBlock(Statement origStatement) {

        def stack = [origStatement] as Stack

        while (stack) {
            def statement = stack.pop()
            if (!(statement instanceof BlockStatement)) {
                return null
            }
            if (statement.empty) {
                return statement
            }
            if (statement.statements.size() != 1) {
                return null
            }
            stack.push(statement.statements[0])
        }
        return null
    }

    /**
     * Return the List of Arguments for the specified MethodCallExpression or a ConstructorCallExpression.
     * The returned List contains either ConstantExpression or MapEntryExpression objects.
     * @param methodCall - the AST MethodCallExpression or ConstructorCalLExpression
     * @return the List of argument objects
     */
    static List getMethodArguments(ASTNode methodCall) {
        if (respondsTo(methodCall, 'getArguments')) {
            def argumentsExpression = methodCall.arguments
            if (respondsTo(argumentsExpression, 'getExpressions')) {
                return argumentsExpression.expressions
            }
            if (respondsTo(argumentsExpression, 'getMapEntryExpressions')) {
                return argumentsExpression.mapEntryExpressions
            }
        }
        []
    }

    /**
     * Tells you if the expression is a method call on a particual object (which is represented as a String).
     * For instance, you may ask isMethodCallOnObject(e, 'this') to find a this reference.  
     * @param expression
     *      the expression
     * @param methodObjectPattern
     * @param methodObjectPattern - the name of the method object (receiver) such as 'this'
     * @return
     * as described
     */
    static boolean isMethodCallOnObject(Expression expression, String methodObjectPattern) {
        expression instanceof MethodCallExpression &&
            ((expression.objectExpression instanceof VariableExpression && expression.objectExpression.name?.matches(methodObjectPattern)) ||
            (expression.objectExpression instanceof PropertyExpression && expression.objectExpression.text?.matches(methodObjectPattern)))

    }

    /**
     * Return true only if the Statement represents a method call for the specified method object (receiver),
     * method name, and with the specified number of arguments.
     * @param stmt - the AST Statement
     * @param methodObject - the name of the method object (receiver)
     * @param methodName - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the Statement is a method call matching the specified criteria
     */
    static boolean isMethodCall(Statement stmt, String methodObject, String methodName, int numArguments) {
        if (stmt instanceof ExpressionStatement) {
            def expression = stmt.expression
            if (expression instanceof MethodCallExpression) {
                return isMethodCall(expression, methodObject, methodName, numArguments)
            }
        }
        false
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method object (receiver),
     * method name, and with the specified number of arguments.
     * @param methodCall - the AST MethodCallExpression
     * @param methodObject - the name of the method object (receiver)
     * @param methodPattern - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the method call matches the specified criteria
     */
    static boolean isMethodCall(MethodCallExpression methodCall, String methodObject, String methodPattern, int numArguments) {
        (isMethodCall(methodCall, methodObject, methodPattern)
                && getMethodArguments(methodCall).size() == numArguments)
    }

    /**
     * Return true only if the expression is a MethodCallExpression representing a method call for the specified
     * method object (receiver), method name, and with the specified number of arguments.
     * @param expression - the AST expression
     * @param methodObject - the name of the method object (receiver)
     * @param methodName - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the method call matches the specified criteria
     */
    static boolean isMethodCall(Expression expression, String methodObject, String methodName, int numArguments) {
        expression instanceof MethodCallExpression && isMethodCall((MethodCallExpression) expression, methodObject, methodName, numArguments)
    }

    /**
     * Return true only if the expression represents a method call (MethodCallExpression) for the specified
     * method object (receiver) and method name.
     *
     * @param expression - the AST expression to be checked
     * @param methodObjectPattern - the name of the method object (receiver)
     * @param methodNamePattern - the name of the method being called
     * @return true only if the expression is a method call that matches the specified criteria
     */
    static boolean isMethodCall(expression, String methodObjectPattern, String methodNamePattern) {
        isMethodCallOnObject(expression, methodObjectPattern) && isMethodNamed(expression, methodNamePattern)
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for any one of the specified method
     * objects (receivers) and any one of the method names. Optionally, you can restrict it to a method call with
     * a certain number of arguments.
     * @param methodCall
     *      the method call object
     * @param methodObjects
     *      a list of receivers, such as ['this', 'super']
     * @param methodNames
     *      a list of method names
     * @param numArguments
     *      optionally, require a certain number of arguments
     * @return
     * as described
     */
    static boolean isMethodCall(MethodCallExpression methodCall, List<String> methodObjects, List<String> methodNames, numArguments = null) {
        for (String name: methodNames) {
            for (String objectName: methodObjects) {
                def match = isMethodCallOnObject(methodCall, objectName) && isMethodNamed(methodCall, name)
                if (match && numArguments == null) {
                    return true
                } else if (match && getMethodArguments(methodCall).size() == numArguments) {
                    return true
                }
            }
        }
        false
    }

    /**
     * Tells you if the expression is a method call for a certain method name with a certain
     * number of arguments.
     * @param expression
     *      the (potentially) method call
     * @param methodName
     *      the name of the method expected
     * @param numArguments
     *      number of expected arguments
     * @return
     * as described
     */
    static boolean isMethodCall(Expression expression, String methodName, int numArguments) {
        if (expression instanceof MethodCallExpression && isMethodNamed(expression, methodName)) {
            return getMethodArguments(expression).size() == numArguments
        }
        false
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method name
     * @param methodCall - the AST MethodCallExpression
     * @param methodNamePattern - the expected name of the method being called
     * @return true only if the method call name matches
     */
    static boolean isMethodNamed(MethodCallExpression methodCall, String methodNamePattern, Integer numArguments = null) {
        def method = methodCall.method
        def isNameMatch = method.properties['value']?.matches(methodNamePattern)

        if (isNameMatch && numArguments != null) {
            return getMethodArguments(methodCall).size() == numArguments
        }
        isNameMatch
    }

    /**
     * Return true if the expression is a constructor call on any of the named classes, with any number of parameters.
     * @param expression - the expression
     * @param className - the possible List of class names
     * @return as described
     */
    static boolean isConstructorCall(Expression expression, List<String> classNames) {
        expression instanceof ConstructorCallExpression && expression.type.name in classNames
    }

    /**
     * Return the AnnotationNode for the named annotation, or else null.
     * Supports Groovy 1.5 and Groovy 1.6.
     * @param node - the AnnotatedNode
     * @param name - the name of the annotation
     * @return the AnnotationNode or else null 
     */
    static AnnotationNode getAnnotation(AnnotatedNode node, String name) {
        def annotations = node.annotations
        annotations instanceof Map ?
            annotations[name] :                                         // Groovy 1.5
            annotations.find { annot -> annot.classNode.name == name }  // Groovy 1.6
    }

    /**
     * Return the List of VariableExpression objects referenced by the specified DeclarationExpression.
     * @param declarationExpression - the DeclarationExpression
     * @return the List of VariableExpression objects
     */
    static List getVariableExpressions(DeclarationExpression declarationExpression) {
        def leftExpression = declarationExpression.leftExpression
        leftExpression.properties['expressions'] ?: [leftExpression]
    }

    /**
     * Return true if the DeclarationExpression represents a 'final' variable declaration.
     *
     * NOTE: THIS IS A WORKAROUND.
     *
     * There does not seem to be an easy way to determine whether the 'final' modifier has been
     * specified for a variable declaration. Return true if the 'final' is present before the variable name.
     */
    static boolean isFinalVariable(DeclarationExpression declarationExpression, SourceCode sourceCode) {
        if (isFromGeneratedSourceCode(declarationExpression)) {
            return false
        }
        def variableExpressions = AstUtil.getVariableExpressions(declarationExpression)
        def variableExpression = variableExpressions[0]
        def startOfDeclaration = declarationExpression.columnNumber
        def startOfVariableName = variableExpression.columnNumber
        def sourceLine = sourceCode.lines[declarationExpression.lineNumber - 1]

        def modifiers = (startOfDeclaration >= 0 && startOfVariableName >= 0) ?
            sourceLine[startOfDeclaration - 1..startOfVariableName - 2] : ''
        modifiers.contains('final')
    }

    /**
     * @return true if the ASTNode was generated (synthetic) rather than from the "real" input source code.
     */
    static boolean isFromGeneratedSourceCode(ASTNode node) {
        node.lineNumber < 0
    }

    /**
     * Tells you if the expression is true, which can be true or Boolean.TRUE.
     * @param expression
     *      expression
     * @return
     * as described
     */
    static boolean isTrue(Expression expression) {
        if (expression instanceof PropertyExpression && classNodeImplementsType(expression.objectExpression.type, Boolean)) {
            if (expression.property instanceof ConstantExpression && expression.property.value == 'TRUE') {
                return true
            }
        }
        ((expression instanceof ConstantExpression) && expression.isTrueExpression()) ||
                expression.text == 'Boolean.TRUE'
    }

    /**
     * Tells you if the expression is either the true or false literal.
     * @param expression
     *      expression
     * @return
     * as described
     */
    static boolean isBoolean(Expression expression) {
        isTrue(expression) || isFalse(expression)
    }

    /**
     * Tells you if the expression is the null literal.
     * @param expression
     *      expression.
     * @return
     * as described
     */
    static boolean isNull(ASTNode expression) {
        expression instanceof ConstantExpression && expression.isNullExpression()
    }

    /**
     * Tells you if the expression is the false expression, either literal or contant.
     * @param expression
     *      expression
     * @return
     * as described
     */
    static boolean isFalse(Expression expression) {
        if (expression instanceof PropertyExpression && classNodeImplementsType(expression.objectExpression.type, Boolean)) {
            if (expression.property instanceof ConstantExpression && expression.property.value == 'FALSE') {
                return true
            }
        }
        ((expression instanceof ConstantExpression) && expression.isFalseExpression()) ||
                expression.text == 'Boolean.FALSE'
    }

    /**
     * Return true only if the specified object responds to the named method
     * @param object - the object to check
     * @param methodName - the name of the method
     * @return true if the object responds to the named method
     */
    static boolean respondsTo(Object object, String methodName) {
        object.metaClass.respondsTo(object, methodName)
    }

    /**
     * This method tells you if a ClassNode implements or extends a certain class.
     * @param node
     *      the node
     * @param target
     *      the class
     * @return
     * true if the class node 'is a' target
     */
    static boolean classNodeImplementsType(ClassNode node, Class target) {
        ClassNode targetNode = ClassHelper.make(target)
        if (node.implementsInterface(targetNode)) {
            return true
        }
        if (node.isDerivedFrom(targetNode)) {
            return true
        }
        if (node.name == target.name) {
            return true
        }
        if (node.name == target.simpleName) {
            return true
        }
        if (node.superClass?.name == target.name) {
            return true
        }
        if (node.superClass?.name == target.simpleName) {
            return true
        }
        node.interfaces.any { ClassNode declaredInterface ->
            classNodeImplementsType(declaredInterface, target)
        }
    }

    /**
     * Returns true if the ASTNode is a declaration of a closure, either as a declaration
     * or a field.
     * @param expression
     *      the target expression
     * @return
     * as described
     */
    static boolean isClosureDeclaration(ASTNode expression) {
        if (expression instanceof DeclarationExpression) {
            if (expression.rightExpression instanceof ClosureExpression) {
                return true
            }
        }
        if (expression instanceof FieldNode) {
            ClassNode type = expression.type
            if (classNodeImplementsType(type, Closure)) {
                return true
            } else if (expression.initialValueExpression instanceof ClosureExpression) {
                return true
            }
        }

        false
    }

    /**
     * Gets the parameter names of a method node.
     * @param node
     *      the node to search parameter names on
     * @return
     * argument names, never null
     */
    static List<String> getParameterNames(MethodNode node) {
        node.parameters*.name 
    }

    /**
     * Gets the argument names of a method call. If the arguments are not VariableExpressions then a null
     * will be returned.
     * @param methodCall
     *      the method call to search
     * @return
     * a list of strings, never null, but some elements may be null
     */
    static List<String> getArgumentNames(MethodCallExpression methodCall) {
        methodCall.arguments?.expressions?.collect {
            if (it instanceof VariableExpression) {
                return it.name
            } else {
                return null
            }
        }
    }

    /**
     * Returns true if the expression is a binary expression with the specified token.
     * @param expression
     *      expression
     * @param token
     *      token
     * @return
     * as described
     */
    static boolean isBinaryExpressionType(Expression expression, String token) {
        if (expression instanceof BinaryExpression) {
            if (expression.operation.text == token) {
                return true
            }
        }
        false
    }

    /**
     * Returns true if the expression is a binary expression with the specified token.
     * @param expression - the expression node
     * @param tokens - the List of allowable (operator) tokens
     * @return as described
     */
    static boolean isBinaryExpressionType(Expression expression, List<String> tokens) {
        if (expression instanceof BinaryExpression) {
            if (expression.operation.text in tokens) {
                return true
            }
        }
        false
    }

    /**
     * Tells you if the expression is a null safe dereference.
     * @param expression
     *      expression
     * @return
     * true if is null safe dereference.
     */
    static boolean isSafe(Expression expression) {
        if (expression instanceof MethodCallExpression || expression instanceof PropertyExpression) {
            return expression.safe
        }
        false
    }

    /**
     * Tells you if the expression is a spread operator call
     * @param expression
     *      expression
     * @return
     * true if is spread expression
     */
    static boolean isSpreadSafe(Expression expression) {
        if (expression instanceof MethodCallExpression || expression instanceof PropertyExpression) {
            return expression.spreadSafe
        }
        false
    }

    /**
     * Tells you if the ASTNode is a method node for the given name, arity, and return type.
     * @param node
     *      the node to inspect
     * @param methodName
     *      the expected name of the method
     * @param numArguments
     *      the expected number of arguments, optional
     * @param returnType
     *      the expected return type, optional
     * @return
     * true if this node is a MethodNode meeting the parameters. false otherwise
     */
    static boolean isMethodNode(ASTNode node, String methodName, Integer numArguments = null, Class returnType = null) {
        if (!node instanceof MethodNode) {
            return false
        }
        if (node.name != methodName) {
            return false
        }
        if (numArguments != null && node.parameters?.length != numArguments) {
            return false
        }
        if (returnType && !AstUtil.classNodeImplementsType(node.returnType, returnType)) {
            return false
        }
        true
    }

    /**
     * Tells you if the given ASTNode is a VariableExpression with the given name.
     * @param expression
     *      any AST Node
     * @param name
     *      a string name
     * @return
     * true if the node is a variable with the specified name
     */
    static boolean isVariable(ASTNode expression, String name) {
        return (expression instanceof VariableExpression && expression.name == name)
    }

    /**
     * Tells you if the ASTNode has a public modifier on it. If the node does not have modifiers at all (like
     * a variable expression) then false is returned.
     * @param node
     *      node to query
     * @return
     * true if definitely public, false if not public or unknown
     */
    static boolean isPublic(ASTNode node) {
        def modifiers = node.properties['modifiers']
        if (modifiers && modifiers instanceof Integer) {
            return Modifier.isPublic(modifiers)
        }
        false
    }

    /**
     * Private constructor. All methods are static.
     */
    private AstUtil() { }

    static boolean isNotNullCheck(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == '!=') {
            if (AstUtil.isNull(expression.leftExpression) || AstUtil.isNull(expression.rightExpression)) {
                return true
            }
        }
        false
    }

    static String getNullComparisonTarget(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == '!=') {
            if (AstUtil.isNull(expression.leftExpression)) {
                return expression.rightExpression.text
            } else if (AstUtil.isNull(expression.rightExpression)) {
                return expression.leftExpression.text
            }
        }
        null
    }

    static boolean isInstanceOfCheck(expression) {
        (expression instanceof BinaryExpression && expression.operation.text == 'instanceof')
    }

    static String getInstanceOfTarget(expression) {
        if (expression instanceof BinaryExpression && expression.operation.text == 'instanceof') {
            return expression.leftExpression.text
        }
        null
    }

    /**
     * Supports discovering many common JDK types, but not all.
     * @param node
     * @param fieldName
     * @return
     */
    static Class getFieldType(ClassNode node, String fieldName) {
        while (node != null) {
            for (FieldNode field: node.fields) {
                if (field.name == fieldName) {
                    return getFieldType(field)
                }
            }
            node = node.outerClass
        }
        null
    }

    /**
     * Supports discovering many common JDK types, but not all.
     */
    static Class getFieldType(FieldNode field) {
        // Step 1: Analyze the field's declared type
        def declaredType = getClassForClassNode(field.type)
        if (declaredType) {
            return declaredType
        }

        // Step 2: Analyze the cast type of the initial expression
        if (field.initialExpression instanceof Expression) {
            def castType = getClassForClassNode(field.initialExpression.type)
            if (castType) {
                return castType
            }
        }

        // Step 3: Look at the literal within the constant
        if (field.initialExpression instanceof ConstantExpression) {
            if (field.initialExpression.value instanceof String) {
                return String
            } else if (isBoolean(field.initialExpression)) {
                return Boolean
            } else if (getClass() in [Integer, Integer.TYPE]) {
                return Integer
            } else if (getClass() in [Long, Long.TYPE]) {
                return Long
            } else if (getClass() in [Double, Double.TYPE]) {
                return Double
            } else if (getClass() in [Float, Float.TYPE]) {
                return Float
            }
        }
        return null
    }

    /**
     * This is private. It is a helper function for the utils.
     */
    private static Class getClassForClassNode(ClassNode type) {
        // todo hamlet - move to a different "InferenceUtil" object
        def primitiveType = getPrimitiveType(type)
        if (primitiveType) {
            return primitiveType
        } else if (AstUtil.classNodeImplementsType(type, String)) {
            return String
        } else if (AstUtil.classNodeImplementsType(type, ReentrantLock)) {
            return ReentrantLock
        } else if (type.name?.endsWith('[]')) {
            return Object[].class       // better type inference could be done, but oh well
        }
        null
    }

    private static Class getPrimitiveType(ClassNode type) {
        if (AstUtil.classNodeImplementsType(type, Boolean) || AstUtil.classNodeImplementsType(type, Boolean.TYPE)) {
            return Boolean
        } else if (AstUtil.classNodeImplementsType(type, Long) || AstUtil.classNodeImplementsType(type, Long.TYPE)) {
            return Long
        } else if (AstUtil.classNodeImplementsType(type, Short) || AstUtil.classNodeImplementsType(type, Short.TYPE)) {
            return Short
        } else if (AstUtil.classNodeImplementsType(type, Double) || AstUtil.classNodeImplementsType(type, Double.TYPE)) {
            return Double
        } else if (AstUtil.classNodeImplementsType(type, Float) || AstUtil.classNodeImplementsType(type, Float.TYPE)) {
            return Float
        } else if (AstUtil.classNodeImplementsType(type, Character) || AstUtil.classNodeImplementsType(type, Character.TYPE)) {
            return Character
        } else if (AstUtil.classNodeImplementsType(type, Integer) || AstUtil.classNodeImplementsType(type, Integer.TYPE)) {
            return Integer
        } else if (AstUtil.classNodeImplementsType(type, Long) || AstUtil.classNodeImplementsType(type, Long.TYPE)) {
            return Long
        } else if (AstUtil.classNodeImplementsType(type, Byte) || AstUtil.classNodeImplementsType(type, Byte.TYPE)) {
            return Byte
        }
        null
    }
}