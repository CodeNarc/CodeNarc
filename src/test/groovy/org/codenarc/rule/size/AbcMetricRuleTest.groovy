/*
 * Copyright 2012 the original author or authors.
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
import org.junit.Test

/**
 * Tests for AbcMetricRule
 *
 * @author Chris Mair
  */
class AbcMetricRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AbcMetric'
        assert rule.maxMethodAbcScore == 60
        assert rule.maxClassAverageMethodAbcScore == 60
        assert rule.maxClassAbcScore == 0
    }

    @Test
    void testApplyTo_ClassWithNoMethods() {
        final SOURCE = '''
            class MyClass {
                def myValue = 23
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
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
        rule.maxMethodAbcScore = 2
        rule.maxClassAverageMethodAbcScore = 2
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleMethod_ExceedsMaxMethodAbcScore() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a = 1; b = 2; c = 3
                }
            }
        '''
        rule.maxMethodAbcScore = 2
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '3.0'])
    }

    @Test
    void testApplyTo_SingleClosureField_ExceedsMaxMethodAbcScore() {
        final SOURCE = '''
            class MyClass {
                def myClosure = { a.someMethod(); b.aProperty }
            }
        '''
        rule.maxMethodAbcScore = 1
        assertSingleViolation(SOURCE, 3, 'def myClosure', ['myClosure', '2.0'])
    }

    @Test
    void testApplyTo_TwoMethodsExceedsMaxMethodAbcScore() {
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
        rule.maxMethodAbcScore = 2
        assertTwoViolations(SOURCE,
                3, 'def myMethod1()', ['myMethod1', '3'],
                9, 'def myMethod3()', ['myMethod3', '7'])
    }

    @Test
    void testApplyTo_Class_ExceedsMaxClassAverageMethodAbcScore() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassAverageMethodAbcScore = 5
        assertSingleViolation(SOURCE, 2, 'class MyClass', ['MyClass', '6'])
    }

    @Test
    void testApplyTo_Class_ExceedsMaxClassAbcScore() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassAbcScore = 5
        assertSingleViolation(SOURCE, 2, 'class MyClass', ['MyClass', '6'])
    }

    @Test
    void testApplyTo_Class_ZeroMaxClassAverageMethodAbcScore_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassAverageMethodAbcScore = 0
        assertNoViolations(SOURCE)
    }

    @Test
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
        rule.maxMethodAbcScore = 4.5
        rule.maxClassAverageMethodAbcScore = 1.9
        rule.maxClassAbcScore = 6.0
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'class MyClass', messageText:['MyClass', '2.2']],
                [lineNumber:2, sourceLineText:'class MyClass', messageText:['MyClass', '6.4']],
                [lineNumber:8, sourceLineText:'def myMethod3()', messageText:['myMethod3', '6']])
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.ignoreMethodNames = 'myMethod'
        rule.maxMethodAbcScore = 1
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.ignoreMethodNames = 'otherMethod'
        rule.maxMethodAbcScore = 1
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '6'])
    }

    @Test
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
        rule.maxMethodAbcScore = 1
        assertSingleViolation(SOURCE, 6, 'def myClosure', ['myClosure', '3'])
    }

    @Test
    void testApplyTo_NoExplicitClass_StillChecksMethods() {
        final SOURCE = '''
            def myMethod() {
                a && b && c && d && e && f
            }
        '''
        rule.maxMethodAbcScore = 1
        rule.maxClassAverageMethodAbcScore = 1
        assertSingleViolation(SOURCE, 2, 'def myMethod()', ['myMethod', '6'])
    }

    @Test
    void testApplyTo_NoExplicitMethodDefinition_ChecksAsRunMethod() {
        final SOURCE = '''
            if (isReady) {
                println a && b && c && d && e
            }
        '''
        rule.maxMethodAbcScore = 1
        rule.maxClassAverageMethodAbcScore = 1
        assertSingleViolation(SOURCE, null, null, ['run', '6'])
    }

    protected Rule createRule() {
        new AbcMetricRule()
    }
}
