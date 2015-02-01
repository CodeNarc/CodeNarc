/*
 * Copyright 2014 the original author or authors.
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

import org.codehaus.groovy.ast.expr.CastExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Checks for unnecessary cast operations
 *
 * @author Chris Mair
 */
class UnnecessaryCastRule extends AbstractAstVisitorRule {

    String name = 'UnnecessaryCast'
    int priority = 2
    Class astVisitorClass = UnnecessaryCastAstVisitor
}

class UnnecessaryCastAstVisitor extends AbstractAstVisitor {

    // Map of Cast type -> Cast expression type (both as Strings)
    private static final CAST_TYPE_MAP = [
        int:'java.lang.Integer',
        long:'java.lang.Long',
        BigDecimal:'java.math.BigDecimal',
        String:'java.lang.String',
        List:'java.util.List',
        Map:'java.util.Map',
    ]

    @Override
    void visitCastExpression(CastExpression expression) {
        if (isUnnecessaryCast(expression)) {
            addViolation(expression, "The cast ${expression.text} in class $currentClassName is unnecessary")
        }
        super.visitCastExpression(expression)
    }

    private boolean isUnnecessaryCast(CastExpression expression) {
        CAST_TYPE_MAP[expression.type.name] == expression.expression.type.name ||
            expression.type.name == expression.expression.type.name
    }
}
