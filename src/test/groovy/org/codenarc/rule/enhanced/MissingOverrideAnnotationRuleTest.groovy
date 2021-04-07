/*
 * Copyright 2017 the original author or authors.
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
package org.codenarc.rule.enhanced

import org.codehaus.groovy.control.Phases
import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for MissingOverrideAnnotationRule
 *
 * @author Marcin Erdmann
 */
class MissingOverrideAnnotationRuleTest extends AbstractRuleTestCase<MissingOverrideAnnotationRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'MissingOverrideAnnotation'
        assert rule.compilerPhase == Phases.SEMANTIC_ANALYSIS
    }

    @Test
    void testNoViolationsSuperClass() {
        assertNoViolations '''
            class SuperClass {
                String superClassMethod(String value) {
                }
            }

            class ValidClass extends SuperClass {
                @Override
                String superClassMethod(String value) {
                }
            }
        '''
    }

    @Test
    void testSingleViolationSuperClass() {
        final SOURCE = '''
            class SuperClass {
                String superClassMethod(String value) {
                }
            }

            class ValidClass extends SuperClass {
                String superClassMethod(String value) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 8, 'String superClassMethod(String value) {',
            '''Method 'superClassMethod' is overriding a method in 'SuperClass' but is not annotated with @Override.''')
    }

    @Test
    void testNoViolationsInterface() {
        assertNoViolations '''
            interface ImplementedInterface {
                void interfaceMethod(String stringValue, Object objectValue)
            }

            class ValidClass implements ImplementedInterface {
                @Override
                void interfaceMethod(String stringValue, Object objectValue) {
                }
            }
        '''
    }

    @Test
    void testSingleViolationInterface() {
        final SOURCE = '''
            interface ImplementedInterface {
                void interfaceMethod(String stringValue, Object objectValue)
            }

            class ValidClass implements ImplementedInterface {
                void interfaceMethod(String stringValue, Object objectValue) {
                }
            }
        '''

        assertSingleViolation(SOURCE, 7, 'void interfaceMethod(String stringValue, Object objectValue) {',
            '''Method 'interfaceMethod' is overriding a method in 'ImplementedInterface' but is not annotated with @Override.''')
    }

    @Test
    void testNoViolationsOverload() {
        assertNoViolations '''
            class SuperClass {
                String superClassMethod(String value) {
                }
            }

            class ValidClass extends SuperClass {
                void superClassMethod(int value) {
                }
            }
        '''
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            abstract class SuperClass {
                abstract String superClassMethod(String value)
            }

            interface ImplementedInterface {
                void interfaceMethod(String stringValue, Object objectValue)
            }

            abstract class MiddleClass extends SuperClass implements ImplementedInterface {
            }

            class InvalidClass extends MiddleClass {
                String superClassMethod(String value) {
                }

                void interfaceMethod(String stringValue, Object objectValue) {
                }

                String toString() {
                }
            }
        '''
        assertViolations(SOURCE,
            [
                lineNumber    : 14,
                source: 'String superClassMethod(String value) {',
                messageText   : '''Method 'superClassMethod' is overriding a method in 'SuperClass' but is not annotated with @Override.'''
            ],
            [
                lineNumber    : 17,
                source: 'void interfaceMethod(String stringValue, Object objectValue) {',
                messageText   : '''Method 'interfaceMethod' is overriding a method in 'ImplementedInterface' but is not annotated with @Override.'''
            ],
            [
                lineNumber    : 20,
                source: 'String toString() {',
                messageText   : '''Method 'toString' is overriding a method in 'java.lang.Object' but is not annotated with @Override.'''
            ]
        )
    }

    @Test
    void testViolationsInInnerAndOuterClasses() {
        final SOURCE = '''
            import java.util.concurrent.Callable;

            class OuterClassWithViolation implements Callable<String> {

                static class InnerClassWithViolation implements Runnable {
                    void run() {
                    }
                }

                String call() {
                }

            }
        '''

        assertViolations(SOURCE,
            [
                lineNumber    : 7,
                source: 'void run() {',
                messageText   : '''Method 'run' is overriding a method in 'java.lang.Runnable' but is not annotated with @Override.'''
            ],
            [
                lineNumber    : 11,
                source: 'String call() {',
                messageText   : '''Method 'call' is overriding a method in 'java.util.concurrent.Callable' but is not annotated with @Override.'''
            ]
        )
    }

    @Test
    void testNoViolationsInInnerAndOuterClasses() {
        assertNoViolations '''
            import java.util.concurrent.Callable;

            class OuterClassWithViolation implements Runnable {

                static class InnerClassWithViolation {
                    void run() {
                    }
                }

                @Override
                void run() {
                }

            }
        '''
    }

    @Test
    void testViolationsInAnonymousInnerClass() {
        final SOURCE = '''
            class OuterClass {
                Runnable runnable

                OuterClass() {
                    runnable = new Runnable() {
                        void run() {
                        }
                    }
                }
            }
        '''

        assertSingleViolation(SOURCE, 7, 'void run() {',
            '''Method 'run' is overriding a method in 'java.lang.Runnable' but is not annotated with @Override.''')
    }

    @Test
    void testDoesNotReportViolationsForMethodsWithDefaultArgsForWhichNotAllSignaturesOverrideMethods() {
        assertNoViolations '''
            class SuperClass {
                String superClassMethod(String value) {
                }
            }

            class ValidClass extends SuperClass {
                void superClassMethod(String value = "") {
                }
            }
        '''
    }

    @Test
    void testViolationsForMethodsWithDefaultArgsForWhichAllSignaturesOverrideMethods() {
        final SOURCE = '''
            interface FirstInterface {
                void run(String value)
            }

            interface SecondInterface {
                void run(String first, String second)
            }

            class InvalidClass implements Runnable, FirstInterface, SecondInterface {
                void run(String first = "", String second = "") {
                }
            }
        '''

        assertSingleViolation(SOURCE, 11, 'void run(String first = "", String second = "") {',
            '''Method 'run' is overriding a method in 'FirstInterface', 'SecondInterface', 'java.lang.Runnable' but is not annotated with @Override.''')
    }

    @Override
    protected MissingOverrideAnnotationRule createRule() {
        new MissingOverrideAnnotationRule()
    }
}
