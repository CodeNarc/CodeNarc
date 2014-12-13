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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codenarc.rule.AbstractMethodVisitor
import org.codenarc.rule.AstVisitor
import org.codenarc.util.AstUtil

/**
 * This rule searches for test methods that do not contain assert statements. Either the test method is missing assert
 * statements, which is an error, or the test method contains custom assert statements that do not follow a proper
 * assert naming convention. Test methods are defined as public void methods that begin with the work test or have a @Test
 * annotation. By default this rule applies to the default test class names, but this can be changed using the rule's
 * applyToClassNames property. 
 *
 * @author Hamlet D'Arcy
  */
class JUnitTestMethodWithoutAssertRule extends AbstractAstVisitorRule {

    String name = 'JUnitTestMethodWithoutAssert'
    int priority = 2
    String assertMethodPatterns = 'assert.*,should.*,fail.*,verify.*,expect.*'
    String applyToClassNames = DEFAULT_TEST_CLASS_NAMES

    @Override
    AstVisitor getAstVisitor() {
        def strings = assertMethodPatterns ? assertMethodPatterns.tokenize(',') : []
        new JUnitTestMethodWithoutAssertAstVisitor(assertMethodPatterns: strings as Set)
    }
}

class JUnitTestMethodWithoutAssertAstVisitor extends AbstractMethodVisitor {

    Set<String> assertMethodPatterns

    @Override
    void visitMethod(MethodNode node) {
        if (JUnitUtil.isTestMethod(node)) {
            if (!statementContainsAssertions(node.code) && !checksException(node) && !checksTimeout(node)) {
                addViolation node, "Test method '$node.name' makes no assertions"
            }
        }
    }

    private boolean statementContainsAssertions(Statement code) {
        if (!code) {
            return false
        }
        def assertionTrap = new AssertionTrap(assertMethodPatterns: assertMethodPatterns)
        code.visit assertionTrap
        assertionTrap.assertionFound
    }

    private boolean checksException(ASTNode node) {
        hasTestAnnotationWithMember(node, 'expected')
    }

    private boolean checksTimeout(ASTNode node) {
        hasTestAnnotationWithMember(node, 'timeout')
    }

    private boolean hasTestAnnotationWithMember(ASTNode node, String memberName) {
        def testAnnotation = AstUtil.getAnnotation(node, 'Test') ?: AstUtil.getAnnotation(node, 'org.junit.Test')
        testAnnotation?.getMember(memberName)
    }
}

/**
 * Visits code searching for assert statements or assert.* method calls.
 */
class AssertionTrap extends AbstractAstVisitor {
    Set<String> assertMethodPatterns
    def assertionFound = false

    @Override
    void visitAssertStatement(AssertStatement statement) {
        assertionFound = true
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (call.method instanceof ConstantExpression && methodNamesCountsAsAssertion(call.method.value)) {
            assertionFound = true
        } else {
            super.visitMethodCallExpression call
        }
    }

    private boolean methodNamesCountsAsAssertion(methodName) {
        if (methodName instanceof String) {
            return assertMethodPatterns.any { pattern ->
                methodName.matches(pattern)
            }
        }
        false
    }
}
