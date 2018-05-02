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

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for explicit calls to getter/accessor methods which can, for the most part, be replaced by property access.
 * A getter is defined as a method call that matches get[A-Z] but not getClass() or get[A-Z][A-Z] such as getURL().
 * Getters do not take method arguments.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class UnnecessaryGetterRule extends AbstractAstVisitorRule {

    String name = 'UnnecessaryGetter'
    int priority = 3
    Class astVisitorClass = UnnecessaryGetterAstVisitor

}

class UnnecessaryGetterAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (isFirstVisit(call)) {
            addViolationsIfGetter(call)
        }
        if (!isSpockMethod(call)) {
            super.visitMethodCallExpression(call)
        }
    }

    private boolean isSpockMethod(MethodCallExpression call) {
        if (AstUtil.getMethodArguments(call).size() != 2) {
            return false
        }
        if (!(call.method instanceof ConstantExpression)) {
            return false
        }
        if (!(call.arguments[1] instanceof ClosureExpression)) {
            return false
        }
        String name = call.method.value
        return name in ['Mock', 'Stub']
    }

    private void addViolationsIfGetter(MethodCallExpression call) {
        if (AstUtil.getMethodArguments(call).size() != 0) {
            return
        }
        if (!(call.method instanceof ConstantExpression)) {
            return
        }
        String name = call.method.value
        if (name == 'getClass' || name.length() < 4) {
            return
        }

        if (name[0..2] == 'get' && (name[3] as Character).isUpperCase()) {
            if (name.length() == 4) {
                def propertyName = name[3].toLowerCase()
                addUnnecessaryGetterViolation(call, propertyName)
            } else if ((name[4] as Character).isLowerCase()) {
                def propertyName = name[3].toLowerCase() + name[4..-1]
                addUnnecessaryGetterViolation(call, propertyName)
            } else {
                def propertyName = name[3..-1]
                addUnnecessaryGetterViolation(call, propertyName)
            }
        }
    }

    private void addUnnecessaryGetterViolation(MethodCallExpression call, String propertyName) {
        // Only add if there is not already a field with that name
        def fieldNames = currentClassNode.fields.name
        if (!fieldNames.contains(propertyName)) {
            String name = call.method.value
            addViolation call, "$name() can probably be rewritten as $propertyName"
        }
    }

}
