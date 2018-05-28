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
 * Tests for PublicMethodsBeforeNonPublicRule
 *
 * @author Chris Mair
 */
class PublicMethodsBeforeNonPublicRuleTest extends AbstractRuleTestCase<PublicMethodsBeforeNonPublicRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'PublicMethodsBeforeNonPublic'
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
            [lineNumber:7, sourceLineText:'static final String staticMethod2() { }', messageText:'public method staticMethod2 in class MyClass is declared after a non-public method'],
            [lineNumber:8, sourceLineText:'public String method2() { }', messageText:'public method method2 in class MyClass is declared after a non-public method'])
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
                [lineNumber:5, sourceLineText:'static final String staticMethod1() { }', messageText:'public method staticMethod1 in class MyClass is declared after a non-public method'],
                [lineNumber:6, sourceLineText:'public String method2() { }', messageText:'public method method2 in class MyClass is declared after a non-public method'])
    }

    @Override
    protected PublicMethodsBeforeNonPublicRule createRule() {
        new PublicMethodsBeforeNonPublicRule()
    }
}
