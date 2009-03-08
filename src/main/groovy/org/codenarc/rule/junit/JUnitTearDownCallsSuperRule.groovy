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

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * Rule that checks that if the JUnit <code>tearDown()</code> method is defined, that it includes a call to
 * <code>super.tearDown()</code>.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 * @version $Revision: 69 $ - $Date: 2009-02-25 22:03:41 -0500 (Wed, 25 Feb 2009) $
 */
class JUnitTearDownCallsSuperRule extends AbstractAstVisitorRule {
    String name = 'JUnitTearDownCallsSuper'
    int priority = 2
    Class astVisitorClass = JUnitTearDownCallsSuperAstVisitor
    String applyToFilesMatching = DEFAULT_TEST_FILES
}

class JUnitTearDownCallsSuperAstVisitor extends AbstractAstVisitor  {
    void visitMethod(MethodNode methodNode) {
        if (methodNode.name == 'tearDown' &&
                methodNode.parameters.size() == 0 &&
                methodNode.code instanceof BlockStatement) {
            def statements = methodNode.code.statements
            def found = statements.find { stmt ->
                return AstUtil.isMethodCall(stmt, 'super', 'tearDown', 0)
            }
            if (!found) {
                addViolation(methodNode)
            }
        }
        super.visitMethod(methodNode)
    }

}