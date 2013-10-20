/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.design

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.util.AstUtil

/**
 * Checks for calls to Locale.setDefault(), which sets the Locale across the entire JVM.
 *
 * @author mingzhi.huang, rob.patrick
 * @author Chris Mair
 */
class LocaleSetDefaultRule extends AbstractAstVisitorRule {
    String name = 'LocaleSetDefault'
    int priority = 2
    Class astVisitorClass = LocaleSetDefaultAstVisitor
}

class LocaleSetDefaultAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (AstUtil.isMethodCall(call, ['Locale', /java\.util\.Locale/], ['setDefault'])) {
            addViolation(call, 'Avoid explicit calls to Locale.setDefault, which sets the Locale across the entire JVM')
        }
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {
        if (expression.operation.text == '=') {
            def leftExpression = expression.leftExpression
            if (leftExpression instanceof PropertyExpression &&
                    (AstUtil.isVariable(leftExpression.objectExpression, /Locale/) ||
                        leftExpression.objectExpression.text == 'java.util.Locale') &&
                    AstUtil.isConstant(leftExpression.property, 'default') ) {
                addViolation(expression, 'Avoid explicit assignment to Locale.default, which sets the Locale across the entire JVM')
            }
        }
        super.visitBinaryExpression(expression)
    }
}
