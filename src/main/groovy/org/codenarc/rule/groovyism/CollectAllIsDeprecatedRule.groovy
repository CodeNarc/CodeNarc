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

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * collectAll is deprecated since Groovy 1.8.1. Use collectNested instead
 *
 * @author Joachim Baumann
 */
class CollectAllIsDeprecatedRule extends AbstractAstVisitorRule {

    protected static final String MESSAGE = 'collectAll{} is deprecated since Groovy 1.8.1. Use collectNested instead{}.'

    String name = 'CollectAllIsDeprecated'
    int priority = 2
    Class astVisitorClass = CollectAllIsDeprecatedAstVisitor

}

class CollectAllIsDeprecatedAstVisitor extends AbstractAstVisitor {
    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        /*
        The structure of the AST for this violation is as follows:
        MethodCallExpression name collectAll(), one or two parameterStack
         */
        if(AstUtil.isMethodCall(call, 'collectAll', 1..2)) {
            addViolation(call, CollectAllIsDeprecatedRule.MESSAGE)
        }

        super.visitMethodCallExpression(call)
    }
}
