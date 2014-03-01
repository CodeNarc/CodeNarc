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
 * Tests for UnnecessaryDefInMethodDeclarationRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryDefInMethodDeclarationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryDefInMethodDeclaration'
    }

    /*
     * Success scenarios
     */

    @Test
    void testSuccessScenario_modifiers() {
        final SOURCE = '''
            String getTreeCellRendererComponent(String p1, def p2) {
                null
            }
            def          method1() { }
            private      method2() { }
            protected    method3() { }
            public       method4() { }
            static       method5() { }
            final        method6() { }
            synchronized method7() { }
            strictfp     method8() { }

            class Test { abstract method() }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_generics() {
        final SOURCE = '''
            def <T> T getService(String serviceName) { null }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_methodNamesContainingModifierNames_beginOfMethodName() {
        final SOURCE = '''
            def privateMethod() { }
            def protectedMethod() { }
            def publicMethod() { }
            def staticMethod() { }
            def finalMethod() { }
            def synchronizedMethod() { }
            def strictfpMethod() { }
            def abstractMethod() { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_methodNamesContainingModifierNames_insideMethodName_singleQuotes() {
        final SOURCE = '''
            def 'modifier private      inside method name'() { }
            def 'modifier protected    inside method name'() { }
            def 'modifier public       inside method name'() { }
            def 'modifier static       inside method name'() { }
            def 'modifier final        inside method name'() { }
            def 'modifier synchronized inside method name'() { }
            def 'modifier strictfp     inside method name'() { }
            def 'modifier abstract     inside method name'() { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_methodNamesContainingModifierNames_insideMethodName_doubleQuotes() {
        final SOURCE = '''
            def "modifier private      inside method name"() { }
            def "modifier protected    inside method name"() { }
            def "modifier public       inside method name"() { }
            def "modifier static       inside method name"() { }
            def "modifier final        inside method name"() { }
            def "modifier synchronized inside method name"() { }
            def "modifier strictfp     inside method name"() { }
            def "modifier abstract     inside method name"() { }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSuccessScenario_types() {
        final SOURCE = '''
            Object method1() { null }
            String method2() { null }
            int    method3() { 1 }
            void   method4() { }
        '''
        assertNoViolations(SOURCE)
    }

    /*
     * Violations
     */

    @Test
    void testViolation_defInConstructor() {
        final SOURCE = '''
            class MyClass {
                def MyClass() {}
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def MyClass()', 'Violation in class MyClass. The def keyword is unneeded on constructors')
    }

    @Test
    void testViolation_defAndPrivate() {
        final SOURCE = '''
            def private method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def private method()', 'The def keyword is unneeded when a method is marked private')
    }

    @Test
    void testViolation_defAndProtected() {
        final SOURCE = '''
            def protected method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def protected method()', 'The def keyword is unneeded when a method is marked protected')
    }

    @Test
    void testViolation_defAndPublic() {
        final SOURCE = '''
            def public method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def public method()', 'The def keyword is unneeded when a method is marked public')
    }

    @Test
    void testViolation_defAndStatic() {
        final SOURCE = '''
            def static method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def static method()', 'The def keyword is unneeded when a method is marked static')
    }

    @Test
    void testViolation_defAndFinal() {
        final SOURCE = '''
            def final method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def final method()', 'The def keyword is unneeded when a method is marked final')
    }

    @Test
    void testViolation_defAndSynchronized() {
        final SOURCE = '''
            def synchronized method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def synchronized method()', 'The def keyword is unneeded when a method is marked synchronized')
    }

    @Test
    void testViolation_defAndStrictfp() {
        final SOURCE = '''
            def strictfp method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def strictfp method()', 'The def keyword is unneeded when a method is marked strictfp')
    }

    @Test
    void testViolation_defAndAbstract() {
        final SOURCE = '''
            abstract class Test {
                def abstract method()
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def abstract method()', 'The def keyword is unneeded when a method is marked abstract')
    }

    @Test
    void testViolation_defAndObjectType() {
        final SOURCE = '''
            def Object method() { null }
        '''
        assertSingleViolation(SOURCE, 2, 'def Object method()', 'The def keyword is unneeded when a method returns the Object type')
    }

    @Test
    void testViolation_defAndReturnType() {
        final SOURCE = '''
            def List method() { null }
        '''
        assertSingleViolation(SOURCE, 2, 'def List method()', 'The def keyword is unneeded when a method specifies a return type')
    }

    @Test
    void testViolation_methodDeclarationAcrossMultipleLines() {
        final SOURCE = '''
            def
            static
            String method() { }
        '''
        assertSingleViolation(SOURCE, 2, 'def', 'The def keyword is unneeded when a method is marked static')
    }

    @Test
    void testViolation_multipleMethodsOnSingleLine() {
        final SOURCE = '''
            def method1() { 'good' }; def public method2() { 'bad' }
            def public method3() { 'bad' }; def method4() { 'good' }
        '''
        assertTwoViolations(SOURCE,
                            2, 'def public method2()', 'The def keyword is unneeded when a method is marked public',
                            3, 'def public method3()', 'The def keyword is unneeded when a method is marked public')
    }

    protected Rule createRule() {
        new UnnecessaryDefInMethodDeclarationRule()
    }
}
