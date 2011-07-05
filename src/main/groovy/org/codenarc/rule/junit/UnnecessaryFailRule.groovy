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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * In a unit test, catching an exception and immedietly calling Assert.fail() is pointless and hides the stack trace. It is better to rethrow the exception or not catch the exception at all.
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryFailRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryFail'
    int priority = 2
    Class astVisitorClass = UnnecessaryFailAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class UnnecessaryFailAstVisitor extends AbstractAstVisitor {

    @Override
    void visitCatchStatement(CatchStatement statement) {
        if (statement.code instanceof BlockStatement && statement.code.statements) {
            statement.code.statements.each {
                addViolationIfFail(it)
            }
        }
        super.visitCatchStatement(statement)
    }

    private addViolationIfFail(ASTNode it) {
        if (it instanceof ExpressionStatement && it.expression instanceof MethodCallExpression) {
            if (AstUtil.isMethodCall(it.expression, ['this', 'Assert'], ['fail'], 0)) {
                addViolation it.expression, 'Catching an exception and failing will hide the stack trace. It is better to rethrow the exception'
            }
        }
    }
}
