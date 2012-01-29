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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * Violations are triggered when an excessive set of consecutive statements all reference the same variable. This can be made more readable by using a with or identity block. 
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryObjectReferencesRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryObjectReferences'
    int priority = 3
    int maxReferencesAllowed = 5
    Class astVisitorClass = UnnecessaryObjectReferencesAstVisitor
}

class UnnecessaryObjectReferencesAstVisitor extends AbstractAstVisitor {

    private final runCollector = [variable: null, count: 0, clear: { this.variable = null; this.count = 0 }]

    @Override
    void visitBlockStatement(BlockStatement block) {

        // search for runs of methods or runs of properties
        block.statements.each { Statement statement ->
            if (!(statement instanceof ExpressionStatement)) {
                runCollector.clear()
                return
            }

            def exp = statement.expression

            if (exp instanceof MethodCallExpression && exp.objectExpression instanceof VariableExpression) {
                accumulateOrError(exp.objectExpression.variable, statement)
            } else if (exp instanceof BinaryExpression && exp.leftExpression instanceof PropertyExpression
                    && exp.leftExpression.objectExpression instanceof VariableExpression ) {
                accumulateOrError(exp.leftExpression.objectExpression.variable, statement)
            } else {
                runCollector.clear()
            }
        }

        runCollector.clear()
        super.visitBlockStatement block
    }

    private void accumulateOrError(String variable, ExpressionStatement statement) {
        if (variable == runCollector.variable)  {
            if (runCollector.count == rule.maxReferencesAllowed) {
                addViolation statement.expression, 'The code could be more concise by using a with() or identity() block'
            } else {
                runCollector.count = runCollector.count + 1
            }
        } else if (variable != 'this') {
            runCollector.variable = variable
            runCollector.count = 1
        }
    }
}
