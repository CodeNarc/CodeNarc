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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codehaus.groovy.ast.expr.*

/**
 * This rule checks for excessively verbose methods of accessing the last element of an array or list. For instance,
 * it is possible to access the last element of an array by performing array[array.length - 1], in Groovy it is
 * simpler to either call array.last() or array[-1]. The same is true for lists. This violation is triggered whenever a
 * get, getAt, or array-style access is used with an object size check.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCallForLastElementRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryCallForLastElement'
    int priority = 3
    Class astVisitorClass = UnnecessaryCallForLastElementAstVisitor
}

class UnnecessaryCallForLastElementAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'get', 1) || AstUtil.isMethodNamed(call, 'getAt', 1)) {
            def methodObjectText = call.objectExpression.text
            if (call.arguments instanceof ArgumentListExpression) {
                Expression exp = call.arguments?.expressions?.first()
                if (isSubtractOneOnObject(methodObjectText, exp)) {
                    addViolation call,
                            "Unnecessarily complex access of last element. This can be simplified to ${methodObjectText}.last() or ${methodObjectText}[-1]"
                }
            }
        }
        super.visitMethodCallExpression call
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (expression.operation.getText() == '[') {
            def methodObjectText = expression.leftExpression.text
            if (isSubtractOneOnObject(methodObjectText, expression.rightExpression)) {
                addViolation expression,
                        "Unnecessarily complex access of last element. This can be simplified to ${methodObjectText}.last() or ${methodObjectText}[-1]"
            }
        }

        super.visitBinaryExpression expression
    }

    private static isSubtractOneOnObject(String methodObjectText, Expression exp) {
        if (!(exp instanceof BinaryExpression)) {
            return false
        }
        if (exp.operation.text != '-') {
            return false
        }
        if (!(exp.rightExpression instanceof ConstantExpression)) {
            return false
        }
        if (exp.rightExpression.value != 1) {
            return false
        }
        if (exp.leftExpression instanceof MethodCallExpression) {
            if (AstUtil.isMethodCall(exp.leftExpression, methodObjectText, 'size', 0)) {
                return true
            }
        }
        if (exp.leftExpression instanceof PropertyExpression) {
            PropertyExpression property = exp.leftExpression
            if (property.objectExpression.text != methodObjectText) {
                return false
            }
            if (!(property.property instanceof ConstantExpression)) {
                return false
            }
            if (property.property.value == 'length') {
                return true
            }
        }
        false
    }
}
