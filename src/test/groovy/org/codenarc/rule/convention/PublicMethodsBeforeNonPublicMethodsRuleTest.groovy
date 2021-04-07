/*
 * Copyright 2018 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for PublicMethodsBeforeNonPublicMethodsRule
 *
 * @author Chris Mair
 */
class PublicMethodsBeforeNonPublicMethodsRuleTest extends AbstractRuleTestCase<PublicMethodsBeforeNonPublicMethodsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'PublicMethodsBeforeNonPublicMethods'
    }

    @Test
    void test_AllPublicMethodsAboveAllNonPublicMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                public static int staticMethod1() { }
                static final String staticMethod2(int id) { }

                public String method1() { }
                String method2() { }

                protected static String staticMethod3() { }

                protected String method3() { }

                private static int staticMethod4() { }

                private int method4() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_PublicMethodAfterProtected_Violations() {
        final SOURCE = '''
            class MyClass {
                public static int staticMethod1() { }

                protected String method1() { }

                static final String staticMethod2() { }
                public String method2() { }

                private int method3(int id) { }
            }
        '''
        assertViolations(SOURCE,
            [line:7, source:'static final String staticMethod2() { }', message:'public method staticMethod2 in class MyClass is declared after a non-public method'],
            [line:8, source:'public String method2() { }', message:'public method method2 in class MyClass is declared after a non-public method'])
    }

    @Test
    void test_PublicMethodAfterPrivate_Violations() {
        final SOURCE = '''
            class MyClass {
                private String method1() { }

                static final String staticMethod1() { }
                public String method2() { }
            }
        '''
        assertViolations(SOURCE,
                [line:5, source:'static final String staticMethod1() { }', message:'public method staticMethod1 in class MyClass is declared after a non-public method'],
                [line:6, source:'public String method2() { }', message:'public method method2 in class MyClass is declared after a non-public method'])
    }

    @Test
    void test_StaticInitializer_NoViolations() {
        final SOURCE = '''
            class MyTest {
                static final String TEST

                static {
                    TEST = 'test\'
                }

                String hello() {
                    return TEST
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InstanceInitializer_NoViolations() {
        final SOURCE = '''
            class MyTest {
                private count

                {
                    count = 99
                }

                String hello() {
                    return 'hello: ' + count
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected PublicMethodsBeforeNonPublicMethodsRule createRule() {
        new PublicMethodsBeforeNonPublicMethodsRule()
    }
}
