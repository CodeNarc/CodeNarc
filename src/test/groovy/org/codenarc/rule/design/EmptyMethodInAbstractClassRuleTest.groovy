/*
 * Copyright 2010 the original author or authors.
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
package org.codenarc.rule.design

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for EmptyMethodInAbstractClassRule
 *
 * @author Hamlet D'Arcy
 */
class EmptyMethodInAbstractClassRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyMethodInAbstractClass'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            abstract class MyClass {
                abstract def method1()

                def method2() {
                    return "value"
                }

                private void method3() {
                    // private is OK
                }
                private void method4() {
                    return null // private is OK
                }

                void method5() {
                    println '...'   // has implementation
                }
                def method6() {
                    "value" // implicit return
                }
            }

            class MyOtherClass {
                void method1() {
                    // OK because not in abstract class
                }
                private void method4() {
                    return null // OK because not in abstract class
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testReturnVoid() {
        final SOURCE = '''
            abstract class MyClass {
                void couldBeAbstract() {
                    // Should be abstract method
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'void couldBeAbstract()', 'The method couldBeAbstract in abstract class MyClass is empty. Consider making it abstract')
    }

    @Test
    void testReturnNull() {
        final SOURCE = '''
            abstract class MyClass {
                def couldBeAbstract() {
                    return null  // Should be abstract method
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def couldBeAbstract()', 'The method couldBeAbstract in abstract class MyClass contains no logic. Consider making it abstract')
    }

    @Test
    void testReturnNullImplicitReturn() {
        final SOURCE = '''
            abstract class MyClass {
                def couldBeAbstract() {
                    null  // Should be abstract method
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def couldBeAbstract()', 'The method couldBeAbstract in abstract class MyClass contains no logic. Consider making it abstract')
    }

    protected Rule createRule() {
        new EmptyMethodInAbstractClassRule()
    }
}
