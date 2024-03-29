/*
 * Copyright 2013 the original author or authors.
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
package org.codenarc.rule.generic

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.jupiter.api.Test

/**
 * Tests for IllegalClassMemberRule
 *
 * @author Chris Mair
 */
class IllegalClassMemberRuleTest extends AbstractRuleTestCase<IllegalClassMemberRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'IllegalClassMember'
    }

    @Test
    void testNoConfiguration_RuleIsNotReady() {
        final SOURCE = ' class MyClass { }'
        rule.applyToClassNames = null       // overwrite initialization
        assert !rule.ready
        assertNoViolations(SOURCE)
    }

    @Test
    void testMustConfigureOneOfApplyToFields_AndOneOfTheModifiersFields() {
        def newRule = new IllegalClassMemberRule()
        newRule.illegalFieldModifiers = 'public'
        assert !newRule.ready

        newRule.applyToClassNames = '*'
        assert newRule.ready

        newRule.applyToClassNames = null
        newRule.applyToFileNames = '*'
        assert newRule.ready

        newRule.applyToFileNames = null
        newRule.applyToFilesMatching = 'A.*'
        assert newRule.ready

        newRule.illegalFieldModifiers = null
        newRule.applyToClassNames = '*'
        assert !newRule.ready
    }

    // Fields

    @Test
    void testFields_IllegalFieldModifiers_Match_Violations() {
        final SOURCE = '''
            class MyClass {
                public field1
                protected field2
                private field3
                public static final FIELD4
            }
        '''
        rule.illegalFieldModifiers = 'public static, protected'
        assertViolations(SOURCE,
            [line:4, source:'protected field2', message:'field2'],
            [line:6, source:'public static final FIELD4', message:'FIELD4'])
    }

    @Test
    void testFields_AllowedFieldModifiers_NoMatch_Violations() {
        final SOURCE = '''
            class MyClass {
                public field1
                protected field2
            }
        '''
        rule.allowedFieldModifiers = 'public final, private, protected static'
        assertViolations(SOURCE,
            [line:3, source:'public field1', message:'field1'],
            [line:4, source:'protected field2', message:'field2'])
    }

    // Properties

    @Test
    void testProperties_IllegalPropertyModifiers_Match_Violations() {
        final SOURCE = '''
            class MyClass {
                def property1
                final property2
                static property3
            }
        '''
        rule.illegalPropertyModifiers = 'final'
        assertViolations(SOURCE,
            [line:4, source:'final property2', message:'property2'])
    }

    @Test
    void testProperties_AllowedPropertyModifiers_NoMatch_Violations() {
        final SOURCE = '''
            class MyClass {
                def property1
                final property2
                static property3
            }
        '''
        rule.allowedPropertyModifiers = 'static'
        assertViolations(SOURCE,
            [line:3, source:'def property1', message:'property1'],
            [line:4, source:'final property2', message:'property2'])
    }

    // Methods

    @Test
    void testMethods_IllegalMethodModifiers_Match_Violations() {
        final SOURCE = '''
            class MyClass {
                public method1() { }
                protected method2() { }
                private method3() { }
                public static final method4() { }
            }
        '''
        rule.illegalMethodModifiers = 'public static, protected'
        assertViolations(SOURCE,
            [line:4, source:'protected method2()', message:'method2'],
            [line:6, source:'public static final method4()', message:'method4'])
    }

    @Test
    void testMethods_AllowedMethodModifiers_NoMatch_Violations() {
        final SOURCE = '''
            class MyClass {
                public method1() { }
                protected method2() { }
            }
        '''
        rule.allowedMethodModifiers = 'public final, private, protected static'
        assertViolations(SOURCE,
            [line:3, source:'public method1()', message:'method1'],
            [line:4, source:'protected method2', message:'method2'])
    }

    @Test
    void testMethods_IgnoreMethodNames_MatchesSingleName_NoViolations() {
        final SOURCE = '''
          class MyClass {
            public method1() { }
          }
        '''
        rule.ignoreMethodNames = 'method1'
        rule.illegalMethodModifiers = 'public'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
          class MyClass {
            public method1() { }
          }
        '''
        rule.ignoreMethodNames = 'method2'
        rule.illegalMethodModifiers = 'public'
        assertSingleViolation(SOURCE, 3, 'public method1')
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
          class MyClass {
            public method1() { }
            public otherMethod1() { }
            public badMethod1() { }
          }
        '''
        rule.allowedMethodModifiers = 'protected'
        rule.ignoreMethodNames = 'meth?d?,x*, badM*'
        assertSingleViolation(SOURCE, 4, 'public otherMethod1')
    }

//-----------------------

    @Test
    void testMethods_IgnoreMethodsWithAnnotationNames_MatchesSingleName_NoViolations() {
        final SOURCE = '''
          class MyClass {
            @Override
            public method1() { }
          }
        '''
        rule.ignoreMethodsWithAnnotationNames = 'Override'
        rule.illegalMethodModifiers = 'public'
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodsWithAnnotationNames_MatchesNoNames() {
        final SOURCE = '''
          class MyClass {
            @Override
            public method1() { }
          }
        '''
        rule.ignoreMethodsWithAnnotationNames = 'Test'
        rule.illegalMethodModifiers = 'public'
        assertSingleViolation(SOURCE, 4, 'public method1')
    }

    @Test
    void testApplyTo_IgnoreMethodsWithAnnotationNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
          class MyClass {
            @Override public method1() { }

            public otherMethod1() { }

            @SuppressWarnings('Bad')
            public badMethod1() { }
          }
        '''
        rule.allowedMethodModifiers = 'protected'
        rule.ignoreMethodsWithAnnotationNames = 'Over?ide,Other,Supp*Warnings'
        assertSingleViolation(SOURCE, 5, 'public otherMethod1')
    }

//---------------

    @Override
    protected IllegalClassMemberRule createRule() {
        new IllegalClassMemberRule(applyToClassNames:'*')
    }
}
