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

import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodCallExpressionVisitor
import org.codenarc.util.AstUtil
import org.codenarc.util.ConsecutiveUtils

/**
 * Violations occur when method calls to append(Object) are chained together with literals as parameters. The chained calls can be joined into one invocation.
 *
 * @author 'Hamlet D'Arcy'
 */
class ConsecutiveLiteralAppendsRule extends AbstractAstVisitorRule {
    String name = 'ConsecutiveLiteralAppends'
    int priority = 2
    Class astVisitorClass = ConsecutiveLiteralAppendsAstVisitor
}

class ConsecutiveLiteralAppendsAstVisitor extends AbstractMethodCallExpressionVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (isChainedAppend(call)) {
            def arg1 = AstUtil.getMethodArguments(call.objectExpression)[0]
            def arg2 = AstUtil.getMethodArguments(call)[0]
            if (ConsecutiveUtils.areJoinableConstants(arg1, arg2) && !AstUtil.isNull(arg1) && !AstUtil.isNull(arg2)) {
                if (arg1 instanceof GStringExpression ||  arg2 instanceof GStringExpression) {
                    addViolation(call, 'Consecutive calls to append method with literal parameters can be joined into one append() call')
                } else {
                    addViolation(call, "Consecutive calls to append method with literal parameters can be joined into append('${arg1.value}${arg2.value}')")
                }
            }
        }
    }

    static private boolean isChainedAppend(MethodCallExpression call) {
        if (AstUtil.isMethodNamed(call, 'append', 1)) {
            if (call.objectExpression instanceof MethodCallExpression
                    && AstUtil.isMethodNamed(call.objectExpression, 'append', 1)) {
                return true
            }
        }
        false
    }

}
