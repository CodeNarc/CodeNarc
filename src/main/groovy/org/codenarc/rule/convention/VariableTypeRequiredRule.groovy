/*
 * Copyright 2018 the original author or authors.
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

import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern

/**
 * Checks that variable types are explicitly specified in declarations (and not using def)
 *
 * @author Chris Mair
 */
class VariableTypeRequiredRule extends AbstractAstVisitorRule {

    String name = 'VariableTypeRequired'
    int priority = 3
    Class astVisitorClass = VariableTypeRequiredAstVisitor
    String ignoreVariableNames
}

class VariableTypeRequiredAstVisitor extends AbstractAstVisitor {

    @Override
    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        List<Expression> varExpressions = AstUtil.getVariableExpressions(declarationExpression)
        varExpressions.each { varExpression ->
            if (varExpression.isDynamicTyped() && !matchesIgnoredName(varExpression)) {
                addViolation(varExpression, $/The type is not specified for variable "$varExpression.name"/$)
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

    private boolean matchesIgnoredName(Expression varExpression) {
        return new WildcardPattern(rule.ignoreVariableNames, false).matches(varExpression.name)
    }

}
