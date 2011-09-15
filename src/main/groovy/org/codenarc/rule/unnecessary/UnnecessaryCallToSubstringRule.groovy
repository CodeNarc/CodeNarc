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

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil

/**
 * Calling String.substring(0) always returns the original string. This code is meaningless.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryCallToSubstringRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryCallToSubstring'
    int priority = 3
    Class astVisitorClass = UnnecessaryCallToSubstringAstVisitor
}

class UnnecessaryCallToSubstringAstVisitor extends AbstractMethodCallExpressionVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodNamed(call, 'substring', 1)) {
            def arg = AstUtil.getMethodArguments(call)[0]
            if (AstUtil.isConstant(arg, 0)) {
                addViolation(call, 'Invoking the String method substring(0) always returns the original value. Method possibly missing 2nd parameter')
            }
        }
    }
}
