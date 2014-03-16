/*
 * Copyright 2012 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for JUnitLostTestRule
 *
 * @author Chris Mair
  */
class JUnitLostTestRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitLostTest'
    }

    @Test
    void testApplyTo_NonMatchingMethods_NoViolations() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                void doSomething() { }          // not named test*()
                void testMe1(int count) { }     // not zero-arg
                int testMe2() { }               // not void
                private void testMe3() { }      // not public
                static void testMe4() { }       // static
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NotJUnit4TestClass_NoViolations() {
        final SOURCE = '''
            class MyTestCase {
                void testMe() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_AnnotatedWithTest_NoViolations() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                @Test
                void testMe() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_PublicVoidTestMethod_NoTestAnnotation_Violation() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                void testMe() { }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'void testMe() { }')
    }

    @Test
    void testApplyTo_ExplicitlyDeclaredPublicMethod_Violation() {
        final SOURCE = '''
            import org.junit.Test
            class MyTestCase {
                public void testMe() { }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'public void testMe() { }')
    }

    @Test
    void testApplyTo_OtherJUnit4Imports() {
        final SOURCE = '''
            import org.junit.After
            class MyTestCase {
                void testMe() { }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'void testMe() { }')
    }

    @Test
    void testApplyTo_StarImportOfJUnit4Package() {
        final SOURCE = '''
            import org.junit.*
            class MyTestCase {
                void testMe() { }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'void testMe() { }')
    }

    @Test
    void testApplyTo_NonTestClass_NoViolations() {
        final SOURCE = '''
            import org.junit.*
            class MyOtherClass {
                void testMe() { }
            }
        '''
        sourceCodePath = 'src/MyController.groovy'
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new JUnitLostTestRule()
    }
}
