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
package org.codenarc.rule.groovyism

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for GetterMethodCouldBePropertyRule
 *
 * @author Hamlet D'Arcy
 * @author Chris Mair
 */
class GetterMethodCouldBePropertyRuleTest extends AbstractRuleTestCase<GetterMethodCouldBePropertyRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'GetterMethodCouldBeProperty'
        assert rule.ignoreMethodsWithOverrideAnnotation == false
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            class MyClass {
                static final VALUE = 'value'
                private static final int OTHER = 99
                final String something = 'something'  // this is cleaner
                final String somethingElse = VALUE      // this is cleaner
                final String someClass = String      // this is cleaner

                @Override
                String getSomething(def parameter) {
                    //not a java getter
                }

                @Override
                String getSomethingElse() {
                    performSomeWork() // not a simple one liner
                    'something else'
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIgnoreProtectedGetterMethods() {
        final SOURCE = '''
            class MyClass {
                @Override
                protected int getValue() { 123 }

                @Override
                protected getValue2() { return 'abc' }

            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testAnonymousInnerClass_ConstantFieldReturn() {
        final SOURCE = '''
            interface IResult {
                int getIntValue()
            }

            class MyClass {
                private static final CONSTANT_RESULT = 5

                IResult operate(int parameter) {
                    return new IResult() {
                        @Override
                        int getIntValue() {
                            CONSTANT_RESULT
                        }
                    }
                }
            }
            '''
        assertSingleViolation(SOURCE, 12,
                'int getIntValue()',
                "The method 'getIntValue ' in class MyClass\$1 can be expressed more simply as the field declaration\nfinal int intValue = CONSTANT_RESULT")
    }

    @Test
    void testAnonymousInnerClass_ConstantFieldReturnExplicit() {
        final SOURCE = '''
            interface IResult {
                int getIntValue()
            }

            class MyClass {
                private static final CONSTANT_RESULT = 5

                IResult operate(int parameter) {
                    return new IResult() {
                        @Override
                        int getIntValue() {
                            return CONSTANT_RESULT
                        }
                    }
                }
            }
            '''
        assertSingleViolation(SOURCE, 12,
                'int getIntValue()',
                "The method 'getIntValue ' in class MyClass\$1 can be expressed more simply as the field declaration\nfinal int intValue = CONSTANT_RESULT")
    }

    @Test
    void testConstantFieldReturn() {
        final SOURCE = '''
            class MyClass {
                private static final VALUE = 'abc'
                @Override
                String getSomething() {
                    VALUE         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 5,
                'String getSomething()',
                "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = VALUE")
    }

    @Test
    void testConstantFieldReturnExplicit() {
        final SOURCE = '''
            class MyClass {
                private static final VALUE = 'abc'
                @Override
                String getSomething() {
                    return VALUE         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 5,
                'String getSomething()',
                "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = VALUE")
    }

    @Test
    void testConstantLiteralReturn() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void testConstantLiteralReturnExplicit() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    return 'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void test_ReturnAClass() {
        final SOURCE = '''
            class MyClass {
                @Override
                Class getSomething() {
                    String         // this could be simplified
                }

                Class getSomeClass() {
                    return Integer         // this could be simplified
                }
            }
        '''
        assertViolations(SOURCE,
            [line:4, source: 'Class getSomething()', message: "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal Class something = String"],
            [line:8, source: 'Class getSomeClass()', message: "The method 'getSomeClass ' in class MyClass can be expressed more simply as the field declaration\nfinal Class someClass = Integer"]
        )
    }

    @Test
    void testConstantExplicitReturn() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {
                    return 'something'         // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'String getSomething()',
            "The method 'getSomething ' in class MyClass can be expressed more simply as the field declaration\nfinal String something = 'something'")
    }

    @Test
    void test_StaticFinalField() {
        final SOURCE = '''
            class MyClass {
                protected static final value = 'value'

                @Override
                String getSomethingElse() {
                    value       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 6,
            'String getSomethingElse()',
            "The method 'getSomethingElse ' in class MyClass can be expressed more simply as the field declaration\nfinal String somethingElse = value")
    }

    @Test
    void test_StaticFinalField_ExplicitReturn() {
        final SOURCE = '''
            class MyClass {
                static final VALUE = 'value'

                @Override
                String getSomethingElse() {
                    return VALUE       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 6,
            'String getSomethingElse()',
            "The method 'getSomethingElse ' in class MyClass can be expressed more simply as the field declaration\nfinal String somethingElse = VALUE")
    }

    @Test
    void test_StaticNonFinalField_NoViolations() {
        final SOURCE = '''
            class MyClass {
                static value = 'value'

                String getSomethingElse() {
                    value               // We can't be sure this value won't change
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_StaticNonFinalField_AnotherExample() {
        final SOURCE = '''
            static class PingCommand extends PingIntegTest.PingCommand {
                static commandNotAllowedEventReceived
                static restrictionChain

                @Override
                RestrictionChainElement getRestrictionChain() {
                    restrictionChain
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_StaticGetterMethod() {
        final SOURCE = '''
            class MyClass {
                static final VALUE = 'value'
                static String getValue() {
                    VALUE       // this could be simplified
                }
            }
        '''
        assertSingleViolation(SOURCE, 4,
            'static String getValue()',
            "The method 'getValue ' in class MyClass can be expressed more simply as the field declaration\nstatic final String value = VALUE")
    }

    @Test
    void test_ignoreMethodsWithOverrideAnnotation() {
        final SOURCE = '''
            class MyClass {
                @Override
                String getSomething() {         // ignored
                    'something'
                }

                String getSomethingElse() {     // violation
                    'something else'
                }
            }
        '''
        rule.ignoreMethodsWithOverrideAnnotation = true
        assertSingleViolation(SOURCE, 8,
                'String getSomethingElse()',
                "The method 'getSomethingElse ' in class MyClass can be expressed more simply as the field declaration\nfinal String somethingElse = 'something else'")
    }

    @Override
    protected GetterMethodCouldBePropertyRule createRule() {
        new GetterMethodCouldBePropertyRule()
    }
}
