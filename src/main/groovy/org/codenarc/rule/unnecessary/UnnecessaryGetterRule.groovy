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
import org.codenarc.util.WildcardPattern

/**
 * Checks for explicit calls to getter/accessor methods which can, for the most part, be replaced by property access.
 * A getter is defined as a method call that matches get[A-Z] but not getClass() or get[A-Z][A-Z] such as getURL().
 * Getters do not take method arguments.
 * <p/>
 * If the <code>checkIsMethods</code> property is true, then also check isXxx() getters methods. Defaults to true.
 * <p/>
 * The <code>ignoreMethodNames</code> property optionally specifies one or more
 * (comma-separated) method names that should be ignored (i.e., that should not cause a
 * rule violation). The name(s) may optionally include wildcard characters ('*' or '?').
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class UnnecessaryGetterRule extends AbstractAstVisitorRule {

    String name = 'UnnecessaryGetter'
    int priority = 3
    Class astVisitorClass = UnnecessaryGetterAstVisitor
    String ignoreMethodNames
    boolean checkIsMethods = true

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
        if (!(call.method instanceof ConstantExpression)) {
            return false
        }

        String name = call.method?.value
        if (!['Mock', 'Spy', 'Stub'].contains(name)) {
            return false
        }

        int numArgs = AstUtil.getMethodArguments(call).size()

        return numArgs in [2, 3] && hasClosureAsLastArgument(numArgs, call)
    }

    private boolean hasClosureAsLastArgument(int numArgs, MethodCallExpression call) {
        return call.arguments[numArgs - 1] instanceof ClosureExpression
    }

    private void addViolationsIfGetter(MethodCallExpression call) {
        if (AstUtil.getMethodArguments(call).size() != 0) {
            return
        }
        if (!(call.method instanceof ConstantExpression)) {
            return
        }
        String name = call.method.value
        if (name == 'getClass' || name.length() < 3 || name == 'get') {
            return
        }

        if (new WildcardPattern(rule.ignoreMethodNames, false).matches(name)) {
            return
        }

        if (isMatchingGetterMethodName(name)) {
            String restOfName = name.startsWith('get') ? name[3..-1] : name[2..-1]

            if (restOfName.length() == 1) {
                def propertyName = restOfName[0].toLowerCase()
                addUnnecessaryGetterViolation(call, propertyName)
            } else if ((restOfName[1] as Character).isLowerCase()) {
                def propertyName = restOfName[0].toLowerCase() + restOfName[1..-1]
                addUnnecessaryGetterViolation(call, propertyName)
            } else {
                addUnnecessaryGetterViolation(call, restOfName)
            }
        }
    }

    private boolean isMatchingGetterMethodName(String name) {
        return (name.startsWith('get') && (name[3] as Character).isUpperCase()) ||
                (rule.checkIsMethods && name.startsWith('is') && (name[2] as Character).isUpperCase())
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
