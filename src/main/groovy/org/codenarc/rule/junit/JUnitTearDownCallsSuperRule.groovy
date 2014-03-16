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
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * Rule that checks that if the JUnit <code>tearDown()</code> method is defined, that it includes a call to
 * <code>super.tearDown()</code>.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class JUnitTearDownCallsSuperRule extends AbstractAstVisitorRule {
    String name = 'JUnitTearDownCallsSuper'
    int priority = 2
    Class astVisitorClass = JUnitTearDownCallsSuperAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitTearDownCallsSuperAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode methodNode) {
        if (JUnitUtil.isTearDownMethod(methodNode)) {
            def statements = methodNode.code.statements
            def found = statements.find { stmt ->
                AstUtil.isMethodCall(stmt, 'super', 'tearDown', 0)
            }
            if (!found) {
                addViolation(methodNode, 'The method tearDown() does not call super.tearDown()')
            }
        }
    }
}
