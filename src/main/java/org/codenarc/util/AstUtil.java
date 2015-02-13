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
package org.codenarc.util;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.Range;
import org.apache.log4j.Logger;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codenarc.source.SourceCode;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

/**
 * Contains static utility methods and constants related to Groovy AST.
 * <p/>
 * This is an internal class and its API is subject to change.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
@SuppressWarnings("PMD.CollapsibleIfStatements")
public class AstUtil {

    private static final Logger LOG = Logger.getLogger(AstUtil.class);
    public static final List<String> AUTO_IMPORTED_PACKAGES = asList("java.lang", "java.io", "java.net", "java.util", "groovy.lang", "groovy.util");
    public static final List<String> AUTO_IMPORTED_CLASSES = asList("java.math.BigDecimal", "java.math.BigInteger");
    public static final List<String> COMPARISON_OPERATORS = asList("==", "!=", "<", "<=", ">", ">=", "<=>");
    private static final Map<String, List<String>> PREDEFINED_CONSTANTS = new HashMap<String, List<String>>();
    static {
        PREDEFINED_CONSTANTS.put("Boolean", asList("FALSE", "TRUE"));
    }

    /**
     * Private constructor. All methods are static.
     */
    private AstUtil() { }

    /**
     * Tells you if an expression is a constant or literal. Basically, is it a map, list, constant, or a predefined
     * constant like true/false.
     * @param expression
     *     any expression
     * @return
     * as described
     */
    public static boolean isConstantOrLiteral(Expression expression) {
        if (expression instanceof ConstantExpression) return true;
        if (expression instanceof ListExpression) return true;
        if (expression instanceof MapExpression) return true;
        return isPredefinedConstant(expression);
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
            Expression object = ((PropertyExpression) expression).getObjectExpression();
            Expression property = ((PropertyExpression) expression).getProperty();

            if (object instanceof VariableExpression) {
                List<String> predefinedConstantNames = PREDEFINED_CONSTANTS.get(((VariableExpression) object).getName());
                if (predefinedConstantNames != null && predefinedConstantNames.contains(property.getText())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if an expression is a constant or else a literal that contains only constant values.
     * Basically, is it a constant, or else a map like [a:1, b:99, c:true], or a list like ['abc', 99.0, false]
     * @param expression - any expression
     */
    public static boolean isConstantOrConstantLiteral(Expression expression) {
        return expression instanceof ConstantExpression ||
            isPredefinedConstant(expression) ||
            isMapLiteralWithOnlyConstantValues(expression) ||
            isListLiteralWithOnlyConstantValues(expression);
    }

    /**
     * Returns true if a Map literal that contains only entries where both key and value are constants.
     * @param expression - any expression
     */
    public static boolean isMapLiteralWithOnlyConstantValues(Expression expression) {
        if (expression instanceof MapExpression) {
            List<MapEntryExpression> entries = ((MapExpression) expression).getMapEntryExpressions();
            for (MapEntryExpression entry : entries) {
                if (!isConstantOrConstantLiteral(entry.getKeyExpression()) ||
                    !isConstantOrConstantLiteral(entry.getValueExpression())) {
                    return false; 
                }
            }
            return true;
        }
        return false; 
    }

    /**
     * Returns true if a List literal that contains only entries that are constants.
     * @param expression - any expression
     */
    public static boolean isListLiteralWithOnlyConstantValues(Expression expression) {
        if (expression instanceof ListExpression) {
            List<Expression> expressions = ((ListExpression) expression).getExpressions();
            for (Expression e : expressions) {
                if (!isConstantOrConstantLiteral(e)) {
                    return false;
                }
            }
            return true; 
        }
        return false;
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
    public static boolean isConstant(Expression expression, Object expected) {
        return expression instanceof ConstantExpression && expected.equals(((ConstantExpression) expression).getValue());
    }

    public static boolean isPropertyNamed(Expression property, Object expectedName) {
        return (property instanceof PropertyExpression && isConstant(((PropertyExpression) property).getProperty(), expectedName));
    }

    /**
     * Return true if the Statement is a block
     * @param statement - the Statement to check
     * @return true if the Statement is a block
     */
    public static boolean isBlock(Statement statement) {
        return statement instanceof BlockStatement;
    }

    /**
     * Return true if the Statement is a block and it is empty (contains no "meaningful" statements).
     * This implementation also addresses some "weirdness" around some statement types (specifically finally)
     * where the BlockStatement answered false to isEmpty() even if it was.
     * @param origStatement - the Statement to check
     * @return true if the BlockStatement is empty
     */
    public static boolean isEmptyBlock(Statement origStatement) {
        Stack<ASTNode> stack = new Stack<ASTNode>();
        stack.push(origStatement);

        while (!stack.isEmpty()) {
            ASTNode statement = stack.pop();
            if (!(statement instanceof BlockStatement)) {
                return false;
            }
            if (((BlockStatement) statement).isEmpty()) {
                return true;
            }
            if (((BlockStatement) statement).getStatements().size() != 1) {
                return false;
            }
            stack.push(((BlockStatement) statement).getStatements().get(0));
        }
        return false;
    }

    public static ASTNode getEmptyBlock(Statement origStatement) {
        Stack<ASTNode> stack = new Stack<ASTNode>();
        stack.push(origStatement);

        while (!stack.isEmpty()) {
            ASTNode statement = stack.pop();
            if (!(statement instanceof BlockStatement)) {
                return null;
            }
            if (((BlockStatement) statement).isEmpty()) {
                return statement;
            }
            if (((BlockStatement) statement).getStatements().size() != 1) {
                return null;
            }
            stack.push(((BlockStatement) statement).getStatements().get(0));
        }
        return null;
    }

    /**
     * Return the List of Arguments for the specified MethodCallExpression or a ConstructorCallExpression.
     * The returned List contains either ConstantExpression or MapEntryExpression objects.
     * @param methodCall - the AST MethodCallExpression or ConstructorCalLExpression
     * @return the List of argument objects
     */
    public static List<? extends Expression> getMethodArguments(ASTNode methodCall) {
        if (methodCall instanceof ConstructorCallExpression) {
            return extractExpressions(((ConstructorCallExpression) methodCall).getArguments());
        } else if (methodCall instanceof MethodCallExpression) {
            return extractExpressions(((MethodCallExpression) methodCall).getArguments());
        } else if (methodCall instanceof StaticMethodCallExpression) {
            return extractExpressions(((StaticMethodCallExpression) methodCall).getArguments());
        } else if (respondsTo(methodCall, "getArguments")) {
            throw new RuntimeException(); // TODO: remove, should never happen
        }
        return new ArrayList<Expression>();
    }

    private static List<? extends Expression> extractExpressions(Expression argumentsExpression ) {
        if (argumentsExpression instanceof ArrayExpression) {
            return ((ArrayExpression) argumentsExpression).getExpressions();
        } else if (argumentsExpression instanceof ListExpression) {
            return ((ListExpression) argumentsExpression).getExpressions();
        } else if (argumentsExpression instanceof TupleExpression) {
            return ((TupleExpression) argumentsExpression).getExpressions();
        } else if (argumentsExpression instanceof MapExpression) {
            return ((MapExpression) argumentsExpression).getMapEntryExpressions();
        } else if (respondsTo(argumentsExpression, "getExpressions")) {
            throw new RuntimeException(); // TODO: add warning
        } else if (respondsTo(argumentsExpression, "getMapEntryExpressions")) {
            throw new RuntimeException(); // TODO: add warning
        }
        return new ArrayList<Expression>();
    }

    /**
     * Tells you if the expression is a method call on a particular object (which is represented as a String).
     * For instance, you may ask isMethodCallOnObject(e, 'this') to find a this reference.  
     * @param expression - the expression
     * @param methodObjectPattern - the name of the method object (receiver) such as 'this'
     * @return
     * as described
     */
    public static boolean isMethodCallOnObject(Expression expression, String methodObjectPattern) {
        if (expression instanceof MethodCallExpression) {
            Expression objectExpression = ((MethodCallExpression) expression).getObjectExpression();
            if (objectExpression instanceof VariableExpression) {
                String name = ((VariableExpression) objectExpression).getName();
                if (name != null && name.matches(methodObjectPattern)) {
                    return true;
                }
            }
            if (objectExpression instanceof PropertyExpression && objectExpression.getText() != null && objectExpression.getText().matches(methodObjectPattern)) {
                return true;
            }
            if (objectExpression instanceof MethodCallExpression && isMethodNamed((MethodCallExpression) objectExpression, methodObjectPattern)) {
                return true;
            }
        }
        return false;
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
    public static boolean isMethodCall(Statement stmt, String methodObject, String methodName, int numArguments) {
        if (stmt instanceof ExpressionStatement) {
            Expression expression = ((ExpressionStatement) stmt).getExpression();
            if (expression instanceof MethodCallExpression) {
                return isMethodCall(expression, methodObject, methodName, numArguments);
            }
        }
        return false;
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method object (receiver),
     * method name, and with the specified number of arguments.
     * @param methodCall - the AST MethodCallExpression
     * @param methodObjectPattern - the name of the method object (receiver)
     * @param methodPattern - the name of the method being called
     * @param numArguments - the number of arguments passed into the method
     * @return true only if the method call matches the specified criteria
     */
    public static boolean isMethodCall(MethodCallExpression methodCall, String methodObjectPattern, String methodPattern, int numArguments) {
        return (isMethodCall(methodCall, methodObjectPattern, methodPattern) && AstUtil.getMethodArguments(methodCall).size() == numArguments); 
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
    public static boolean isMethodCall(Expression expression, String methodObject, String methodName, int numArguments) {
        return expression instanceof MethodCallExpression && isMethodCall((MethodCallExpression) expression, methodObject, methodName, numArguments);
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
    public static boolean isMethodCall(Expression expression, String methodObjectPattern, String methodNamePattern) {
        return isMethodCallOnObject(expression, methodObjectPattern) && isMethodNamed((MethodCallExpression) expression, methodNamePattern);
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
    public static boolean isMethodCall(MethodCallExpression methodCall, List<String> methodObjects, List<String> methodNames, Integer numArguments) {
        if (methodNames != null) {
            for (String name: methodNames) {
                if (methodObjects != null) {
                    for (String objectName: methodObjects) {
                        boolean match = isMethodCallOnObject(methodCall, objectName) && isMethodNamed(methodCall, name);
                        if (match && numArguments == null) {
                            return true;
                        } else if (match && getMethodArguments(methodCall).size() == numArguments) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isMethodCall(MethodCallExpression methodCall, List<String> methodObjects, List<String> methodNames) {
        return isMethodCall(methodCall, methodObjects, methodNames, null);
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
    public static boolean isMethodCall(Expression expression, String methodName, int numArguments) {
        return expression instanceof MethodCallExpression
                && isMethodNamed((MethodCallExpression) expression, methodName)
                && getMethodArguments(expression).size() == numArguments;
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
    public static boolean isMethodCall(Expression expression, String methodName, Range numArguments) {
        if (expression instanceof MethodCallExpression && AstUtil.isMethodNamed((MethodCallExpression) expression, methodName)) {
            int arity = AstUtil.getMethodArguments(expression).size();
            if (arity >= (Integer)numArguments.getFrom() && arity <= (Integer)numArguments.getTo()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true only if the MethodCallExpression represents a method call for the specified method name
     * @param methodCall - the AST MethodCallExpression
     * @param methodNamePattern - the expected name of the method being called
     * @param numArguments - The number of expected arguments
     * @return true only if the method call name matches
     */
    public static boolean isMethodNamed(MethodCallExpression methodCall, String methodNamePattern, Integer numArguments) {
        Expression method = methodCall.getMethod();

        // !important: performance enhancement
        boolean IS_NAME_MATCH = false;
        if (method instanceof ConstantExpression) {
            if (((ConstantExpression) method).getValue() instanceof String) {
                IS_NAME_MATCH = ((String)((ConstantExpression) method).getValue()).matches(methodNamePattern);
            }
        }

        if (IS_NAME_MATCH && numArguments != null) {
            return AstUtil.getMethodArguments(methodCall).size() == numArguments;
        }
        return IS_NAME_MATCH;
    }

    public static boolean isMethodNamed(MethodCallExpression methodCall, String methodNamePattern) {
        return isMethodNamed(methodCall, methodNamePattern, null);
    }

    /**
     * Return true if the expression is a constructor call on any of the named classes, with any number of parameters.
     * @param expression - the expression
     * @param classNames - the possible List of class names
     * @return as described
     */
    public static boolean isConstructorCall(Expression expression, List<String> classNames) {
        return expression instanceof ConstructorCallExpression && classNames.contains(expression.getType().getName());  
    }

    /**
     * Return true if the expression is a constructor call on a class that matches the supplied.
     * @param expression - the expression
     * @param classNamePattern - the possible List of class names
     * @return as described
     */
    public static boolean isConstructorCall(Expression expression, String classNamePattern) {
        return expression instanceof ConstructorCallExpression && expression.getType().getName().matches(classNamePattern);
    }

    /**
     * Return the AnnotationNode for the named annotation, or else null.
     * Supports Groovy 1.5 and Groovy 1.6.
     * @param node - the AnnotatedNode
     * @param name - the name of the annotation
     * @return the AnnotationNode or else null 
     */
    public static AnnotationNode getAnnotation(AnnotatedNode node, String name) {
        List<AnnotationNode> annotations = node.getAnnotations();
        for (AnnotationNode annot : annotations) {
            if (annot.getClassNode().getName().equals(name)) {
                return annot;
            }
        }
        return null;
    }

    /**
     * Return true only if the node has the named annotation
     * @param node - the AST Node to check
     * @param name - the name of the annotation
     * @return true only if the node has the named annotation
     */
    public static boolean hasAnnotation(AnnotatedNode node, String name) {
        return AstUtil.getAnnotation(node, name) != null;
    }

    /**
     * Return true only if the node has any of the named annotations
     * @param node - the AST Node to check
     * @param names - the names of the annotations
     * @return true only if the node has any of the named annotations
     */
    public static boolean hasAnyAnnotation(AnnotatedNode node, String... names) {
        for (String name : names) {
            if (hasAnnotation(node, name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the List of VariableExpression objects referenced by the specified DeclarationExpression.
     * @param declarationExpression - the DeclarationExpression
     * @return the List of VariableExpression objects
     */
    public static List<Expression> getVariableExpressions(DeclarationExpression declarationExpression) {
        Expression leftExpression = declarationExpression.getLeftExpression();

        // !important: performance enhancement
        if (leftExpression instanceof ArrayExpression) {
            List<Expression> expressions = ((ArrayExpression) leftExpression).getExpressions();
            return expressions.isEmpty() ? Arrays.asList(leftExpression) : expressions;
        } else if (leftExpression instanceof ListExpression) {
            List<Expression> expressions = ((ListExpression) leftExpression).getExpressions();
            return expressions.isEmpty() ? Arrays.asList(leftExpression) : expressions;
        } else if (leftExpression instanceof TupleExpression) {
            List<Expression> expressions = ((TupleExpression) leftExpression).getExpressions();
            return expressions.isEmpty() ? Arrays.asList(leftExpression) : expressions;
        } else if (leftExpression instanceof VariableExpression) {
            return Arrays.asList(leftExpression);
        }
        // todo: write warning
        return Collections.emptyList();
    }

    /**
     * Return true if the DeclarationExpression represents a 'final' variable declaration.
     *
     * NOTE: THIS IS A WORKAROUND.
     *
     * There does not seem to be an easy way to determine whether the 'final' modifier has been
     * specified for a variable declaration. Return true if the 'final' is present before the variable name.
     */
    public static boolean isFinalVariable(DeclarationExpression declarationExpression, SourceCode sourceCode) {
        if (isFromGeneratedSourceCode(declarationExpression)) {
            return false;
        }
        List<Expression> variableExpressions = getVariableExpressions(declarationExpression);
        if (!variableExpressions.isEmpty()) {
            Expression variableExpression = variableExpressions.get(0);
            int startOfDeclaration = declarationExpression.getColumnNumber();
            int startOfVariableName = variableExpression.getColumnNumber();
            String sourceLine = sourceCode.getLines().get(declarationExpression.getLineNumber() - 1);

            String modifiers = (startOfDeclaration >= 0 && startOfVariableName >= 0) ?
                sourceLine.substring(startOfDeclaration - 1, startOfVariableName - 1) : "";
            return modifiers.contains("final");
        }
        return false;
    }

    /**
     * @return true if the ASTNode was generated (synthetic) rather than from the "real" input source code.
     */
    public static boolean isFromGeneratedSourceCode(ASTNode node) {
        return node.getLineNumber() < 0 || (node instanceof ClassNode && ((ClassNode)node).isScript());
    }

    /**
     * Tells you if the expression is true, which can be true or Boolean.TRUE.
     * @param expression
     *      expression
     * @return
     * as described
     */
    public static boolean isTrue(Expression expression) {
        if (expression == null) {
            return false;
        }
        if (expression instanceof PropertyExpression
                && classNodeImplementsType(((PropertyExpression) expression).getObjectExpression().getType(), Boolean.class)) {
            if (((PropertyExpression) expression).getProperty() instanceof ConstantExpression
                    && "TRUE".equals(((ConstantExpression) ((PropertyExpression) expression).getProperty()).getValue())) {
                return true;
            }
        }
        return ((expression instanceof ConstantExpression) && ((ConstantExpression) expression).isTrueExpression()) ||
                "Boolean.TRUE".equals(expression.getText());
    }

    /**
     * Tells you if the expression is either the true or false literal.
     * @param expression
     *      expression
     * @return
     * as described
     */
    public static boolean isBoolean(Expression expression) {
        return isTrue(expression) || isFalse(expression);
    }

    /**
     * Tells you if the expression is the null literal.
     * @param expression
     *      expression.
     * @return
     * as described
     */
    public static boolean isNull(ASTNode expression) {
        return expression instanceof ConstantExpression && ((ConstantExpression)expression).isNullExpression();
    }

    /**
     * Tells you if the expression is the false expression, either literal or contant.
     * @param expression
     *      expression
     * @return
     * as described
     */
    public static boolean isFalse(Expression expression) {
        if (expression == null) {
            return false;
        }
        if (expression instanceof PropertyExpression && classNodeImplementsType(((PropertyExpression) expression).getObjectExpression().getType(), Boolean.class)) {
            if (((PropertyExpression) expression).getProperty() instanceof ConstantExpression
                    && "FALSE".equals(((ConstantExpression) ((PropertyExpression) expression).getProperty()).getValue())) {
                return true;
            }
        }
        return ((expression instanceof ConstantExpression) && ((ConstantExpression) expression).isFalseExpression())
                || "Boolean.FALSE".equals(expression.getText());
    }

    /**
     * Return true only if the specified object responds to the named method
     * @param object - the object to check
     * @param methodName - the name of the method
     * @return true if the object responds to the named method
     */
    public static boolean respondsTo(Object object, String methodName) {
        MetaClass metaClass = DefaultGroovyMethods.getMetaClass(object);
        if (!metaClass.respondsTo(object, methodName).isEmpty()) {
            return true;
        }
        Map properties = DefaultGroovyMethods.getProperties(object);
        return properties.containsKey(methodName);
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
    public static boolean classNodeImplementsType(ClassNode node, Class target) {
        ClassNode targetNode = ClassHelper.make(target);
        if (node.implementsInterface(targetNode)) {
            return true;
        }
        if (node.isDerivedFrom(targetNode)) {
            return true;
        }
        if (node.getName().equals(target.getName())) {
            return true;
        }
        if (node.getName().equals(target.getSimpleName())) {
            return true;
        }
        if (node.getSuperClass() != null && node.getSuperClass().getName().equals(target.getName())) {
            return true;
        }
        if (node.getSuperClass() != null && node.getSuperClass().getName().equals(target.getSimpleName())) {
            return true;
        }
        if (node.getInterfaces() != null) {
            for (ClassNode declaredInterface : node.getInterfaces()) {
                if (classNodeImplementsType(declaredInterface, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the ASTNode is a declaration of a closure, either as a declaration
     * or a field.
     * @param expression
     *      the target expression
     * @return
     * as described
     */
    public static boolean isClosureDeclaration(ASTNode expression) {
        if (expression instanceof DeclarationExpression) {
            if (((DeclarationExpression) expression).getRightExpression() instanceof ClosureExpression) {
                return true;
            }
        }
        if (expression instanceof FieldNode) {
            ClassNode type = ((FieldNode) expression).getType();
            if (AstUtil.classNodeImplementsType(type, Closure.class)) {
                return true;
            } else if (((FieldNode) expression).getInitialValueExpression() instanceof ClosureExpression) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the parameter names of a method node.
     * @param node
     *      the node to search parameter names on
     * @return
     * argument names, never null
     */
    public static List<String> getParameterNames(MethodNode node) {
        ArrayList<String> result = new ArrayList<String>();

        if (node.getParameters() != null) {
            for (Parameter parameter : node.getParameters()) {
                result.add(parameter.getName());                 
            }
        }
        return result;
    }

    /**
     * Gets the argument names of a method call. If the arguments are not VariableExpressions then a null
     * will be returned.
     * @param methodCall
     *      the method call to search
     * @return
     * a list of strings, never null, but some elements may be null
     */
    public static List<String> getArgumentNames(MethodCallExpression methodCall) {
        ArrayList<String> result = new ArrayList<String>();

        Expression arguments = methodCall.getArguments();
        List<Expression> argExpressions = null;
        if (arguments instanceof ArrayExpression) {
            argExpressions = ((ArrayExpression) arguments).getExpressions();
        } else if (arguments instanceof ListExpression) {
            argExpressions = ((ListExpression) arguments).getExpressions();
        } else if (arguments instanceof TupleExpression) {
            argExpressions = ((TupleExpression) arguments).getExpressions();
        } else {
            LOG.warn("getArgumentNames arguments is not an expected type");
        }

        if (argExpressions != null) {
            for (Expression exp : argExpressions) {
                if (exp instanceof VariableExpression) {
                    result.add(((VariableExpression) exp).getName());
                }
            }
        }
        return result;
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
    public static boolean isBinaryExpressionType(Expression expression, String token) {
        if (expression instanceof BinaryExpression) {
            if (token.equals(((BinaryExpression) expression).getOperation().getText())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the expression is a binary expression with the specified token.
     * @param expression - the expression node
     * @param tokens - the List of allowable (operator) tokens
     * @return as described
     */
    public static boolean isBinaryExpressionType(Expression expression, List<String> tokens) {
        if (expression instanceof BinaryExpression) {
            if (tokens.contains(((BinaryExpression) expression).getOperation().getText())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells you if the expression is a null safe dereference.
     * @param expression
     *      expression
     * @return
     * true if is null safe dereference.
     */
    public static boolean isSafe(Expression expression) {
        if (expression instanceof MethodCallExpression) {
            return ((MethodCallExpression) expression).isSafe();
        }
        if (expression instanceof PropertyExpression) {
            return ((PropertyExpression) expression).isSafe();
        }
        return false;
    }

    /**
     * Tells you if the expression is a spread operator call
     * @param expression
     *      expression
     * @return
     * true if is spread expression
     */
    public static boolean isSpreadSafe(Expression expression) {
        if (expression instanceof MethodCallExpression) {
            return ((MethodCallExpression) expression).isSpreadSafe();
        }
        if (expression instanceof PropertyExpression) {
            return ((PropertyExpression) expression).isSpreadSafe();
        }
        return false;
    }

    /**
     * Tells you if the ASTNode is a method node for the given name, arity, and return type.
     * @param node
     *      the node to inspect
     * @param methodNamePattern
     *      the expected name of the method
     * @param numArguments
     *      the expected number of arguments, optional
     * @param returnType
     *      the expected return type, optional
     * @return
     * true if this node is a MethodNode meeting the parameters. false otherwise
     */
    public static boolean isMethodNode(ASTNode node, String methodNamePattern, Integer numArguments, Class returnType) {
        if (!(node instanceof MethodNode)) {
            return false;
        }
        if (!(((MethodNode) node).getName().matches(methodNamePattern))) {
            return false;
        }
        if (numArguments != null && ((MethodNode)node).getParameters() != null && ((MethodNode)node).getParameters().length != numArguments) {
            return false;
        }
        if (returnType != null && !AstUtil.classNodeImplementsType(((MethodNode) node).getReturnType(), returnType)) {
            return false;
        }
        return true;
    }
    
    public static boolean isMethodNode(ASTNode node, String methodNamePattern, Integer numArguments) {
        return isMethodNode(node, methodNamePattern, numArguments, null);
    }
    public static boolean isMethodNode(ASTNode node, String methodNamePattern) {
        return isMethodNode(node, methodNamePattern, null, null);
    }

    /**
     * Tells you if the given ASTNode is a VariableExpression with the given name.
     * @param expression
     *      any AST Node
     * @param pattern
     *      a string pattern to match
     * @return
     * true if the node is a variable with the specified name
     */
    public static boolean isVariable(ASTNode expression, String pattern) {
        return (expression instanceof VariableExpression && ((VariableExpression) expression).getName().matches(pattern));
    }

    /**
     * Tells you if the ASTNode has a public modifier on it. If the node does not have modifiers at all (like
     * a variable expression) then false is returned.
     * @param node
     *      node to query
     * @return
     * true if definitely public, false if not public or unknown
     */
    public static boolean isPublic(ASTNode node) {
        Integer modifiers = null;
        // !important - Performance improvement
        if (node instanceof ClassNode) {
            modifiers = ((ClassNode) node).getModifiers();
        } else if (node instanceof FieldNode) {
            modifiers = ((FieldNode) node).getModifiers();
        }else if (node instanceof MethodNode) {
            modifiers = ((MethodNode) node).getModifiers();
        }else if (node instanceof PropertyNode) {
            modifiers = ((PropertyNode) node).getModifiers();
        } else {
            LOG.warn("isPublic node is not an expected type");
        }
        if (modifiers != null) {
            return Modifier.isPublic(modifiers);
        }
        return false;
    }

    public static boolean isNotNullCheck(Object expression) {
        if (expression instanceof BinaryExpression) {
            if ("!=".equals(((BinaryExpression) expression).getOperation().getText())) {
                if (isNull(((BinaryExpression) expression).getLeftExpression())
                        || isNull(((BinaryExpression) expression).getRightExpression())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNullCheck(Object expression) {
        if (expression instanceof BinaryExpression) {
            if ("==".equals(((BinaryExpression) expression).getOperation().getText())) {
                if (isNull(((BinaryExpression) expression).getLeftExpression()) || isNull(((BinaryExpression) expression).getRightExpression())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getNullComparisonTarget(Object expression) {
        if (expression instanceof BinaryExpression && "!=".equals(((BinaryExpression) expression).getOperation().getText())) {
            if (isNull(((BinaryExpression) expression).getLeftExpression())) {
                return ((BinaryExpression) expression).getRightExpression().getText();
            } else if (isNull(((BinaryExpression) expression).getRightExpression())) {
                return ((BinaryExpression) expression).getLeftExpression().getText();
            }
        }
        return null;
    }

    public static boolean isInstanceOfCheck(Object expression) {
        return (expression instanceof BinaryExpression && "instanceof".equals(((BinaryExpression) expression).getOperation().getText()));
    }

    public static String getInstanceOfTarget(Object expression) {
        if (isInstanceOfCheck(expression)) {
            return ((BinaryExpression)expression).getLeftExpression().getText();
        }
        return null;
    }

    /**
     * Supports discovering many common JDK types, but not all.
     */
    public static Class getFieldType(ClassNode node, String fieldName) {
        while (node != null) {
            for (FieldNode field: node.getFields()) {
                if (field.getName().equals(fieldName)) {
                    return getFieldType(field);
                }
            }
            node = node.getOuterClass();
        }
        return null;
    }

    /**
     * Supports discovering many common JDK types, but not all.
     */
    public static Class getFieldType(FieldNode field) {
        // Step 1: Analyze the field's declared type
        Class declaredType = getClassForClassNode(field.getType());
        if (declaredType != null) {
            return declaredType;
        }

        // Step 2: Analyze the cast type of the initial expression
        if (field.getInitialExpression() != null) {
            Class castType = getClassForClassNode(field.getInitialExpression().getType());
            if (castType != null) {
                return castType;
            }
        }

        // Step 3: Look at the literal within the constant
        if (field.getInitialExpression() instanceof ConstantExpression) {
            Object constantValue = ((ConstantExpression) field.getInitialExpression()).getValue();
            if (constantValue instanceof String) {
                return String.class;
            } else if (isBoolean(field.getInitialExpression())) {
                return Boolean.class;
            } else if (constantValue.getClass() == Integer.class || constantValue.getClass() == Integer.TYPE) {
                return Integer.class;
            } else if (constantValue.getClass() == Long.class || constantValue.getClass() == Long.TYPE) {
                return Long.class;
            } else if (constantValue.getClass() == Double.class || constantValue.getClass() == Double.TYPE) {
                return Double.class;
            } else if (constantValue.getClass() == Float.class || constantValue.getClass() == Float.TYPE) {
                return Float.class;
            }
        }
        return null;
    }

    /**
     * This is private. It is a helper function for the utils.
     */
    private static Class getClassForClassNode(ClassNode type) {
        // todo hamlet - move to a different "InferenceUtil" object
        Class primitiveType = getPrimitiveType(type);
        if (primitiveType != null) {
            return primitiveType;
        } else if (classNodeImplementsType(type, String.class)) {
            return String.class;
        } else if (classNodeImplementsType(type, ReentrantLock.class)) {
            return ReentrantLock.class;
        } else if (type.getName() != null && type.getName().endsWith("[]")) {
            return Object[].class;       // better type inference could be done, but oh well
        }
        return null;
    }

    private static Class getPrimitiveType(ClassNode type) {
        if (classNodeImplementsType(type, Boolean.class) || classNodeImplementsType(type, Boolean.TYPE)) {
            return Boolean.class;
        } else if (classNodeImplementsType(type, Long.class) || classNodeImplementsType(type, Long.TYPE)) {
            return Long.class;
        } else if (classNodeImplementsType(type, Short.class) || classNodeImplementsType(type, Short.TYPE)) {
            return Short.class;
        } else if (classNodeImplementsType(type, Double.class) || classNodeImplementsType(type, Double.TYPE)) {
            return Double.class;
        } else if (classNodeImplementsType(type, Float.class) || classNodeImplementsType(type, Float.TYPE)) {
            return Float.class;
        } else if (classNodeImplementsType(type, Character.class) || classNodeImplementsType(type, Character.TYPE)) {
            return Character.class;
        } else if (classNodeImplementsType(type, Integer.class) || classNodeImplementsType(type, Integer.TYPE)) {
            return Integer.class;
        } else if (classNodeImplementsType(type, Long.class) || classNodeImplementsType(type, Long.TYPE)) {
            return Long.class;
        } else if (classNodeImplementsType(type, Byte.class) || classNodeImplementsType(type, Byte.TYPE)) {
            return Byte.class;
        }
        return null;
    }

    public static boolean isThisReference(Expression expression) {
        return expression instanceof VariableExpression && "this".equals(((VariableExpression) expression).getName());
    }

    public static boolean isSuperReference(Expression expression) {
        return expression instanceof VariableExpression && "super".equals(((VariableExpression) expression).getName());
    }

    public static boolean classNodeHasProperty(ClassNode classNode, String propertyName) {
        if (classNode.getFields() != null) {
            for (FieldNode field : classNode.getFields()) {
                if (propertyName.equals(field.getName())) {
                    return true;
                }
            }
        }
        if (classNode.getProperties() != null) {
            for (PropertyNode property : classNode.getProperties()) {
                if (propertyName.equals(property.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * gets the first non annotation line number of a node, taking into account annotations. 
     */
    public static int findFirstNonAnnotationLine(ASTNode node, SourceCode sourceCode) {
        if (node instanceof AnnotatedNode && !((AnnotatedNode) node).getAnnotations().isEmpty()) {

            // HACK: Groovy line numbers are broken when annotations have a parameter :(
            // so we must look at the lineNumber, not the lastLineNumber
            AnnotationNode lastAnnotation = null;
            for (AnnotationNode annotation : ((AnnotatedNode) node).getAnnotations()) {
                if (lastAnnotation == null) lastAnnotation = annotation;
                else if (lastAnnotation.getLineNumber() < annotation.getLineNumber()) lastAnnotation = annotation;
            }

            String rawLine = getRawLine(sourceCode, lastAnnotation.getLastLineNumber()-1);
            // is the annotation the last thing on the line?
            if (rawLine.length() > lastAnnotation.getLastColumnNumber()) {
                // no it is not
                return lastAnnotation.getLastLineNumber();
            }
            // yes it is the last thing, return the next thing
            return lastAnnotation.getLastLineNumber() + 1;
        }
        return node.getLineNumber();
    }

    public static String getRawLine(SourceCode sourceCode, int lineNumber) {
        List<String> allLines = sourceCode.getLines();
        return (lineNumber >= 0) && lineNumber < allLines.size() ? allLines.get(lineNumber) : null;
    }

    public static boolean isOneLiner(Object statement) {
        if (statement instanceof BlockStatement && ((BlockStatement) statement).getStatements() != null) {
            if (((BlockStatement) statement).getStatements().size() == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean expressionIsNullCheck(ASTNode node) {
        if (!(node instanceof IfStatement)) {
            return false;
        }
        if (!(((IfStatement) node).getBooleanExpression() != null)) {
            return false;
        }
        BooleanExpression booleanExp = ((IfStatement) node).getBooleanExpression();
        if (isBinaryExpressionType(booleanExp.getExpression(), "==")) {
            if (isNull(((BinaryExpression)booleanExp.getExpression()).getLeftExpression())
                    && ((BinaryExpression) booleanExp.getExpression()).getRightExpression() instanceof VariableExpression) {
                return true;
            } else if (isNull(((BinaryExpression) booleanExp.getExpression()).getRightExpression())
                    && ((BinaryExpression) booleanExp.getExpression()).getLeftExpression() instanceof VariableExpression) {
                return true;
            }
        } else if (booleanExp.getExpression() instanceof NotExpression && ((NotExpression) booleanExp.getExpression()).getExpression() instanceof VariableExpression) {
            return true;
        }
        return false;
    }

    public static boolean expressionIsAssignment(ASTNode node, String variableName) {
        if (node instanceof Expression && isBinaryExpressionType((Expression) node, "=")) {
            if (isVariable(((BinaryExpression) node).getLeftExpression(), variableName)) {
                return true;
            }
        } else if (node instanceof ExpressionStatement && isBinaryExpressionType(((ExpressionStatement) node).getExpression(), "=")) {
            if (AstUtil.isVariable(((BinaryExpression)((ExpressionStatement) node).getExpression()).getLeftExpression(), variableName)) {
                return true;
            }
        }
        return false;
    }

    private static String repeat(char c, int count) {
        String result = "";
        for (int x = 0; x <= count; x++) {
            result = result + c;
        }
        return result;
    }

    public static String getNodeText(ASTNode expression, SourceCode sourceCode) {
        String line = sourceCode.getLines().get(expression.getLineNumber() - 1);

        // If multi-line, only include rest of first line
        int endColumn = expression.getLineNumber() == expression.getLastLineNumber()
                ? expression.getLastColumnNumber() - 1
                : line.length();
        return line.substring(expression.getColumnNumber() - 1, endColumn);
    }

    public static String getDeclaration(ASTNode node, SourceCode sourceCode) {
        if (node.getLineNumber() < 1) return "";
        if (node.getLastLineNumber() < 1) return "";
        if (node.getColumnNumber() < 1) return "";
        if (node.getLastColumnNumber() < 1) return "";

        String acc = "";
        for (int lineIndex = node.getLineNumber() - 1; lineIndex <= node.getLastLineNumber() -1; lineIndex++) {
            // the raw line is required to apply columnNumber and lastColumnNumber
            String line = getRawLine(sourceCode, lineIndex);

            // extract the relevant part of the first line
            if (lineIndex == node.getLineNumber() - 1) {
                int nonRelevantColumns = node.getColumnNumber() - 1;
                line = line.replaceFirst(".{" + nonRelevantColumns + "}", repeat(' ', nonRelevantColumns)); // retain the line length as it's important when using lastColumnNumber
            }

            // extract the relevant part of the last line
            if (lineIndex == node.getLastLineNumber() - 1) {
                int stopIndex = node.getLastColumnNumber() < line.length() ? node.getLastColumnNumber() - 2 : line.length() - 1;
                line = line.substring(0, stopIndex); 
            }

            if (line.contains("{")) {
                acc += line.substring(0, line.indexOf("{"));
                break;
            } else {
                acc += line + " ";
            }
        }
        return acc;
    }

    public static String createPrettyExpression(ASTNode expression) {
        if (expression instanceof ConstantExpression && ((ConstantExpression) expression).getValue() instanceof String) {
            return "'" + expression.getText() + "'";
        }
        if (expression instanceof GStringExpression) {
            return "\"" + expression.getText() + "\"";
        }
        return expression.getText();
    }

}