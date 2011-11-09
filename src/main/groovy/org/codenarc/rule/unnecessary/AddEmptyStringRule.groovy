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

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Finds empty string literals which are being added. This is an inefficient way to convert any type to a String.
 *
 * @author Hamlet D'Arcy
 */
class AddEmptyStringRule extends AbstractAstVisitorRule {
    String name = 'AddEmptyString'
    int priority = 2
    Class astVisitorClass = AddEmptyStringAstVisitor
}

class AddEmptyStringAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (isFirstVisit(expression) && AstUtil.isBinaryExpressionType(expression, '+')) {
            if (expression.leftExpression instanceof ConstantExpression && expression.leftExpression.value == '') {
                addViolation expression, 'Concatenating an empty string is an inefficient way to convert an object to a String. Consider using toString() or String.valueOf(Object)'
            }
        }
        
        super.visitBinaryExpression(expression)
    }
}
