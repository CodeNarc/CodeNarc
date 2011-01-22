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
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Catches concatenation of two string literals on the same line. These can safely by joined. 
 *
 * @author Hamlet D'Arcy
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class ConsecutiveStringConcatenationRule extends AbstractAstVisitorRule {
    String name = 'ConsecutiveStringConcatenation'
    int priority = 2
    Class astVisitorClass = ConsecutiveStringConcatenationAstVisitor
}

class ConsecutiveStringConcatenationAstVisitor extends AbstractAstVisitor {
    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (AstUtil.isBinaryExpressionType(expression, '+')) {
            if (areJoinableConstants(expression.leftExpression, expression.rightExpression)) {
                addViolation(expression, 'String concatenation can be joined into a single literal')
            }
        }

        super.visitBinaryExpression(expression)
    }

    private static boolean areJoinableConstants(Expression left, Expression right) {
        if (left.lastLineNumber != right.lineNumber) {
            return false
        }
        if (isJoinableType(left) && isJoinableType(right)) {
            return true
        }
        false
    }

    private static boolean isJoinableType(Expression expression) {
        if (expression instanceof ConstantExpression && expression.value instanceof String) {
            return true
        }
        return expression instanceof GStringExpression
    }
}
