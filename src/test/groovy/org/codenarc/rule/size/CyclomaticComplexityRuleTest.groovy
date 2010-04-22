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
 * Tests for CyclomaticComplexityRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CyclomaticComplexityRuleTest extends AbstractRuleTestCase {

    // TODO tests for closure fields

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'CyclomaticComplexity'
        assert rule.maxMethodComplexity == 20
        assert rule.maxClassAverageMethodComplexity == 20
    }

    void testApplyTo_ClassWithNoMethods() {
        final SOURCE = """
            class MyClass {
                def myValue = 23
            }
        """
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_EqualToMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e
                }
            }
        """
        rule.maxMethodComplexity = 5
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleMethod_ExceedsMaxMethodComplexity() {
        final SOURCE = """
            class MyClass {
                def myMethod() {
                    return a && b && c && d && e && f
                }
            }
        """
        rule.maxMethodComplexity = 5
        // TODO include line number and source line
        assertSingleViolation(SOURCE, null, null, ['myMethod', '6'])
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
                    return a && b && c
                }
                def myMethod2(int someValue) { println 'ok' }

                def myMethod3() {
                    return a || b || c || d || e || f
                }
            }
        """
        rule.maxMethodComplexity = 5
        rule.maxClassAverageMethodComplexity = 3
        assertTwoViolations(SOURCE,
                null, null, ['myMethod3', '6'],
                null, null, ['MyClass', '3.3'])
    }

    void testApplyTo_NoClassDefinition() {
        final SOURCE = '''
            if (isReady) {
                println 'ready'
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new CyclomaticComplexityRule()
    }

}