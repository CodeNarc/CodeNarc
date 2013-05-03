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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryDefInFieldDeclarationRule
 *
 * @author Hamlet D'Arcy
 */
class UnnecessaryDefInFieldDeclarationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryDefInFieldDeclaration'
    }

    /*
     * Success scenarios
     */

    @Test
    void testSuccessScenario_modifiers() {
        final SOURCE = '''
            class MyClass {
                def          field1 = { }
                private      field2 = { }
                protected    field3 = { }
                public       field4 = { }
                static       field5 = { }
                final        field6 = { }
                strictfp     field7 = { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_fieldNamesContainingModifierNames() {
        final SOURCE = '''
             class MyClass {
                def privateField = { }
                def protectedField = { }
                def publicField = { }
                def staticField = { }
                def finalField = { }
                def synchronizedField = { }
                def strictfpField = { }
                def abstractField = { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_types() {
        final SOURCE = '''
            class MyClass {
                Object field1 = {}
                String field2 = {}
                int    field3 = {}
                void   field4 = {}
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_defAfterAssignment() {
        final SOURCE = '''
            class SampleTest {
                private static final String SOURCE_SHARED_BETWEEN_TESTS = \'\'\'
                    def sum = 1 + 1
                \'\'\'
            }
        '''
        assertNoViolations(SOURCE)
    }

    /*
     * Violations
     */

    @Test
    void testViolation_defAndPrivate() {
        final SOURCE = '''
            class MyClass {
                def private field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def private field', 'The def keyword is unneeded when a field is marked private')
    }

    @Test
    void testViolation_defAndProtected() {
        final SOURCE = '''
            class MyClass {
                def protected field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def protected field', 'The def keyword is unneeded when a field is marked protected')
    }

    @Test
    void testViolation_defAndPublic() {
        final SOURCE = '''
            class MyClass {
                def public field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def public field', 'The def keyword is unneeded when a field is marked public')
    }

    @Test
    void testViolation_defAndStatic() {
        final SOURCE = '''
            class MyClass {
                def static field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def static field', 'The def keyword is unneeded when a field is marked static')
    }

    @Test
    void testViolation_defAndFinal() {
        final SOURCE = '''
            class MyClass {
                def final field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def final field', 'The def keyword is unneeded when a field is marked final')
    }

    @Test
    void testViolation_defAndTyped() {
        final SOURCE = '''
            class MyClass {
                def String field = ''
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def String field', 'The def keyword is unneeded when a field type is specified')
    }

    @Test
    void testViolation_defAndStrictfp() {
        final SOURCE = '''
            class MyClass {
                def strictfp field = { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def strictfp field', 'The def keyword is unneeded when a field is marked strictfp')
    }

    @Test
    void testViolation_defAndObjectType() {
        final SOURCE = '''
            class MyClass {
                def Object field = {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def Object field', 'The def keyword is unneeded when a field is specified Object type')
    }

    @Test
    void testViolation_fieldDeclarationAcrossMultipleLines() {
        final SOURCE = '''
            class MyClass {
                def
                static
                String field = { }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def', 'The def keyword is unneeded when a field is marked static')
    }

    @Test
    void testViolation_multipleFieldsOnSingleLine() {
        final SOURCE = '''
            class MyClass {
                def field1 = { 'good' }; def public field2 = { 'bad' }
                def public field3 = { 'bad' }; def field4 = { 'good' }
            }
        '''
        assertTwoViolations(SOURCE,
                            3, 'def public field2', 'The def keyword is unneeded when a field is marked public',
                            4, 'def public field3', 'The def keyword is unneeded when a field is marked public')
    }

    protected Rule createRule() {
        new UnnecessaryDefInFieldDeclarationRule()
    }
}
