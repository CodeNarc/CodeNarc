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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * In many case collectMany() yields the same result as collect{}.flatten. It is easier to
 * understand and more clearly transports the purpose.
 *
 * @author Joachim Baumann
 */
class UseCollectManyRule extends AbstractAstVisitorRule {

    protected static final String MESSAGE = 'collect{}.flatten() can be collectMany{}'

    String name = 'UseCollectMany'
    int priority = 2
    Class astVisitorClass = UseCollectManyAstVisitor
}

class UseCollectManyAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        /*
        The structure of the AST for this violation is as follows:
        MethodCallExpression name flatten(), no arguments
        - objectExpression contains as first element
          - MethodCallExpression name collect(), one argument (closure)
        Nota Bene: This does not detect assignment to a variable and subsequent call to flatten()
         */

        if(AstUtil.isMethodCall(call, 'flatten', 0)) {
            // found flatten, now we try to find the previous collect()-call
            Expression expression = call.objectExpression
            if(AstUtil.isMethodCall(expression, 'collect', 1)) {
                addViolation(call, UseCollectManyRule.MESSAGE)
            }
        }

        super.visitMethodCallExpression(call)
    }

}
