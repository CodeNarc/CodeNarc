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
package org.codenarc.rule.spock

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.syntax.Types

/**
 * A specification that mutates system properties without restoring them leaks that state into every
 * specification that runs after it, in a way that depends on the execution order and on whether the
 * suite runs in parallel. The failure that results shows up in a <em>different</em> specification,
 * which is the most expensive kind of test failure to diagnose.
 *
 * Spock ships <code>@RestoreSystemProperties</code> for exactly this: it snapshots the system
 * properties before and restores them after the annotated feature - or, on the class, around every
 * feature. This rule reports a mutation of <code>System.properties</code> in a Spock specification
 * when neither the enclosing method, nor the specification class, nor a superclass declared in the
 * same source file carries that annotation.
 *
 * Only positively identified mutations of <code>System</code> are reported: the receiver has to be
 * spelled <code>System</code> or <code>java.lang.System</code>, because CodeNarc runs without a
 * classpath. A mutation through a local alias (<code>def props = System.properties</code>) is not
 * reported.
 *
 * @author Leonard Bruenings
 */
class SpockUseRestoreSystemPropertiesRule extends AbstractSpockRule {

    String name = 'SpockUseRestoreSystemProperties'
    int priority = 2
    Class astVisitorClass = SpockUseRestoreSystemPropertiesAstVisitor
}

class SpockUseRestoreSystemPropertiesAstVisitor extends AbstractSpockAstVisitor<SpockUseRestoreSystemPropertiesRule> {

    private static final String ANNOTATION_NAME = 'RestoreSystemProperties'

    /** The receiver has to be spelled out - there is no classpath to resolve anything else against */
    private static final List<String> SYSTEM_NAMES = ['System', 'java.lang.System']

    /** Mutating methods declared on java.lang.System itself */
    private static final List<String> SYSTEM_MUTATING_METHODS = ['setProperty', 'clearProperty', 'setProperties']

    /** Mutating methods of the Properties object returned by System.getProperties() */
    private static final List<String> PROPERTIES_MUTATING_METHODS = [
        'setProperty', 'put', 'putAll', 'putIfAbsent', 'remove', 'clear', 'replace', 'replaceAll',
        'merge', 'compute', 'computeIfAbsent', 'computeIfPresent', 'load', 'loadFromXML',
    ]

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        super.visitMethodCallExpression(call)
        String methodName = SpockUtil.getMethodName(call)
        if (methodName == null) {
            return
        }
        if (isSystemClass(call.objectExpression) && methodName in SYSTEM_MUTATING_METHODS) {
            addMutationViolation(call, "System.${methodName}")
        } else if (isSystemProperties(call.objectExpression) && methodName in PROPERTIES_MUTATING_METHODS) {
            addMutationViolation(call, "${describe(call.objectExpression)}.${methodName}")
        }
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        super.visitBinaryExpression(expression)
        if (!isAssignment(expression)) {
            return
        }
        Expression target = expression.leftExpression
        if (isSystemPropertiesTarget(target)) {
            addMutationViolation(expression, describeTarget(target))
        }
    }

    private void addMutationViolation(Expression expression, String description) {
        // Only mutations inside a method of the specification are reported; a mutation elsewhere
        // (e.g. a field initializer) has no enclosing method to check the annotation against
        if (currentMethod == null || SpockUtil.hasAnnotationOnMethodOrClass(currentMethod, ANNOTATION_NAME)) {
            return
        }
        addViolation(expression, "${description} mutates global state; annotate the feature or class with @${ANNOTATION_NAME}")
    }

    private static boolean isAssignment(BinaryExpression expression) {
        return Types.ofType(expression.operation.type, Types.ASSIGNMENT_OPERATOR)
    }

    /**
     * The single place that decides whether an expression denotes the global system properties. The
     * mirror image of this check - for the meta class of a type - belongs in its own rule.
     */
    private static boolean isSystemProperties(Expression expression) {
        if (expression instanceof PropertyExpression) {
            PropertyExpression property = expression as PropertyExpression
            return property.propertyAsString == 'properties' && isSystemClass(property.objectExpression)
        }
        if (expression instanceof MethodCallExpression) {
            MethodCallExpression call = expression as MethodCallExpression
            return SpockUtil.getMethodName(call) == 'getProperties' && isSystemClass(call.objectExpression) && hasNoArguments(call)
        }
        return false
    }

    private static boolean isSystemPropertiesTarget(Expression target) {
        // System.properties = props
        if (isSystemProperties(target)) {
            return true
        }
        // System.properties.foo = 'bar'
        if (target instanceof PropertyExpression) {
            return isSystemProperties((target as PropertyExpression).objectExpression)
        }
        // System.properties['foo'] = 'bar'
        if (target instanceof BinaryExpression) {
            BinaryExpression binary = target as BinaryExpression
            return binary.operation.type == Types.LEFT_SQUARE_BRACKET && isSystemProperties(binary.leftExpression)
        }
        return false
    }

    private static boolean isSystemClass(Expression expression) {
        return qualifiedName(expression) in SYSTEM_NAMES
    }

    /**
     * @return the dotted name an expression spells out, e.g. 'System' or 'java.lang.System', or null
     *         if the expression is not a plain name
     */
    private static String qualifiedName(Expression expression) {
        if (expression instanceof PropertyExpression) {
            PropertyExpression property = expression as PropertyExpression
            String owner = qualifiedName(property.objectExpression)
            String name = property.propertyAsString
            return owner != null && name != null ? "${owner}.${name}" : null
        }
        return unqualifiedName(expression)
    }

    private static String unqualifiedName(Expression expression) {
        if (expression instanceof VariableExpression) {
            return (expression as VariableExpression).name
        }
        if (expression instanceof ClassExpression) {
            return (expression as ClassExpression).type.name
        }
        return null
    }

    private static boolean hasNoArguments(MethodCallExpression call) {
        Expression arguments = call.arguments
        return !(arguments instanceof TupleExpression) || (arguments as TupleExpression).expressions.isEmpty()
    }

    private static String describe(Expression expression) {
        return expression instanceof MethodCallExpression ? 'System.getProperties()' : 'System.properties'
    }

    private static String describeTarget(Expression target) {
        if (target instanceof BinaryExpression) {
            return "${describe((target as BinaryExpression).leftExpression)}[...]"
        }
        if (target instanceof PropertyExpression && !isSystemProperties(target)) {
            PropertyExpression property = target as PropertyExpression
            String name = property.propertyAsString
            return name != null ? "${describe(property.objectExpression)}.${name}" : "${describe(property.objectExpression)}[...]"
        }
        return 'System.properties'
    }
}
