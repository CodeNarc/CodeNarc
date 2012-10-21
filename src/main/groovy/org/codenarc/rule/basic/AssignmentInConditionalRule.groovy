/*
 * Copyright 2012 the original author or authors.
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
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * An assignment operator (=) was used in a conditional test. This is usually a typo, and the comparison operator (==) was intended.
 *
 * @author 'Hamlet D'Arcy'
 * @author Chris Mair
 */
class AssignmentInConditionalRule extends AbstractAstVisitorRule {
    String name = 'AssignmentInConditional'
    int priority = 2
    Class astVisitorClass = AssignmentInConditionalAstVisitor
}

class AssignmentInConditionalAstVisitor extends AbstractAstVisitor {
    @Override
    void visitIfElse(IfStatement node) {
        addViolationIfAssignment(node.booleanExpression.expression)
        super.visitIfElse(node)
    }

    @Override
    void visitWhileLoop(WhileStatement node) {
        addViolationIfAssignment(node.booleanExpression.expression)
        super.visitWhileLoop(node)
    }

    @Override
    void visitTernaryExpression(TernaryExpression node) {
        addViolationIfAssignment(node.booleanExpression.expression)
        super.visitTernaryExpression(node)
    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression node) {
        addViolationIfAssignment(node.booleanExpression.expression)
        super.visitShortTernaryExpression(node)
    }

    private addViolationIfAssignment(node) {
        if (isFirstVisit(node) && node instanceof BinaryExpression) {
            if (node.operation.text == '=') {
                addViolation(node, 'Assignment used as conditional value, which always results in true. Use the == operator instead')
            }
            else {
                if (node.operation.text in ['&&', '||']) {
                    addViolationIfAssignment(node.leftExpression)
                    addViolationIfAssignment(node.rightExpression)
                }
            }
        }
    }
}
