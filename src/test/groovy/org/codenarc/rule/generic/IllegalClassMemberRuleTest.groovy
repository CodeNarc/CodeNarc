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

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for IllegalClassMemberRule
 *
 * @author Chris Mair
 */
class IllegalClassMemberRuleTest extends AbstractRuleTestCase {

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
            [lineNumber:4, sourceLineText:'protected field2', messageText:'field2'],
            [lineNumber:6, sourceLineText:'public static final FIELD4', messageText:'FIELD4'])
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
            [lineNumber:3, sourceLineText:'public field1', messageText:'field1'],
            [lineNumber:4, sourceLineText:'protected field2', messageText:'field2'])
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
            [lineNumber:4, sourceLineText:'final property2', messageText:'property2'])
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
            [lineNumber:3, sourceLineText:'def property1', messageText:'property1'],
            [lineNumber:4, sourceLineText:'final property2', messageText:'property2'])
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
            [lineNumber:4, sourceLineText:'protected method2()', messageText:'method2'],
            [lineNumber:6, sourceLineText:'public static final method4()', messageText:'method4'])
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
            [lineNumber:3, sourceLineText:'public method1()', messageText:'method1'],
            [lineNumber:4, sourceLineText:'protected method2', messageText:'method2'])
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

    protected Rule createRule() {
        new IllegalClassMemberRule(applyToClassNames:'*')
    }
}
