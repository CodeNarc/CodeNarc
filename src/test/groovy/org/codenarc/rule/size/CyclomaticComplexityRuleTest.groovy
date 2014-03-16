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
import org.junit.Test

/**
 * Tests for CyclomaticComplexityRule
 *
 * @author Chris Mair
  */
class CyclomaticComplexityRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CyclomaticComplexity'
        assert rule.maxMethodComplexity == 20
        assert rule.maxClassComplexity == 0
        assert rule.maxClassAverageMethodComplexity == 20
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
    void testApplyTo_SingleMethod_EqualToMaxMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e
                }
            }
        '''
        rule.maxMethodComplexity = 5
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleMethod_ExceedsMaxMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxMethodComplexity = 5
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '6'])
    }

    @Test
    void testSuppressWarningsOnClass() {
        final SOURCE = '''
            @SuppressWarnings('CyclomaticComplexity')
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxMethodComplexity = 5
        assert manuallyApplyRule(SOURCE).size() == 0
    }

    @Test
    void testSuppressWarningsOnMethod() {
        final SOURCE = '''
            class MyClass {
                @SuppressWarnings('CyclomaticComplexity')
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxMethodComplexity = 5
        assert manuallyApplyRule(SOURCE).size() == 0
    }

    @Test
    void testApplyTo_SingleClosureField_ExceedsMaxMethodComplexity() {
        final SOURCE = '''
            class MyClass {
                def myClosure = { a && b && c && d && e }
            }
        '''
        rule.maxMethodComplexity = 2
        assertSingleViolation(SOURCE, 3, 'def myClosure', ['myClosure', '5'])
    }

    @Test
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
        assertTwoViolations(SOURCE, 3, 'def myMethod1()', ['myMethod1', '3'], 9, 'def myMethod3()', ['myMethod3', '7'])
    }

    @Test
    void testApplyTo_Class_ExceedsMaxAverageClassComplexity() {
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

    @Test
    void testApplyTo_Class_ExceedsMaxClassComplexity() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
            }
        '''
        rule.maxClassComplexity = 5
        assertSingleViolation(SOURCE, 2, 'class MyClass', ['MyClass', '6'])
    }

    @Test
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

    @Test
    void testApplyTo_ClassAndMethod_ExceedThreshold() {
        final SOURCE = '''
            class MyClass {
                def myMethod1() {
                    a && b && c
                }
                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    a || b || c || d || e || f
                }
            }
        '''
        rule.maxMethodComplexity = 5
        rule.maxClassComplexity = 9
        rule.maxClassAverageMethodComplexity = 3
        assertViolations(SOURCE,
                [lineNumber:2, sourceLineText:'class MyClass', messageText:['MyClass', '3.3']],
                [lineNumber:2, sourceLineText:'class MyClass', messageText:['MyClass', '10']],
                [lineNumber:8, sourceLineText:'def myMethod3()', messageText:['myMethod3', '6']])
    }

    @Test
    void testApplyTo_ClassAndMethods_AtThreshold() {
        final SOURCE = '''
            class MyClass {
                def myMethod1() {
                    a && b && c
                }
                def myClosure = { a ?: (b ?: c) }

                def myMethod2() {
                    a || b || c
                }
            }
        '''
        rule.maxMethodComplexity = 3
        rule.maxClassComplexity = 9
        rule.maxClassAverageMethodComplexity = 3
        assertNoViolations(SOURCE)
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
        rule.maxMethodComplexity = 1
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
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, 3, 'def myMethod()', ['myMethod', '6'])
    }

    @Test
    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    a && b && c && d && e && f
                }
                def myClosure = { a ?: (b ?: c) }
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

    @Test
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

    @Test
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
        new CyclomaticComplexityRule()
    }

}
