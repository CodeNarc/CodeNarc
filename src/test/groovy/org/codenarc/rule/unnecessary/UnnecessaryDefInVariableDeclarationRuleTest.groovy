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
 * Tests for UnnecessaryDefInVariableDeclarationRule
 *
 * @author René Scheibe
  */
class UnnecessaryDefInVariableDeclarationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryDefInVariableDeclaration'
    }

    /*
     * Success scenarios
     */

    @Test
    void testSuccessScenario_modifiers() {
        final SOURCE = '''
            final SOURCE = \'\'\'
                def closure = { new Date() }
            \'\'\'
            final     variable0 = 'example'
            def       variable1 = 'example'
            private   variable2 = 'example'
            protected variable3 = 'example'
            public    variable4 = 'example'
            final     variable5 = 'example'
            volatile  variable6 = 'example'
            transient variable7 = 'example'

            class Test { static variable = 'example' }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_variableNamesContainingModifierNames() {
        final SOURCE = '''
            def privateVariable   = 'example'
            def protectedVariable = 'example'
            def publicVariable    = 'example'
            def finalVariable     = 'example'
            def volatileVariable  = 'example'
            def transientVariable = 'example'

            class Test { def staticVariable = 'example' }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_types() {
        final SOURCE = '''
            Object variable1 = 'example'
            String variable2 = 'example'
            int    variable3 = 1
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_misc() {
        final SOURCE = '''
            def variable1 = variable2 = 'example'
            def variable3 = true ? 1 : 2
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_Enum() {
        final SOURCE = '''
            enum MavenScope {
                COMPILE,
                RUNTIME,
                TEST,
                PROVIDED,
                SYSTEM
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testThatFieldsAreNotChecked() {
        final SOURCE = '''
            class MyClass {
                def private variable1
                def private variable2 = 'example'
            }
        '''
        assertNoViolations SOURCE
    }

    /*
     * Violations
     */

    @Test
    void testViolation_defAndPrivate() {
        final SOURCE = '''
            def private variable1
            def private variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def private variable1', 'The def keyword is unneeded when a variable is marked private',
                            3, 'def private variable2', 'The def keyword is unneeded when a variable is marked private')
    }

    @Test
    void testViolation_defAndProtected() {
        final SOURCE = '''
            def protected variable1
            def protected variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def protected variable1', 'The def keyword is unneeded when a variable is marked protected',
                            3, 'def protected variable2', 'The def keyword is unneeded when a variable is marked protected')
    }

    @Test
    void testViolation_defAndPublic() {
        final SOURCE = '''
            def public variable1
            def public variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def public variable1', 'The def keyword is unneeded when a variable is marked public',
                            3, 'def public variable2', 'The def keyword is unneeded when a variable is marked public')
    }

    @Test
    void testViolation_defAndFinal() {
        final SOURCE = '''
            def final variable1
            def final variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def final variable1', 'The def keyword is unneeded when a variable is marked final',
                            3, 'def final variable2', 'The def keyword is unneeded when a variable is marked final')
    }

    @Test
    void testViolation_defAndVolatile() {
        final SOURCE = '''
            def volatile variable1
            def volatile variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def volatile variable1', 'The def keyword is unneeded when a variable is marked volatile',
                            3, 'def volatile variable2', 'The def keyword is unneeded when a variable is marked volatile')
    }

    @Test
    void testViolation_defAndTransient() {
        final SOURCE = '''
            def transient variable1
            def transient variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def transient variable1', 'The def keyword is unneeded when a variable is marked transient',
                            3, 'def transient variable2', 'The def keyword is unneeded when a variable is marked transient')
    }

    @Test
    void testViolation_defAndObjectType() {
        final SOURCE = '''
            def Object variable1
            def Object variable2 = 'example'
        '''
        assertTwoViolations(SOURCE,
                            2, 'def Object variable1', 'The def keyword is unneeded when a variable is of type Object',
                            3, 'def Object variable2', 'The def keyword is unneeded when a variable is of type Object')
    }

    @Test
    void testViolation_variableDeclarationAcrossMultipleLines() {
        final SOURCE = '''
            def
            public
            int a = 1
        '''
        assertSingleViolation(SOURCE, 2, 'def', 'The def keyword is unneeded when a variable is marked public')
    }

    @Test
    void testViolation_variableTypeDeclared() {
        final SOURCE = '''
            def String foo
        '''
        assertSingleViolation(SOURCE, 2, 'def String foo', 'The def keyword is unneeded when a variable is declared with a type')
    }

    @Test
    void testViolation_multipleVariablesOnSingleLine() {
        final SOURCE = '''
            def variable1 = 'good'; def public variable2 = 'bad'
            def public variable3 = 'bad'; def variable4 = 'good'
        '''
        assertTwoViolations(SOURCE,
                2, "def public variable2 = 'bad'", 'The def keyword is unneeded when a variable is marked public',
                3, "def public variable3 = 'bad'", 'The def keyword is unneeded when a variable is marked public')
    }

    protected Rule createRule() {
        new UnnecessaryDefInVariableDeclarationRule()
    }
}
