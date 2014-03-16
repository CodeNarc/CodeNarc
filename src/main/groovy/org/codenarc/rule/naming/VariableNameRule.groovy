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
package org.codenarc.rule.naming

import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil
import org.codenarc.util.WildcardPattern

/**
 * Rule that verifies that the name of each variable matches a regular expression. By default it checks that
 * non-<code>final</code> variable names start with a lowercase letter and contains only letters or numbers.
 * By default, <code>final</code> variable names start with an uppercase letter and contain only uppercase
 * letters, numbers and underscores.
 * <p/>
 * The <code>regex</code> property specifies the default regular expression to validate a variable name.
 * It is required and cannot be null or empty. It defaults to '[a-z][a-zA-Z0-9]*'.
 * <p/>
 * The <code>finalRegex</code> property specifies the regular expression to validate <code>final</code>
 * variable names. It is optional but defaults to '[A-Z][A-Z0-9_]*'. If not set, then <code>regex</code> is
 * used to validate <code>final</code> variables.  
 * <p/>
 * The <code>ignoreVariableNames</code> property optionally specifies one or more
 * (comma-separated) variable names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Chris Mair
  */
class VariableNameRule extends AbstractAstVisitorRule {
    String name = 'VariableName'
    int priority = 2
    String regex = DEFAULT_VAR_NAME
    String finalRegex = DEFAULT_CONST_NAME
    String ignoreVariableNames
    Class astVisitorClass = VariableNameAstVisitor
}

class VariableNameAstVisitor extends AbstractAstVisitor  {

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        assert rule.regex
        if (isFirstVisit(declarationExpression)) {
            def varExpressions = AstUtil.getVariableExpressions(declarationExpression)
            def re = rule.finalRegex && AstUtil.isFinalVariable(declarationExpression, sourceCode) ?
                rule.finalRegex : rule.regex

            varExpressions.each { varExpression ->

                if (!new WildcardPattern(rule.ignoreVariableNames, false).matches(varExpression.name) &&
                        !(varExpression.name ==~ re)) {
                    def msg = "Variable named $varExpression.name in class $currentClassName does not match the pattern ${re.toString()}"
                    addViolation(declarationExpression, msg)
                }
            }
        }
        super.visitDeclarationExpression(declarationExpression)
    }

}
