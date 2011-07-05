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

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The code uses x % 2 == 1 to check to see if a value is odd, but this won't work for negative numbers (e.g., (-5) % 2 == -1).
 * If this code is intending to check for oddness, consider using x & 1 == 1, or x % 2 != 0.
 *
 * @author 'Hamlet D'Arcy'
 */
class BrokenOddnessCheckRule extends AbstractAstVisitorRule {
    String name = 'BrokenOddnessCheck'
    int priority = 2
    Class astVisitorClass = BrokenOddnessCheckAstVisitor
}

class BrokenOddnessCheckAstVisitor extends AbstractAstVisitor {

    @SuppressWarnings('NestedBlockDepth')  // this code isn't that bad
    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (AstUtil.isBinaryExpressionType(expression, '==')) {
            if (AstUtil.isBinaryExpressionType(expression.leftExpression, '%')) {
                if (AstUtil.isConstant(expression.rightExpression, 1)) {
                    BinaryExpression modExp = expression.leftExpression
                    if (AstUtil.isConstant(modExp.rightExpression, 2)) {
                        def variable = modExp.leftExpression.text
                        addViolation(expression, "The code uses '($variable % 2 == 1)' to check for oddness, which does not work for negative numbers. Use ($variable & 1 == 1) or ($variable % 2 != 0) instead")
                    } 
                }
            } else if (AstUtil.isBinaryExpressionType(expression.rightExpression, '%')) {

                if (AstUtil.isConstant(expression.leftExpression, 1)) {
                    BinaryExpression modExp = expression.rightExpression
                    if (AstUtil.isConstant(modExp.rightExpression, 2)) {
                        def variable = modExp.leftExpression.text
                        addViolation(expression, "The code uses '(1 == $variable % 2)' to check for oddness, which does not work for negative numbers. Use ($variable & 1 == 1) or ($variable % 2 != 0) instead")
                    }
                }
            }
        }
        super.visitBinaryExpression(expression)
    }
}
