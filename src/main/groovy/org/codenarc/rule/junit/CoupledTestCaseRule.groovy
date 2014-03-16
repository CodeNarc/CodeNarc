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

import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.util.AstUtil

/**
 * This rule finds test cases that are coupled to other test cases, either by invoking static methods on another test
 * case or by creating instances of another test case. If you require shared logic in test cases then extract that logic
 * to a new class where it can properly be reused. Static references to methods on the current test class are ignored.
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
  */
class CoupledTestCaseRule extends AbstractAstVisitorRule {
    String name = 'CoupledTestCase'
    int priority = 2
    Class astVisitorClass = CoupledTestCaseAstVisitor
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES
}

class CoupledTestCaseAstVisitor extends AbstractAstVisitor {

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {

        if (AstUtil.isMethodCall(call, '[A-Z].*Test', '.*') && !isMethodCallOnSameClass(call)) {
            addViolation(call, "$call.text invokes a method on another test case. Test cases should not be coupled. Move this method to a helper object")
        }
        super.visitMethodCallExpression(call)
    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression call) {

        if (call.type.name.matches('[A-Z].*Test')) {
            addViolation(call, "$call.text creates an instance of a test case. Test cases should not be coupled. Move this method to a helper object")
        }
        super.visitConstructorCallExpression(call)
    }

    private boolean isMethodCallOnSameClass(MethodCallExpression call) {
        AstUtil.isMethodCallOnObject(call, getCurrentClassName()) ||
            AstUtil.isMethodCallOnObject(call, getCurrentClassNode().nameWithoutPackage)
    }

}
