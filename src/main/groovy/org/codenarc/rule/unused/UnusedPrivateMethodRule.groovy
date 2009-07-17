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
package org.codenarc.rule.unused

import org.codehaus.groovy.ast.FieldNode
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression

/**
 * Rule that checks for private methods that are not referenced within the same class.
 *
 * Known limitations:
 * <ul>
 *  <li>Does not handle method reference through property access: getName() accessed as x.name</li>
 *  <li>Does not handle method invocations when method name is a GString (e.g. this."${methodName}"</li>
 *  <li>Does not handle invoking private method of another instance (i.e. other than 'this')</li>
 *  <li>Does not differentiate between multiple private methods with the same name but different parameters (i.e., overloaded)</li>
 *  <li>Does not check constructors</li>
 * </ul>
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class UnusedPrivateMethodRule extends AbstractAstVisitorRule {
    String name = 'UnusedPrivateMethod'
    int priority = 2
    Class astVisitorClass = UnusedPrivateMethodAstVisitor
}

class UnusedPrivateMethodAstVisitor extends AbstractAstVisitor  {
    private unusedPrivateMethods
    private currentClassNode

    void visitClass(ClassNode classNode) {
        this.currentClassNode = classNode
        this.unusedPrivateMethods = classNode.methods.findAll { methodNode ->
            methodNode.modifiers & FieldNode.ACC_PRIVATE
        }
        super.visitClass(classNode)

        unusedPrivateMethods.each { unusedPrivateMethod ->
            addViolation(unusedPrivateMethod)
        }
        this.currentClassNode = null
    }

    void visitMethodCallExpression(MethodCallExpression expression) {
        if (isMethodCall(expression, 'this')) {    
            removeUnusedPrivateMethods(expression.method.value)
        }

        // Static invocation through current class name
        if (isMethodCall(expression, currentClassNode.nameWithoutPackage)) {    
            removeUnusedPrivateMethods(expression.method.value)
        }

        super.visitMethodCallExpression(expression)
    }

    private boolean isMethodCall(MethodCallExpression expression, String targetName) {
        return expression.objectExpression instanceof VariableExpression &&
               expression.objectExpression.name == targetName &&
               expression.method instanceof ConstantExpression
    }

    private void removeUnusedPrivateMethods(String name, boolean staticOnly=false) {
        def referencedMethods = unusedPrivateMethods.findAll { methodNode ->
            methodNode.name == name &&
            (!staticOnly || methodNode.modifiers & FieldNode.ACC_STATIC)
        }
        referencedMethods.each { referencedMethod ->
            unusedPrivateMethods.remove(referencedMethod)
        }
    }
}