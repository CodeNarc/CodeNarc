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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * A Set literal is created with duplicate constant value. A set cannot contain two elements with the same value.
 *
 * @author Hamlet D'Arcy
 */
class DuplicateSetValueRule extends AbstractAstVisitorRule {
    String name = 'DuplicateSetValue'
    int priority = 2
    Class astVisitorClass = DuplicateSetValueAstVisitor
}

class DuplicateSetValueAstVisitor extends AbstractAstVisitor {
    @Override
    void visitCastExpression(CastExpression expression) {

        if (isSetLiteral(expression)) {
            expression.expression.expressions?.inject([]) { acc, value ->
                if (isDuplicate(value, acc)) {
                    addViolationForDuplicate(value)
                }
                acc
            }
        }

        super.visitCastExpression(expression)
    }

    private static boolean isDuplicate(ASTNode expression, previousValues) {
        if ((expression instanceof ConstantExpression)) {
            if (previousValues.contains(expression.value)) {
                return true
            }
            previousValues.add(expression.value)
        }
        false
    }

    private addViolationForDuplicate(ConstantExpression constant) {
        if (constant.value == null) {
            addViolation(constant, 'The constant value null is duplicated in the Set literal')
        } else if (constant.value instanceof String) {
            addViolation(constant, "The constant value '$constant.value' is duplicated in the Set literal")
        } else {
            addViolation(constant, "The constant value $constant.value is duplicated in the Set literal")
        }
    }

    private static boolean isSetLiteral(CastExpression expression) {
        return expression.type?.name?.endsWith('Set') && expression.expression instanceof ListExpression
    }

}
