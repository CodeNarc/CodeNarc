/*
 * Copyright 2010 the original author or authors.
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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * The overriding method merely calls the same method defined in a superclass.
 *
 * @author Sven Lange
 * @author Hamlet D'Arcy
  */
class UnnecessaryOverridingMethodRule extends AbstractAstVisitorRule {
    String name = 'UnnecessaryOverridingMethod'
    int priority = 3
    Class astVisitorClass = UnnecessaryOverridingMethodAstVisitor
}

class UnnecessaryOverridingMethodAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode node) {

        if (isSingleLineMethod(node) && node.code.statements[0]?.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCall = node.code.statements[0].expression
            if (AstUtil.isMethodCall(methodCall, 'super', node.name, node.parameters.length)) {
                if (AstUtil.getParameterNames(node) == AstUtil.getArgumentNames(methodCall)) {
                    addViolation node, "The method $node.name contains no logic and can be safely deleted"
                }
            }
        }
    }

    private static boolean isSingleLineMethod(MethodNode node) {
        if (node?.code instanceof BlockStatement) {
            if (node.code.statements?.size() == 1) {
                if (node.code.statements[0] instanceof ExpressionStatement) {
                    return true
                }
            }
        }
        false
    }

}
