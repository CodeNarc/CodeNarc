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
package org.codenarc.rule.unnecessary

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * This rule finds usages of String.substring(int) and String.substring(int, int) that can be replaced by use of the subscript operator. For instance, var.substring(5) can be replaced with var[5..-1]. 
 *
 * @author Hamlet D'Arcy
 */
class UnnecessarySubstringRule extends AbstractAstVisitorRule {
    String name = 'UnnecessarySubstring'
    int priority = 3
    Class astVisitorClass = UnnecessarySubstringAstVisitor
}

class UnnecessarySubstringAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCall(call, '[^A-Z].*', 'substring', 1) && call.arguments instanceof ArgumentListExpression) {
            addViolation(call, 'The String.substring(int) method can be replaced with the subscript operator')
        } else if (AstUtil.isMethodCall(call, '[^A-Z].*', 'substring', 2) && call.arguments instanceof ArgumentListExpression) {
            addViolation(call, 'The String.substring(int, int) method can be replaced with the subscript operator')
        }
    }
}
