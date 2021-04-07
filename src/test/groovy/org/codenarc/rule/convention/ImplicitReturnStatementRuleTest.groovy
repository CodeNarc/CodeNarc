/*
 * Copyright 2020 the original author or authors.
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
package org.codenarc.rule.convention

import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

/**
 * Tests for ImplicitReturnStatementRule
 *
 * @author Chris Mair
 */
class ImplicitReturnStatementRuleTest extends AbstractRuleTestCase<ImplicitReturnStatementRule> {

    @Test
    void test_RuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ImplicitReturnStatement'
    }

    @Test
    void test_MethodsWithReturnStatements_NoViolations() {
        final SOURCE = '''
            class MyClass {
                String baseName = "base"
                String getName(String id) {
                    return baseName + "." + id
                }

                boolean example() { return true }

                boolean longerExample() {
                    if (baseName == null) {
                        return true
                    }
                    if (baseName == 'abc' && System.getProperty('test')) {
                        println 'abc'
                        return true
                    }
                    return false
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_VoidMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                void example() { println 'ok' }
                void example2() { 'abc' }
                void example3() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_AbstractMethods_NoViolations() {
        final SOURCE = '''
            abstract class AbstractClass {
                abstract int example()
                abstract String example2(int count)
                abstract void example3(String name, long id)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InterfaceMethods_NoViolations() {
        final SOURCE = '''
            interface SomeService {
                int example()
                String example2(int count)
                void example3(String name, long id)
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_TryCatch_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int execute() {
                    try {
                        return executeTask(new Date())
                    }
                    catch(AnalyzerException e) {
                        throw new Exception('Task failed: ' + e.getMessage(), e)
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Switch_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int myMethod() {
                    boolean flag = false
                    switch(flag) {
                        case true:
                            return 99
                        case false:
                            return 11
                    }
}            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_If_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int execute() {
                    if (value() > 1) {
                        return 99
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_While_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int execute() {
                    println 'execute'
                    while(true) {
                        if (value() > 1) {
                            return 99
                        }
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    // TODO Add test for DoWhile statement when we upgrade to a Groovy that supports it

    @Test
    void test_For_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int execute() {
                    println 'execute'
                    for (int i=0; i<10; i++) {
                        if (value() > 1) {
                            return 99
                        }
                    }
                }
                int execute2() {
                    for(int i in [0, 1, 2]) {
                        println i
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Throw_NoViolations() {
        final SOURCE = '''
            class MyClass {
                int throwAnException() {
                    throw new Exception('Error')
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_DefMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def example() { println 'ok' }
                def example2() { 'abc' }
                def example3() {
                    doStuff()
                    99L
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_MethodsWithoutReturnStatements_Violations() {
        final SOURCE = '''
            class MyClass {
                String baseName = "base"
                String getName(String id) {
                    baseName + "." + id
                }

                boolean example() { true }

                protected int longerExample() {
                    if (baseName == null) {
                        return 0
                    }
                    if (baseName == 'abc' && System.getProperty('test')) {
                        println 'abc'
                        return 3
                    }
                    99
                }

                private Object emptyMethod() { }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source:'String getName(String id)', message:'The method getName in class MyClass is missing an explicit return'],
            [line:8, source:'boolean example()', message:'The method example in class MyClass is missing an explicit return'],
            [line:10, source:'protected int longerExample()', message:'The method longerExample in class MyClass is missing an explicit return'],
            [line:21, source:'private Object emptyMethod()', message:'The method emptyMethod in class MyClass is missing an explicit return'])
    }

    @Override
    protected ImplicitReturnStatementRule createRule() {
        new ImplicitReturnStatementRule()
    }
}
