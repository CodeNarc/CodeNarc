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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression

/**
 * This rule catches calling the method removeAll with yourself as a parameter.
 *
 * @author Hamlet D'Arcy
 * @version $Revision$ - $Date$
 */
class RemoveAllOnSelfRule extends AbstractAstVisitorRule {
    String name = 'RemoveAllOnSelf'
    int priority = 2
    Class astVisitorClass = RemoveAllOnSelfAstVisitor
}

class RemoveAllOnSelfAstVisitor extends AbstractAstVisitor {

    def void visitMethodCallExpression(MethodCallExpression call) {

        if (isMethodNamed(call, "removeAll") && getArity(call) == 1) {
            String variableName = call.objectExpression.text
            def argumentName = call.arguments.expressions[0].text
            if (argumentName == variableName) {
                addViolation call
            }
        }
        super.visitMethodCallExpression(call)
    }

    private static boolean isMethodNamed(MethodCallExpression call, target) {
        call?.method?.text == target
    }

    private static int getArity(MethodCallExpression call) {
        if (call?.arguments instanceof TupleExpression) {
            return call?.arguments?.expressions?.size()
        }
        return 0
    }
}
