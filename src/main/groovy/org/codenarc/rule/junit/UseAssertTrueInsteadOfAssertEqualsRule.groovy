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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule detects JUnit calling assertEquals where the first parameter is a boolean. These assertions
 * should be made by more specific methods, like assertTrue or assertFalse.
 *
 * If the checkAssertStatements property is true, then it also checks for assert statements, e.g. assert x == true.
 *
 * @author Hamlet D'Arcy
  */
class UseAssertTrueInsteadOfAssertEqualsRule extends AbstractAstVisitorRule {
    String name = 'UseAssertTrueInsteadOfAssertEquals'
    int priority = 3
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
    boolean checkAssertStatements = false
    Class astVisitorClass = UseAssertTrueInsteadOfAssertEqualsAstVisitor
}

class UseAssertTrueInsteadOfAssertEqualsAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        List args = AstUtil.getMethodArguments(call)
        if (AstUtil.isMethodCall(call, 'this', 'assertEquals')) {

            if (args.size() == 2 && (AstUtil.isBoolean(args[0]) || AstUtil.isBoolean(args[1]))) {
                addViolation call, 'assertEquals can be simplified using assertTrue or assertFalse'
            } else if (args.size() == 3 && (AstUtil.isBoolean(args[1]) || AstUtil.isBoolean(args[2]))) {
                addViolation call, 'assertEquals can be simplified using assertTrue or assertFalse'
            }
        }
        super.visitMethodCallExpression call
    }

    @Override
    void visitAssertStatement(AssertStatement statement) {
        if (rule.checkAssertStatements && AstUtil.isBinaryExpressionType(statement.booleanExpression.expression, '==')) {
            BinaryExpression exp = statement.booleanExpression.expression
            if (AstUtil.isTrue(exp.leftExpression)) {
                addViolation(statement, "The expression '$exp.text' can be simplified to '$exp.rightExpression.text'")
            } else if (AstUtil.isTrue(exp.rightExpression)) {
                addViolation(statement, "The expression '$exp.text' can be simplified to '$exp.leftExpression.text'")
            } else if (AstUtil.isFalse(exp.leftExpression)) {
                addViolation(statement, "The expression '$exp.text' can be simplified to '!$exp.rightExpression.text'")
            } else if (AstUtil.isFalse(exp.rightExpression)) {
                addViolation(statement, "The expression '$exp.text' can be simplified to '!$exp.leftExpression.text'")
            }

        }
        super.visitAssertStatement(statement)
    }
}
