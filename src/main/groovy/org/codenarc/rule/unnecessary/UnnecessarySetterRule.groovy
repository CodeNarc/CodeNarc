/*
 * Copyright 2017 the original author or authors.
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

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Checks for explicit calls to setter methods which can, for the most part, be replaced by assignment to property.
 * A setter is defined as a method call that matches set[A-Z] but not set[A-Z][A-Z] such as setURL().
 * Setters take one method argument. Setter calls within an expression are ignored.
 */
class UnnecessarySetterRule extends AbstractAstVisitorRule {
    String name = 'UnnecessarySetter'
    int priority = 3
    Class astVisitorClass = UnnecessarySetterAstVisitor
}

class UnnecessarySetterAstVisitor extends AbstractAstVisitor {

    @Override
    void visitExpressionStatement(ExpressionStatement statement) {
        if (statement.expression instanceof MethodCallExpression) {
            addViolationsIfSetter(statement.expression)
        }
        super.visitExpressionStatement(statement)
    }

    private void addViolationsIfSetter(MethodCallExpression call) {
        if (AstUtil.getMethodArguments(call).size() != 1) {
            return
        }

        if (!(call.method instanceof ConstantExpression)) {
            return
        }

        if (isSuperCall(call)) {
            return
        }

        String name = call.method.value
        if (name.length() > 3
                && name[0..2] == 'set'
                && (name[3] as Character).isUpperCase()
                && (name.length() == 4 || name[4..-1] != name[4..-1].toUpperCase()) ) {
            // TODO Restore once CodeNarc upgrades to Groovy 2.4
            // def propertyName = name[3..-1].uncapitalize()
            // def assignment = AstUtil.getNodeText(call.arguments, sourceCode)

            def propertyName = name[3].toLowerCase() + name[4..-1] //name[3..-1].uncapitalize()
            addUnnecessarySetterViolation(call, propertyName)
        }
    }

    private boolean isSuperCall(MethodCallExpression call) {
        def objectExpression = call.objectExpression
        objectExpression instanceof VariableExpression && objectExpression.superExpression
    }

    private void addUnnecessarySetterViolation(MethodCallExpression call, String propertyName) {
        // Only add if there is not already a field with that name
        def fieldNames = currentClassNode.fields.name
        if (!fieldNames.contains(propertyName)) {
            String name = call.method.value
            addViolation call, "$name(..) can probably be rewritten as $propertyName = .."

            // TODO Restore once CodeNarc upgrades to Groovy 2.4
            //addViolation call, "$name($assignment) can probably be rewritten as $propertyName = $assignment"
        }
    }
}
