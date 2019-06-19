/*
 * Copyright 2019 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.Rule
import org.codenarc.rule.Violation
import org.codenarc.source.SourceCode

/**
 * Checks that the implicit it closure parameter is not used and that parameters are declared explicitly instead
 *
 * @author Marcin Erdmann
 */
class ImplicitClosureParameterRule extends AbstractAstVisitorRule {

    String name = 'ImplicitClosureParameter'
    int priority = 3
    boolean allowUsingItAsParameterName = false
    Class astVisitorClass = ImplicitClosureParameterAstVisitor
}

class ImplicitClosureParameterAstVisitor extends AbstractAstVisitor {
    private final ImplicitClosureParameterCodeVisitor closureCodeVisitor = new ImplicitClosureParameterCodeVisitor()

    @Override
    void setRule(Rule rule) {
        super.setRule(rule)
        closureCodeVisitor.rule = rule
    }

    @Override
    void setSourceCode(SourceCode sourceCode) {
        super.setSourceCode(sourceCode)
        closureCodeVisitor.sourceCode = sourceCode
    }

    @Override
    List<Violation> getViolations() {
        super.getViolations() + closureCodeVisitor.violations
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        if (expression.parameters != null) {
            if (expression.parameters.size() == 0) {
                expression.code.visit(closureCodeVisitor)
            } else if (!rule.allowUsingItAsParameterName && hasItParameter(expression)) {
                addViolation(expression, 'By convention "it" should not be used as a closure parameter name.')
            }
        }
        super.visitClosureExpression(expression)
    }

    private boolean hasItParameter(ClosureExpression closureExpression) {
        closureExpression.parameters.any { it.name == 'it' }
    }
}

class ImplicitClosureParameterCodeVisitor extends AbstractAstVisitor {

    @Override
    void visitVariableExpression(VariableExpression expression) {
        if (expression.name == 'it') {
            addViolation(expression, 'By convention closure parameters should be specified explicitly.')
        }
        super.visitVariableExpression(expression)
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        //do not visit nested closures
    }
}
