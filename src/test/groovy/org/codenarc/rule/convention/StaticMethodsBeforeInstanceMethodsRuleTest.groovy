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
import org.junit.jupiter.api.Test

/**
 * Tests for StaticMethodsBeforeInstanceMethodsRule
 *
 * @author Chris Mair
 * @author Peter Thomas
 */
class StaticMethodsBeforeInstanceMethodsRuleTest extends AbstractRuleTestCase<StaticMethodsBeforeInstanceMethodsRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'StaticMethodsBeforeInstanceMethods'
        assert rule.ignoreMethodNames == null
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
            [line:7, source:'static final String staticMethod2(int id) { }', message:'public static method staticMethod2 in class MyClass is declared after a public instance method'],
            [line:11, source:'protected static staticMethod3() { }', message:'protected static method staticMethod3 in class MyClass is declared after a protected instance method'],
            [line:16, source:'private static staticMethod4() { }', message:'private static method staticMethod4 in class MyClass is declared after a private instance method'])
    }

    @Test
    void test_Script_NoViolations() {
        final SOURCE = ''' #!/usr/bin/groovy

            import acme.Utils

            static boolean call() {
                new Utils().isSomething()
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_StaticMethodsAboveInstanceMethodsByVisibility_IgnoreMatched_NoViolations() {
        final SOURCE = '''
            class MyClass {
                // Public
                public static int staticMethod1() { }
                static final String staticMethod2(int id) { }
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                public String method1() { }
                int method2() { }

                // Protected
                protected static staticMethod3() { }
                protected String getStringValue2() { }
                protected void setStringValue2(String value) { }
                protected String method3() { }

                // Private
                private static staticMethod4() { }
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                private int method4() { }
                int method5() { }
            }
        '''
        rule.ignoreMethodNames = 'get*,set*'
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InstanceMethodsAboveStaticMethodsByVisibility_IgnoreMatched_NoViolations() {
        final SOURCE = '''
            class MyClass {
                // Getters and Setters
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                public String getStringValue1() { }
                public void setStringValue1(String value) { }
                String getStringValue2() { }
                void setStringValue2(String value) { }

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
        rule.ignoreMethodNames = 'get*,set*'
        assertNoViolations(SOURCE)
    }

    @Test
    void test_InstanceMethodsAboveStaticMethodsByVisibility_IgnoreNotMatched_Violations() {
        final SOURCE = '''
            class MyClass {
                // Getters and Setters
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                public String getStringValue1() { }
                public void setStringValue1(String value) { }
                String getStringValue2() { }
                void setStringValue2(String value) { }

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
        rule.ignoreMethodNames = 'unmatchedGet*,unmatchedSet*'
        assertViolations(SOURCE,
            [line:12, source:'public static int staticMethod1() { }', message:'public static method staticMethod1 in class MyClass is declared after a public instance method'],
            [line:13, source:'static final String staticMethod2(int id) { }', message:'public static method staticMethod2 in class MyClass is declared after a public instance method'],
            [line:22, source:'private static staticMethod4() { }', message:'private static method staticMethod4 in class MyClass is declared after a private instance method'])
    }

    @Test
    void test_InstanceMethodsAboveStaticMethodsByVisibility_PartialIgnoreMatch_Violations() {
        final SOURCE = '''
            class MyClass {
                // Getters and Setters
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                protected String getStringValue2() { }
                protected void setStringValue2(String value) { }
                public String getStringValue1() { }
                public void setStringValue1(String value) { }

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
        rule.ignoreMethodNames = 'get*,setPrivateStringValue1'
        assertViolations(SOURCE,
            [line:12, source:'public static int staticMethod1() { }', message:'public static method staticMethod1 in class MyClass is declared after a public instance method'],
            [line:13, source:'static final String staticMethod2(int id) { }', message:'public static method staticMethod2 in class MyClass is declared after a public instance method'],
            [line:18, source:'protected static staticMethod3() { }', message:'protected static method staticMethod3 in class MyClass is declared after a protected instance method'])
    }

    @Test
    void test_InstanceMethodsBetweenStaticMethods_IgnoreMatched_Violations() {
        final SOURCE = '''
            class MyClass {
                // Getters and Setters
                private String getPrivateStringValue1() { }
                private void setPrivateStringValue1(String value) { }
                protected String getStringValue2() { }
                protected void setStringValue2(String value) { }

                // Public
                public static int staticMethod1() { }
                public String getStringValue1() { }
                public void setStringValue1(String value) { }
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
        rule.ignoreMethodNames = 'get*,set*'
        assertViolations(SOURCE,
            [line:13, source:'static final String staticMethod2(int id) { }', message:'public static method staticMethod2 in class MyClass is declared after a public instance method'])
    }

    @Override
    protected StaticMethodsBeforeInstanceMethodsRule createRule() {
        new StaticMethodsBeforeInstanceMethodsRule()
    }

}
