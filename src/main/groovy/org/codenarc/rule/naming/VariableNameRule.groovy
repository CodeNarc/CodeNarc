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
 *
 * @author Chris Mair
 * @version $Revision: 37 $ - $Date: 2009-02-06 21:31:05 -0500 (Fri, 06 Feb 2009) $
 */
class VariableNameRule extends AbstractAstVisitorRule {
    String name = 'VariableName'
    int priority = 2
    String regex = DEFAULT_VAR_NAME
    String finalRegex = DEFAULT_CONST_NAME

    Class astVisitorClass = VariableNameAstVisitor
}

class VariableNameAstVisitor extends AbstractAstVisitor  {
    private visitedDeclarations = new HashSet()

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        assert rule.regex
        def variableExpression = declarationExpression.variableExpression
        def re = rule.finalRegex && isFinal(declarationExpression) ? rule.finalRegex : rule.regex

        def alreadyVisited = visitedDeclarations.contains(declarationExpression)
        if (declarationExpression.lineNumber >= 0 && !alreadyVisited && !(variableExpression.name ==~ re)) {
            addViolation(declarationExpression)
            visitedDeclarations << declarationExpression
        }

        super.visitDeclarationExpression(declarationExpression)
    }

    /**
     * NOTE: THIS IS A WORKAROUND.
     * There does not seem to be an easy way to determine whether the 'final' modifier has been
     * specified for a variable declaration. Return true if the 'final' is present before the variable name.
     */
    private boolean isFinal(declarationExpression) {
        def variableName = declarationExpression.variableExpression.name
        def expressionSource = expressionSource(declarationExpression)
        // The 'final' modifier .. variable name .. either an '=' or end of string
        def m = expressionSource =~ /final\s+.*/ + variableName + /\s*($|\=)/
        return m.find()
    }

    private String expressionSource(node) {
        // TODO Narrow this down a bit to just the declaration; but sometimes had issues with lastColumnNumber for that node
        def sourceLine = sourceCode.lines[node.lineNumber-1]
        return sourceLine
    }

}