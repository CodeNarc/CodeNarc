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
package org.codenarc.rule.basic

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * The overriding method merely calls the same method defined in a superclass
 *
 * @author Sven Lange
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class UselessOverridingMethodRule extends AbstractAstVisitorRule {
    String name = 'UselessOverridingMethod'
    int priority = 2
    Class astVisitorClass = UselessOverridingMethodAstVisitor
}

class UselessOverridingMethodAstVisitor extends AbstractAstVisitor {

    def void visitMethod(MethodNode node) {

        if (isSingleLineMethod(node) && node.code.statements[0]?.expression instanceof MethodCallExpression) {
            MethodCallExpression methodCall = node.code.statements[0].expression
            if (AstUtil.isMethodCall(methodCall, "super", node.name, node.parameters.length)) {
                if (AstUtil.getParameterNames(node) == AstUtil.getArgumentNames(methodCall)) {
                    addViolation node
                }
            }
        }
        super.visitMethod(node)
    }

    private static boolean isSingleLineMethod(MethodNode node) {
        if (node?.code instanceof BlockStatement) {
            if (node.code.statements?.size() == 1) {
                if (node.code.statements[0] instanceof ExpressionStatement) {
                    return true
                }
            }
        }
        return false
    }

}
