/*
 * Copyright 2009 the original author or authors.
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

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for AbcComplexityRule
 *
 * @author Chris Mair
  */
class AbcComplexityRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AbcComplexity'
        assert rule.maxMethodComplexity == 60
        assert rule.maxClassAverageMethodComplexity == 60
    }

    void testApplyTo_ClassWithNoMethods() {
        final SOURCE = '''
            class MyClass {
                def myValue = 23
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_EqualToClassAndMethodThreshold() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    switch(x) {
                        case 1: break
                        case 3: break
                    }
                }
            }
        '''
        rule.maxMethodComplexity = 2
        rule.maxClassAverageMethodComplexity = 2
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_ExceedsMaxMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a = 1; b = 2; c = 3
                }
            }
        '''
        rule.maxMethodComplexity = 2
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '3.0'])
    }

    void testApplyTo_SingleClosureField_ExceedsMaxMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myClosure = { a.someMethod(); b.aProperty }
            }
        '''
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, 3, 'def myClosure', ['myClosure', '2.0'])
    }

    void testApplyTo_TwoMethodsExceedsMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod1() {
                    a && b && c
                }

                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    a || b || c || d || e || f || g
                }
            }
        """
        rule.maxMethodComplexity = 2
        assertTwoViolations(SOURCE,
                3, 'def myMethod1()', ['myMethod1', '3'],
                9, 'def myMethod3()', ['myMethod3', '7'])
    }

    void testApplyTo_Class_ExceedsMaxClassAverageMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassAverageMethodComplexity = 5
        assertSingleViolation(SOURCE, 2, 'class MyClass', ['MyClass', '6'])
    }

    void testApplyTo_Class_ZeroMaxClassAverageMethodComplexity_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassAverageMethodComplexity = 0
        assertNoViolations(SOURCE)
    }

    void testApplyTo_ClassAndMethod_ExceedThreshold() {
        final SOURCE = """
            class MyClass {
                def myMethod1() {
                    a = 1; b = 2
                }
                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    a || b || c || d || e || f
                }
            }
        """
        rule.maxMethodComplexity = 4.5
        rule.maxClassAverageMethodComplexity = 1.9
        assertTwoViolations(SOURCE,
                2, 'class MyClass', ['MyClass', '2.2'],
                8, 'def myMethod3()', ['myMethod3', '6'])
    }

    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.ignoreMethodNames = 'myMethod'
        rule.maxMethodComplexity = 1
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.ignoreMethodNames = 'otherMethod'
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '6'])
    }

    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
                def myClosure = { a && b && c }
                def otherClosure = { a ?: (b ?: c) }
                def myMethod2() {
                    a || b || c
                }
            }
        '''
        rule.ignoreMethodNames = 'myM*d*,otherC??su*'
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, 6, 'def myClosure', ['myClosure', '3'])
    }

    void testApplyTo_NoExplicitClass_StillChecksMethods() {
        final SOURCE = '''
            def myMethod() {
                a && b && c && d && e && f
            }
        '''
        rule.maxMethodComplexity = 1
        rule.maxClassAverageMethodComplexity = 1
        assertSingleViolation(SOURCE, 2, 'def myMethod()', ['myMethod', '6'])
    }

    void testApplyTo_NoExplicitMethodDefinition_ChecksAsRunMethod() {
        final SOURCE = '''
            if (isReady) {
                println a && b && c && d && e
            }
        '''
        rule.maxMethodComplexity = 1
        rule.maxClassAverageMethodComplexity = 1
        assertSingleViolation(SOURCE, null, null, ['run', '6'])
    }

    protected Rule createRule() {
        new AbcComplexityRule()
    }
}