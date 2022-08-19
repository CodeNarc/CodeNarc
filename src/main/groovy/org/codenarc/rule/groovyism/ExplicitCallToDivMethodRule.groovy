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
package org.codenarc.rule.groovyism

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codenarc.rule.AbstractAstVisitorRule

/**
 * This rule detects when the div(Object) method is called directly in code instead of using the / operator. A groovier way to express this: a.div(b) is this: a / b
 *
 * @author Hamlet D'Arcy
 */
class ExplicitCallToDivMethodRule extends AbstractAstVisitorRule {
    String name = 'ExplicitCallToDivMethod'
    int priority = 2
    Class astVisitorClass = ExplicitCallToDivMethodAstVisitor
    boolean ignoreThisReference = false
}

class ExplicitCallToDivMethodAstVisitor extends ExplicitCallToMethodAstVisitor {
    ExplicitCallToDivMethodAstVisitor() {
        super('div')
    }

    @Override
    protected String getViolationMessage(MethodCallExpression exp) {
        "Explicit call to ${exp.text} method can be rewritten as ${exp.objectExpression.text} / ${exp.arguments.text}"
    }

    @Override
    protected boolean shouldIgnoreViolationForMethodCall(MethodCallExpression call) {
        // We know the method call has exactly one argument
        // Ignore if argument is a Map (named args), Closure or a String
        def arg1 = call.arguments.expressions[0]
        return arg1 instanceof NamedArgumentListExpression ||
                arg1 instanceof ClosureExpression ||
                (arg1 instanceof ConstantExpression && arg1.value instanceof String)
    }
}
