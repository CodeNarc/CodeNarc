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
 * @version $Revision$ - $Date$
 */
class AbcComplexityRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'AbcComplexity'
        assert rule.maxMethodComplexity == 60
        assert rule.maxClassAverageMethodComplexity == 60
    }

    void testApplyTo_ClassWithNoMethods() {
        final SOURCE = """
            class MyClass {
                def myValue = 23
            }
        """
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_EqualToClassAndMethodThreshold() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    switch(x) {
                        case 1: break
                        case 3: break
                    }
                }
            }
        """
        rule.maxMethodComplexity = 2
        rule.maxClassAverageMethodComplexity = 2
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_ExceedsMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    a = 1; b = 2; c = 3
                }
            }
        """
        rule.maxMethodComplexity = 2
        // TODO include line number and source line
        assertSingleViolation(SOURCE, null, null, ['myMethod', '3.0'])
    }

    void testApplyTo_SingleClosureField_ExceedsMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myClosure = { a.someMethod(); b.aProperty }
            }
        """
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, null, null, ['myClosure', '2.0'])
    }

    void testApplyTo_TwoMethodsExceedsMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod1() {
                    return a && b && c
                }

                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    return a || b || c || d || e || f || g
                }
            }
        """
        rule.maxMethodComplexity = 2
        assertTwoViolations(SOURCE, null, null, ['myMethod1', '3'], null, null, ['myMethod3', '7'])
    }

    void testApplyTo_Class_ExceedsMaxAverageClassComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e && f
                }
            }
        """
        rule.maxClassAverageMethodComplexity = 5
        assertSingleViolation(SOURCE, null, null, ['MyClass', '6'])
    }

    void testApplyTo_ClassAndMethod_ExceedThreshold() {
        final SOURCE = """
            class MyClass {
                def myMethod1() {
                    return a = 1; b = 2
                }
                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    return a || b || c || d || e || f
                }
            }
        """
        rule.maxMethodComplexity = 4.5
        rule.maxClassAverageMethodComplexity = 1.9
        assertTwoViolations(SOURCE,
                null, null, ['myMethod3', '6'],
                null, null, ['MyClass', '2.2'])
    }

    void testApplyTo_IgnoreMethodNames_MatchesSingleName() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e && f
                }
            }
        """
        rule.ignoreMethodNames = 'myMethod'
        rule.maxMethodComplexity = 1
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreMethodNames_MatchesNoNames() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e && f
                }
            }
        """
        rule.ignoreMethodNames = 'otherMethod'
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, null, null, ['myMethod', '6'])
    }

    void testApplyTo_IgnoreMethodNames_MultipleNamesWithWildcards() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e && f
                }
                def myClosure = { a && b && c }
                def otherClosure = { a ?: (b ?: c) }
                def myMethod2() {
                    return a || b || c
                }
            }
        """
        rule.ignoreMethodNames = 'myM*d*,otherC??su*'
        rule.maxMethodComplexity = 1
        assertSingleViolation(SOURCE, null, null, ['myClosure', '3'])
    }

    void testApplyTo_NoExplicitClass_StillChecksMethods() {
        final SOURCE = '''
            def myMethod() {
                return a && b && c && d && e && f
            }
        '''
        rule.maxMethodComplexity = 1
        rule.maxClassAverageMethodComplexity = 1
        assertSingleViolation(SOURCE, null, null, ['myMethod', '6'])
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
        return new AbcComplexityRule()
    }

}