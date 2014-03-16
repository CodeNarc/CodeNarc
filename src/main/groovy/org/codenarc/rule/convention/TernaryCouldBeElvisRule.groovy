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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.*

/**
 * Rule that checks for ternary expressions where the boolean and true expressions are the same.
 * These can be simplified to an Elvis expression.
 *
 * @author Chris Mair
 */
class TernaryCouldBeElvisRule extends AbstractAstVisitorRule {

    String name = 'TernaryCouldBeElvis'
    int priority = 3
    Class astVisitorClass = TernaryCouldBeElvisAstVisitor
}

class TernaryCouldBeElvisAstVisitor extends AbstractAstVisitor {

    @Override
    void visitTernaryExpression(TernaryExpression expression) {
        if (isNotElvis(expression) && areTheSame(expression.booleanExpression.expression, expression.trueExpression)) {
            def asElvis = "${expression.trueExpression.text} ?: ${expression.falseExpression.text}"
            addViolation(expression, "${expression.text} in class $currentClassName can be simplified to $asElvis")
        }
        super.visitTernaryExpression(expression)
    }

    private boolean isNotElvis(TernaryExpression expression) {
        return !(expression instanceof ElvisOperatorExpression)
    }

    private boolean areTheSame(Expression booleanExpression, Expression trueExpression) {
        if (booleanExpression.class != trueExpression.class) {
            return false
        }
        if (booleanExpression instanceof VariableExpression) {
            return booleanExpression.name == trueExpression.name
        }
        if (booleanExpression instanceof MethodCallExpression) {
            return booleanExpression.text == trueExpression.text
        }
        return false
    }
}
