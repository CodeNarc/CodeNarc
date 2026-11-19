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

import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;

/**
 * Contains static utility methods for inferring the Java type behind a Groovy AST node.
 * <p/>
 * This is an internal class and its API is subject to change.
 */
public class InferenceUtil {

    /**
     * Private constructor. All methods are static.
     */
    private InferenceUtil() { }

    /**
     * Return the inferred type for the specified field, or null if it cannot be determined.
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
            if (constantValue == null) {
                return null;
            } else if (constantValue instanceof String) {
                return String.class;
            } else if (AstUtil.isBoolean(field.getInitialExpression())) {
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
        Class primitiveType = getPrimitiveType(type);
        if (primitiveType != null) {
            return primitiveType;
        } else if (AstUtil.classNodeImplementsType(type, String.class)) {
            return String.class;
        } else if (AstUtil.classNodeImplementsType(type, ReentrantLock.class)) {
            return ReentrantLock.class;
        } else if (type.isArray() || (type.getName() != null && type.getName().endsWith("[]"))) {
            return Object[].class;       // better type inference could be done, but oh well
        }
        return null;
    }

    private static Class getPrimitiveType(ClassNode type) {
        if (AstUtil.classNodeImplementsType(type, Boolean.class) || AstUtil.classNodeImplementsType(type, Boolean.TYPE)) {
            return Boolean.class;
        } else if (AstUtil.classNodeImplementsType(type, Long.class) || AstUtil.classNodeImplementsType(type, Long.TYPE)) {
            return Long.class;
        } else if (AstUtil.classNodeImplementsType(type, Short.class) || AstUtil.classNodeImplementsType(type, Short.TYPE)) {
            return Short.class;
        } else if (AstUtil.classNodeImplementsType(type, Double.class) || AstUtil.classNodeImplementsType(type, Double.TYPE)) {
            return Double.class;
        } else if (AstUtil.classNodeImplementsType(type, Float.class) || AstUtil.classNodeImplementsType(type, Float.TYPE)) {
            return Float.class;
        } else if (AstUtil.classNodeImplementsType(type, Character.class) || AstUtil.classNodeImplementsType(type, Character.TYPE)) {
            return Character.class;
        } else if (AstUtil.classNodeImplementsType(type, Integer.class) || AstUtil.classNodeImplementsType(type, Integer.TYPE)) {
            return Integer.class;
        } else if (AstUtil.classNodeImplementsType(type, Long.class) || AstUtil.classNodeImplementsType(type, Long.TYPE)) {
            return Long.class;
        } else if (AstUtil.classNodeImplementsType(type, Byte.class) || AstUtil.classNodeImplementsType(type, Byte.TYPE)) {
            return Byte.class;
        }
        return null;
    }
}
