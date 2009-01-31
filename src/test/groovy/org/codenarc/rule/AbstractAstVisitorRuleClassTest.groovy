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
import org.codenarc.source.SourceString
import org.codenarc.test.AbstractTest

/**
 * Tests for AbstractAstVisitorRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class AbstractAstVisitorRuleClassTest extends AbstractTest {

    static final SOURCE = 'ABC'
    def rule
    def sourceCode

    void testApplyTo() {
        def violations = rule.applyTo(sourceCode)
        assert violations.size() == 1
        assert violations[0].rule == rule
        assert violations[0].sourceLine == SOURCE
    }

    void testApplyTo_CompilerErrorInSource() {
        final NEW_SOURCE = '''
            class MyClass {
                try {
                } catch(MyException e) {
                    // TODO Should do something here
                }
            }
        '''
        sourceCode = new SourceString(NEW_SOURCE)
        def violations = rule.applyTo(sourceCode)
        assert violations.size() == 0
    }

    void testApplyTo_TwoClasses() {
        final NEW_SOURCE = '''
            class MyClass1 {
                int value
            }
            class MyClass2 {
                String name
            }
        '''
        sourceCode = new SourceString(NEW_SOURCE)
        def violations = rule.applyTo(sourceCode)
        assert violations.size() == 2
        assert violations[0].rule == rule
        assert violations[1].rule == rule
    }

    void testApplyTo_AstVisitorClassNull() {
        rule.astVisitorClass = null
        shouldFailWithMessageContaining('astVisitorClass') { rule.applyTo(sourceCode) }
    }

    void testApplyTo_AstVisitorClassNotAnAstVisitor() {
        rule.astVisitorClass = String
        shouldFailWithMessageContaining('astVisitorClass') { rule.applyTo(sourceCode) }
    }

    void setUp() {
        super.setUp()
        rule = new TestAstVisitorRule()
        sourceCode = new SourceString(SOURCE)
    }
}

// Test AbstractAstVisitorRule implementation class
class TestAstVisitorRule extends AbstractAstVisitorRule {
    String name = 'Test'
    int priority = 3
    Class astVisitorClass = TestAstVisitor
}

// Test AstVisitor implementation class
class TestAstVisitor extends AbstractAstVisitor {
    void visitClass(ClassNode classNode) {
        violations.add(new Violation(rule:rule, sourceLine:sourceCode.text))
        super.visitClass(classNode)
    }

}