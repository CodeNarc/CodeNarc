/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.size

import org.codenarc.rule.Rule
import org.junit.Test
import org.codenarc.rule.AbstractRuleTestCase

import static org.codenarc.test.TestUtil.shouldFail

/**
 * Tests for ParameterCountRule
 *
 * @author Maciej Ziarko
 */
class ParameterCountRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'ParameterCount'
    }

    @Test
    void testSetMaxParameter_negativeInteger() {
        shouldFail(IllegalArgumentException) {
            rule.maxParameters = -1
        }
    }

    @Test
    void testSetMaxParameter_zero() {
        shouldFail(IllegalArgumentException) {
            rule.maxParameters = 0
        }
    }

    @Test
    void testNoViolations_defaultMaxParameter() {
        assertNoViolations('''
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3, int arg4) {
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5) {
                }
            }
        ''')
    }

    @Test
    void testNoViolations_customMaxParameter() {
        rule.maxParameters = 3
        assertNoViolations('''
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3) {
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3) {
                }
            }
        ''')
    }

    @Test
    void testSingleViolation_defaultMaxParameter() {
        assertInlineViolations("""
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3, int arg4) {
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) { ${violation('method TestClass.someMethod')}
                }
            }
        """)
    }

    @Test
    void testSingleViolation_customMaxParameter() {
        rule.maxParameters = 3
        assertInlineViolations("""
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3, int arg4) { ${violation('constructor of class TestClass')}
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3) {
                }
            }
        """)
    }

    @Test
    void testMultipleViolations_defaultMaxParameter() {
        assertInlineViolations("""
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) { ${violation('constructor of class TestClass')}
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) { ${violation('method TestClass.someMethod')}
                }
            }
        """)
    }

    @Test
    void testMultipleViolations_customMaxParameter() {
        rule.maxParameters = 3
        assertInlineViolations("""
            class TestClass {

                TestClass() {
                }

                TestClass(int arg1) {
                }

                TestClass(int arg1, int arg2, int arg3, int arg4) { ${violation('constructor of class TestClass')}
                }

                void someMethod() {
                }

                void someMethod(int arg1, int arg2, int arg3, int arg4, int arg5) { ${violation('method TestClass.someMethod')}
                }
            }
        """)
    }

    private String violation(String name) {
        return inlineViolation("Number of parameters in ${name} exceeds maximum allowed (${rule.maxParameters}).")
    }

    protected Rule createRule() {
        new ParameterCountRule()
    }
}
