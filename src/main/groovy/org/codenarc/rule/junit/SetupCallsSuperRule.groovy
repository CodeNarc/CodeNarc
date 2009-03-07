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

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codenarc.util.AstUtil

/**
 * Rule that verifies that the name of each method matches a regular expression. By default it checks that the
 * method name starts with a lowercase letter. Implicit method names are ignored (i.e., 'main' and 'run'
 * methods automatically created for Groovy scripts).
 * <p/>
 * The <code>regex</code> property specifies the regular expression to check the method name against. It is
 * required and cannot be null or empty. It defaults to '[a-z]\w*'.
 *
 * @author Chris Mair
 * @version $Revision: 69 $ - $Date: 2009-02-25 22:03:41 -0500 (Wed, 25 Feb 2009) $
 */
class SetupCallsSuperRule extends AbstractAstVisitorRule {
    String name = 'SetupCallsSuper'
    int priority = 2
    Class astVisitorClass = SetupCallsSuperAstVisitor
}

class SetupCallsSuperAstVisitor extends AbstractAstVisitor  {
    void visitMethod(MethodNode methodNode) {
        println "visitMethod name=${methodNode.name}"
        println "code=$methodNode.code"
        if (methodNode.name == 'setUp' && methodNode.code instanceof BlockStatement) {
            def statements = methodNode.code.statements
            def found = statements.find { stmt ->
                return AstUtil.isMethodCall(stmt, 'super', 'setUp', 0)
            }
            if (!found) {
                addViolation(methodNode)
            }
        }
        super.visitMethod(methodNode)
    }

}