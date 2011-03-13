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
import org.codehaus.groovy.ast.ClassNode

/**
 * Catches concatenation of two string literals on the same line. These can safely by joined. 
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class ConsecutiveStringConcatenationRule extends AbstractAstVisitorRule {
    String name = 'ConsecutiveStringConcatenation'
    int priority = 3
    Class astVisitorClass = ConsecutiveStringConcatenationAstVisitor
}

class ConsecutiveStringConcatenationAstVisitor extends AbstractAstVisitor {
    final static DEFAULT_NAME = '<unknown>'
    def className = DEFAULT_NAME

    @Override
    protected void visitClassEx(ClassNode node) {
        className = node.name
    }

    @Override protected void visitClassComplete(ClassNode node) {
        className = DEFAULT_NAME
    }

    @Override
    void visitBinaryExpression(BinaryExpression expression) {

        if (AstUtil.isBinaryExpressionType(expression, '+')) {
            Expression left = expression.leftExpression
            Expression right = expression.rightExpression
            if (areJoinableConstants(left, right)) {
                if (left instanceof GStringExpression || right instanceof GStringExpression) {
                    addViolation(expression, "String concatenation in class $className can be joined into a single literal")
                } else {
                    def lvalue = escape(left.value)
                    def rvalue = escape(right.value)
                    addViolation(expression, "String concatenation in class $className can be joined into the literal '${lvalue}${rvalue}'")
                }
            }
        }

        super.visitBinaryExpression(expression)
    }
    private static String escape(value) {
        if (value instanceof String) {
            return value.replaceAll('\n', '\\\\n').replaceAll("'", "\'")
        }
        value
    }

    private static boolean areJoinableConstants(Expression left, Expression right) {
        if (left.lastLineNumber != right.lineNumber) {
            return false
        }
        if (isJoinableType(left) && isJoinableType(right)) {
            // don't join two numbers
            if (!isNumberLiteral(left) || !isNumberLiteral(right)) {
                return true
            }
        }
        false
    }

    private static boolean isNumberLiteral(Expression node) {
        (node instanceof ConstantExpression && !AstUtil.isNull(node) && node.type.isResolved() && Number.isAssignableFrom(node.type.typeClass))
    }

    private static boolean isJoinableType(Expression node) {
        if (node instanceof ConstantExpression && !AstUtil.isNull(node)) {
            return true
        }
        return node instanceof GStringExpression
    }
}
