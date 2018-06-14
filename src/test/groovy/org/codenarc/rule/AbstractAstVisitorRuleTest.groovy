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
package org.codenarc.rule

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining
import static org.junit.Assert.assertFalse

import org.codehaus.groovy.ast.ClassNode
import org.codenarc.util.WildcardPattern
import org.junit.Test

/**
 * Tests for AbstractAstVisitorRule
 *
 * @author Chris Mair
 * @author Hamlet D'Arcy
 */
class AbstractAstVisitorRuleTest extends AbstractRuleTestCase<AbstractAstVisitorRule> {
    private static final SOURCE = '''
        class MyClass {
            int value
        }
    '''
    static skipTestThatUnrelatedCodeHasNoViolations

    @Test
    void testApplyTo() {
        assertSingleViolation(SOURCE)
    }

    @Test
    void testApplyTo_TwoClasses() {
        final SOURCE2 = '''
            class MyClass1 {
                int value
            }
            class MyClass2 {
                String name
            }
        '''
        assertTwoViolations(SOURCE2, null, null, null, null)
    }

    @Test
    void testApplyToClassNames() {
        rule.applyToClassNames = 'MyClass'
        assertSingleViolation(SOURCE)

        rule.applyToClassNames = 'OtherClass,SomeTest,MyClass'
        assertSingleViolation(SOURCE)

        rule.applyToClassNames = 'XXX'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToClassNames_Wildcards() {
        rule.applyToClassNames = 'My*'
        assertSingleViolation(SOURCE)
        rule.applyToClassNames = 'MyTest??'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToClassNames_PatternSpecifiesPackage_NoPackage() {
        rule.applyToClassNames = 'org.codenarc.MyClass'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyToClassNames_PatternMatchesSamePackage() {
        final SOURCE2 = '''
            package org.codenarc
            class MyClass { }
        '''
        rule.applyToClassNames = 'org.codenarc.OtherClass,MyClass'
        assertSingleViolation(SOURCE2)
    }

    @Test
    void testApplyToClassNames_PatternMatchesDifferentPackage() {
        final SOURCE2 = '''
            package org.other.project
            class MyClass { }
        '''
        rule.applyToClassNames = 'com.big.Other*,MyTest,org.codenarc.MyCla?s'
        assertNoViolations(SOURCE2)
    }

    @Test
    void testDoNotApplyToClassNames() {
        rule.doNotApplyToClassNames = 'OtherClass'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToClassNames = 'OtherClass,MyClass,SomeTest'
        assertNoViolations(SOURCE)

        rule.doNotApplyToClassNames = 'MyClass'
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoNotApplyToClassNames_Wildcards() {
        rule.doNotApplyToClassNames = 'My??Test'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToClassNames = 'My??Test,OtherTest'
        assertSingleViolation(SOURCE)

        rule.doNotApplyToClassNames = 'M*Cl?ss'
        assertNoViolations(SOURCE)
    }

    @Test
    void testDoNotApplyToClassNames_PatternSpecifiesPackage_NoPackage() {
        rule.doNotApplyToClassNames = 'org.codenarc.MyClass'
        assertSingleViolation(SOURCE)
    }

    @Test
    void testDoNotApplyToClassNames_PatternMatchesClassNameWithPackage() {
        final SOURCE2 = '''
            package org.codenarc
            class MyClass { }
        '''
        rule.doNotApplyToClassNames = 'Other*,MyTest,org.codenarc.MyCla?s'
        assertNoViolations(SOURCE2)
    }

    @Test
    void testDoNotApplyToClassNames_PatternMatchestClassNameWithoutPackage() {
        final SOURCE2 = '''
            package org.codenarc
            class MyClass { }
        '''
        rule.doNotApplyToClassNames = 'Other*,MyClass'
        assertNoViolations(SOURCE2)
    }

    @Test
    void testDoNotApplyToClassNames_PatternDoesNotMatchPackage() {
        final SOURCE2 = '''
            package org.other.project
            class MyClass { }
        '''
        rule.doNotApplyToClassNames = 'Other*,MyTest,org.codenarc.MyCla?s'
        assertSingleViolation(SOURCE2)
    }

    @Test
    void testDoNotApplyToClassNames_PatternMatchesClassNameAndAlsoPackage() {
        final SOURCE2 = '''
            package org.codenarc
            class MyClass { }
        '''
        rule.doNotApplyToClassNames = 'MyC*ss,MyTest,org.*.MyClass'
        assertNoViolations(SOURCE2)
    }

    @Test
    void testBothApplyToClassNamesAndDoNotApplyToClassNames() {
        rule.applyToClassNames = 'MyClass'         // apply = YES
        rule.doNotApplyToClassNames = 'MyClass'    // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToClassNames = 'Xxx'             // apply = NO
        rule.doNotApplyToClassNames = 'MyClass'    // doNotApply = YES
        assertNoViolations(SOURCE)

        rule.applyToClassNames = 'MyClass'         // apply = YES
        rule.doNotApplyToClassNames = 'Xxx'        // doNotApply = NO
        assertSingleViolation(SOURCE)

        rule.applyToClassNames = 'Xxx'             // apply = NO
        rule.doNotApplyToClassNames = 'Xxx'        // doNotApply = NO
        assertNoViolations(SOURCE)
    }

    @Test
    void testDefineNewApplyToClassNamesProperty() {
        rule = new FakeAstVisitorRuleDefinesNewApplyToClassNamesRule()
        assertSingleViolation('class ApplyToClassName { }')
        assertNoViolations('class DoNotApplyToClassName { }')
        assertNoViolations('class OtherClass { }')
    }

    @Test
    void testApplyTo_AstVisitorClassNull() {
        rule.astVisitorClass = null
        shouldFailWithMessageContaining('astVisitorClass') { applyRuleTo('def x') }
    }

    @Test
    void testApplyTo_AstVisitorClassNotAnAstVisitor() {
        rule.astVisitorClass = String
        shouldFailWithMessageContaining('astVisitorClass') { applyRuleTo('def x') }
    }

    @Test
    void testDEFAULT_TEST_FILES() {
        assert 'MyTest.groovy' ==~ AbstractAstVisitorRule.DEFAULT_TEST_FILES
        assert 'MyTests.groovy' ==~ AbstractAstVisitorRule.DEFAULT_TEST_FILES
        assert 'MyTestCase.groovy' ==~ AbstractAstVisitorRule.DEFAULT_TEST_FILES
        assert 'MySpec.groovy' ==~ AbstractAstVisitorRule.DEFAULT_TEST_FILES
        assertFalse 'MyNonTestClass.groovy' ==~ AbstractAstVisitorRule.DEFAULT_TEST_FILES
    }

    @Test
    void testDEFAULT_TEST_CLASS_NAMES() {
        def wildcardPattern = new WildcardPattern(AbstractAstVisitorRule.DEFAULT_TEST_CLASS_NAMES)
        assert wildcardPattern.matches('MyTest')
        assert wildcardPattern.matches('MyTests')
        assert wildcardPattern.matches('MyTestCase')
        assert wildcardPattern.matches('MySpec')
        assertFalse wildcardPattern.matches('MyNonTestClass')\
    }

    @Override
    protected AbstractAstVisitorRule createRule() {
        new FakeAstVisitorRule()
    }
}

// Test AbstractAstVisitorRule implementation class
class FakeAstVisitorRule extends AbstractAstVisitorRule {
    String name = 'Test'
    int priority = 3
    Class astVisitorClass = FakeAstVisitor
}

// Test AstVisitor implementation class
class FakeAstVisitor extends AbstractAstVisitor {
    @Override
    void visitClassEx(ClassNode classNode) {
        violations.add(new Violation(rule:rule))
        super.visitClassEx(classNode)
    }
}

// Test AbstractAstVisitorRule implementation class that defines new 'applyToClassNames' and 'doNotApplyToClassNames' properties
class FakeAstVisitorRuleDefinesNewApplyToClassNamesRule extends AbstractAstVisitorRule {
    String name = 'Test'
    int priority = 3
    Class astVisitorClass = FakeAstVisitor
    String applyToClassNames = 'ApplyToClassName'
    String doNotApplyToClassNames = 'DoNotApplyToClassName'
}
