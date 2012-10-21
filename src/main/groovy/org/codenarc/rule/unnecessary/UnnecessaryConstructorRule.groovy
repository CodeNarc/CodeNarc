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

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

import java.lang.reflect.Modifier

/**
 * UnnecessaryConstructor
 *
 * @author Tomasz Bujok
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class UnnecessaryConstructorRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryConstructor'
    int priority = 3
    Class astVisitorClass = UnnecessaryConstructorAstVisitor
}

class UnnecessaryConstructorAstVisitor extends AbstractAstVisitor {

    void visitClassEx(ClassNode node) {
        if (node.declaredConstructors?.size() == 1) {
            analyzeConstructor node.declaredConstructors[0]
        }
        super.visitClassEx(node)
    }

    private void analyzeConstructor(ConstructorNode node) {
        if(isEmptyOrJustCallsSuper(node) && !Modifier.isPrivate(node.modifiers) && node.parameters?.size() == 0) {
             addViolation node, 'The constructor can be safely deleted'
        }
    }

    private boolean isEmptyOrJustCallsSuper(ConstructorNode node) {
        isEmptyConstructor(node) || containsOnlyCallToSuper(node)
    }

    private boolean isEmptyConstructor(ConstructorNode node) {
        node.code?.isEmpty()
    }

    private boolean containsOnlyCallToSuper(ConstructorNode node) {
        if (!AstUtil.isOneLiner(node.code)) {
            return false
        }
        def onlyStatement = node.code.statements[0]

        return onlyStatement instanceof ExpressionStatement &&
            onlyStatement.expression instanceof ConstructorCallExpression &&
            onlyStatement.expression.superCall &&
            onlyStatement.expression.arguments.expressions.empty
    }
}
