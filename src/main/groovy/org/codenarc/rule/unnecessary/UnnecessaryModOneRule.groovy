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
import org.codehaus.groovy.ast.expr.Expression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Any expression mod 1 (exp % 1) is guaranteed to always return zero. This code is probably an error, and should be either (exp & 1) or (exp % 2).
 *
 * @author 'Hamlet D'Arcy'
  */
class UnnecessaryModOneRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryModOne'
    int priority = 3
    Class astVisitorClass = UnnecessaryModOneAstVisitor
}

class UnnecessaryModOneAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (AstUtil.isBinaryExpressionType(expression, '%')) {
            Expression rhs = expression.rightExpression
            if (rhs instanceof ConstantExpression && rhs.value == 1) {
                Expression lhs = expression.leftExpression
                addViolation(expression, "$expression.text is guaranteed to be zero. Did you mean ($lhs.text & 1) or ($lhs.text % 2)")
            }
        }
        super.visitBinaryExpression(expression)
    }

}
