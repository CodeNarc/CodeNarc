/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.rule

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode

/**
 * Unit test for AbstractAstVisitor SuppressWarnings support.
 */
class SuppressWarningsTest extends AbstractRuleTestCase {

    @SuppressWarnings('JUnitTestMethodWithoutAssert')
    @Override
    void testThatUnrelatedCodeHasNoViolations() {
        // make sure base method does not fail
    }

    void testSuppressWarningsOnFields() {
        final SOURCE = '''
            class MyClass {
                @SuppressWarnings('SuppressWarningsTest')
                def c = { println 'ok' }
            }
        '''
        // only allow violation from class
        assertSingleViolation(SOURCE, 2, 'class MyClass')
    }

    void testSuppressWarningsOnMethods() {
        final SOURCE = '''
            @SuppressWarnings('SuppressWarningsTest')
            def c() { println 'ok' }
        '''
        assertNoViolations(SOURCE)
    }

    void testSuppressWarningsOnClass() {
        final SOURCE = '''
            @SuppressWarnings('SuppressWarningsTest')
            class MyClass { }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new SuppressWarningsTestRule()
    }

}

private class SuppressWarningsTestRule extends AbstractAstVisitorRule {
    String name = 'SuppressWarningsTest'
    int priority = 2
    Class astVisitorClass = SuppressWarningsTestAstVisitor
}

class SuppressWarningsTestAstVisitor extends AbstractAstVisitor {
    @Override
    void visitFieldEx(FieldNode node) {
        addViolation node
    }

    @Override
    void visitMethodEx(MethodNode node) {
        addViolation node
    }

    @Override
    protected void visitClassEx(ClassNode node) {
        addViolation node
    }
}