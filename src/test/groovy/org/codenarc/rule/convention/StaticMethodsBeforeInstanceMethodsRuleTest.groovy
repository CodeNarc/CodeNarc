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
 * Tests for StaticMethodsBeforeInstanceMethodsRule
 *
 * @author Chris Mair
 */
class StaticMethodsBeforeInstanceMethodsRuleTest extends AbstractRuleTestCase<StaticMethodsBeforeInstanceMethodsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'StaticMethodsBeforeInstanceMethods'
    }

    @Test
    void test_AllStaticMethodsAboveAllInstanceMethods_NoViolations() {
        final SOURCE = '''
            class MyClass {
                public static int staticMethod1() { }
                static final String staticMethod2(int id) { }
                static staticMethod3() { }

                public String method1() { }
                protected String method2() { }
                private int method3() { }
                int method4() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_StaticMethodsAboveInstanceMethodsByVisibility_NoViolations() {
        final SOURCE = '''
            class MyClass {
                // Public
                public static int staticMethod1() { }
                static final String staticMethod2(int id) { }
                public String method1() { }
                int method2() { }

                // Protected
                protected static staticMethod3() { }
                protected String method3() { }

                // Private
                private static staticMethod4() { }
                private int method4() { }
                int method5() { }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolations() {
        final SOURCE = '''
            class MyClass {
                // Public
                public static int staticMethod1() { }
                public String method1() { }
                int method2() { }
                static final String staticMethod2(int id) { }

                // Protected
                protected String method3() { }
                protected static staticMethod3() { }

                // Private
                private int method4() { }
                private int method5() { }
                private static staticMethod4() { }
                private String method5() { }
            }
        '''
        assertViolations(SOURCE,
            [lineNumber:7, sourceLineText:'static final String staticMethod2(int id) { }', messageText:'public static method staticMethod2 in class MyClass is declared after a public instance method'],
            [lineNumber:11, sourceLineText:'protected static staticMethod3() { }', messageText:'protected static method staticMethod3 in class MyClass is declared after a protected instance method'],
            [lineNumber:16, sourceLineText:'private static staticMethod4() { }', messageText:'private static method staticMethod4 in class MyClass is declared after a private instance method'])
    }

    @Override
    protected StaticMethodsBeforeInstanceMethodsRule createRule() {
        new StaticMethodsBeforeInstanceMethodsRule()
    }

}
