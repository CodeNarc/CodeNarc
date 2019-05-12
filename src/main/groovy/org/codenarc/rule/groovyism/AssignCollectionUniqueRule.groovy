/*
 * Copyright 2011 the original author or authors.
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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The unique() method mutates the original list. If a user is using the result of this method then they probably don't understand this.
 *
 * @author Nick Larson
 * @author Juan Vazquez
 * @author Jon DeJong
 * @author Chris Mair
 */
class AssignCollectionUniqueRule extends AbstractAstVisitorRule {

    String name = 'AssignCollectionUnique'
    int priority = 2
    Class astVisitorClass = AssignCollectionUniqueAstVisitor

}

class AssignCollectionUniqueAstVisitor extends AbstractAstVisitor {

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        Expression right = expression.rightExpression

        if (right instanceof MethodCallExpression) {
            if (isChainedUnique(right) || (isChainedUnique(right.objectExpression))) {
                addViolation(expression, 'unique() mutates the original list.')
            }
        }
        super.visitDeclarationExpression expression
    }

    private boolean isChainedUnique(Expression right) {
        if (isMatchingUniqueCall(right)) {
            if (right.objectExpression instanceof VariableExpression) {
                return true
            }
        }
        false
    }

    private boolean isMatchingUniqueCall(Expression right) {
        final String UNIQUE = 'unique'
        return AstUtil.isMethodCall(right, UNIQUE, 0) ||
                (AstUtil.isMethodCall(right, UNIQUE, 1) && right.arguments.expressions[0] instanceof ClosureExpression) ||
                (AstUtil.isMethodCall(right, UNIQUE, 1) && !isFalseConstant(right.arguments.expressions[0])) ||
                (AstUtil.isMethodCall(right, UNIQUE, 2) && !isFalseConstant(right.arguments.expressions[0]))
    }

    private boolean isFalseConstant(Expression expression) {
        return expression instanceof ConstantExpression && expression.falseExpression
    }

}
