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
package org.codenarc.rule.convention

import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule

import java.util.regex.Pattern

/**
 * Def keyword is overused and should be replaced with specific type.
 * <p/>
 * The <code>excludeRegex</code> property optionally specifies regex
 * to find text which could occur immediately after def.
 *
 * @author Dominik Przybysz
 * @author Chris Mair
 */
class NoDefRule extends AbstractAstVisitorRule {

    public static final String MESSAGE = 'def for declaration should not be used'
    public static final String MESSAGE_DEF_RETURN = 'def for method return type should not be used'
    public static final String MESSAGE_DEF_PARAMETER = 'def for method parameter type should not be used'
    public static final String MESSAGE_DEF_FIELD = 'def should not be used for field type'

    String name = 'NoDef'
    int priority = 3
    protected Pattern excludePattern

    Class astVisitorClass = NoDefAstVisitor

    void setExcludeRegex(String excludeRegex) {
        this.excludePattern = excludeRegex ? ~/$excludeRegex/ : null
    }
}

class NoDefAstVisitor extends AbstractAstVisitor {

    private static final Pattern CONTAINS_WHITESPACE_PATTERN = ~/.*\s+.*/

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        def leftExpression = expression.getLeftExpression()
        if (leftExpression instanceof ArgumentListExpression) {
            boolean hasNonExcluded = leftExpression.expressions.find { expr ->  dynamicTypedAndNotExcludedVariable(expr) }
            if (hasNonExcluded) {
                addViolation(expression, NoDefRule.MESSAGE)
            }
        }
        else {
            if (dynamicTypedAndNotExcludedVariable(expression.variableExpression)) {
                addViolation(leftExpression, NoDefRule.MESSAGE)
            }
        }

        super.visitDeclarationExpression(expression)
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        if (node.isDynamicReturnType() && !methodExcluded(node.name)) {
            addViolation(node, NoDefRule.MESSAGE_DEF_RETURN)
        }

        visitParameters(node.getParameters())

        super.visitConstructorOrMethod(node, isConstructor)
    }

    @Override
    void visitField(FieldNode node) {
        if (node.isDynamicTyped() && !nameExcluded(node.name)) {
            addViolation(node, NoDefRule.MESSAGE_DEF_FIELD)
        }
        super.visitField(node)
    }

    private void visitParameters(Parameter[] parameters) {
        if (parameters) {
            parameters.each { Parameter param ->
                if (dynamicTypedAndNotExcludedVariable(param)) {
                    addViolation(param, NoDefRule.MESSAGE_DEF_PARAMETER)
                }
            }
        }
    }

    private boolean methodExcluded(String text) {
        return matches(CONTAINS_WHITESPACE_PATTERN, text) || nameExcluded(text)
    }

    private boolean dynamicTypedAndNotExcludedVariable(Variable variableExpression) {
        return variableExpression.isDynamicTyped() && !nameExcluded(variableExpression.name)
    }

    private boolean nameExcluded(String text) {
        matches(rule.excludePattern, text)
    }

    private boolean matches(Pattern pattern, String text) {
        pattern && pattern.matcher(text).matches()
    }
}
