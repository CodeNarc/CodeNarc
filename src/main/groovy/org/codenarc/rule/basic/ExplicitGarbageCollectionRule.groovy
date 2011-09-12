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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * Calls to System.gc(), Runtime.getRuntime().gc(), and System.runFinalization() are not advised. Code should have the
 * same behavior whether the garbage collection is disabled using the option -Xdisableexplicitgc or not. Moreover,
 * "modern" jvms do a very good job handling garbage collections. If memory usage issues unrelated to memory leaks
 * develop within an application, it should be dealt with JVM options rather than within the code itself.
 *
 * @author 'Hamlet D'Arcy'
 */
class ExplicitGarbageCollectionRule extends AbstractAstVisitorRule {
    String name = 'ExplicitGarbageCollection'
    int priority = 2
    Class astVisitorClass = ExplicitGarbageCollectionAstVisitor
}

class ExplicitGarbageCollectionAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodCall(call, ['System'], ['gc', 'runFinalization'], 0)) {
            addViolation call, 'Garbage collection should not be explicitly forced'
        } else if (AstUtil.isMethodNamed(call, 'gc', 0)) {
            if (isPropertyExpression(call.objectExpression, 'Runtime', 'runtime')) {
                addViolation call, 'Garbage collection should not be explicitly forced'
            } else if (AstUtil.isMethodCall(call.objectExpression, 'Runtime', 'getRuntime', 0)) {
                addViolation call, 'Garbage collection should not be explicitly forced'
            }

        } 
    }

    private static boolean isPropertyExpression(Expression expression, String object, String propertyName) {
        if (!(expression instanceof PropertyExpression)) {
            return false
        }

        if (expression.property instanceof ConstantExpression && expression.property.value == propertyName) {
            if (expression.objectExpression instanceof VariableExpression && expression.objectExpression.variable == object) {
                return true
            }
        }
        false
    }
}
