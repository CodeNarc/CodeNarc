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
package org.codenarc.rule.junit

import org.codehaus.groovy.ast.MethodNode
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.util.AstUtil

/**
 * Rule that checks for a JUnit <code>tearDown()</code> method that only contains a call to
 * <code>super.tearDown()</code>.
 * <p/>
 * This rule sets the default value of <code>applyToFilesMatching</code> to only match source code file
 * paths ending in 'Test.groovy' or 'Tests.groovy'.
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
  */
class JUnitUnnecessaryTearDownRule extends AbstractAstVisitorRule {
    String name = 'JUnitUnnecessaryTearDown'
    int priority = 3
    Class astVisitorClass = JUnitUnnecessaryTearDownAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class JUnitUnnecessaryTearDownAstVisitor extends AbstractMethodVisitor {

    @Override
    void visitMethod(MethodNode methodNode) {
        if (JUnitUtil.isTearDownMethod(methodNode)) {
            def statements = methodNode.code.statements
            if (statements.size() == 1 && AstUtil.isMethodCall(statements[0], 'super', 'tearDown', 0)) {
                addViolation(methodNode, 'The tearDown() method contains no logic and can be removed')
            }
        }
    }
}
