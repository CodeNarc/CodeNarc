/*
 * Copyright 2013 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor

/**
 * Check for regular (single quote) strings containing a GString-type expression (${..}).
 *
 * @author Chris Mair
 */
class GStringExpressionWithinStringRule extends AbstractAstVisitorRule {
    String name = 'GStringExpressionWithinString'
    int priority = 2
    Class astVisitorClass = GStringExpressionWithinStringAstVisitor
}

class GStringExpressionWithinStringAstVisitor extends AbstractAstVisitor {

    private static final GSTRING_EXPRESSION_REGEX = /\$\{.*\}/

    @Override
    void visitConstantExpression(ConstantExpression expression) {
        if (isFirstVisit(expression) && expression.value instanceof String && expression.lineNumber > -1) {
            def matcher = expression.value =~ GSTRING_EXPRESSION_REGEX
            if (matcher) {
                addViolation(expression, "The String '$expression.value' contains a GString-type expression: '${matcher[0]}'")
            }
        }
        super.visitConstantExpression(expression)
    }

}
