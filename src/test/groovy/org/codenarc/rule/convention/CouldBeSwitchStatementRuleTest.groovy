/*
 * Copyright 2017 the original author or authors.
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
 * Tests for CouldBeSwitchStatementRule
 *
 * @author Jenn Strater
 */
class CouldBeSwitchStatementRuleTest extends AbstractRuleTestCase<CouldBeSwitchStatementRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'CouldBeSwitchStatement'
    }

    @Test
    void testNoViolations() {
        final SOURCE = '''
            if (x == 1) {
                y = x
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationsWithOnlyTwoMatchingConditions() {
        final SOURCE = '''
            if (x == 1) {
                y = x
            } else if (x == 2) {
                y = x * 2
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testViolationsWithThreeIfsAndAnElse() {
        final SOURCE = '''
            if (x == 1) {
                y = x
            } else if (x == 2) {
                y = x * 2
            } else if (x == 3) {
                y = x * 3
            } else {
                y = 0
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (x == 1) {', rule.errorMessage)
    }

    @Test
    void testSingleViolationWithThreeIfsNoElse() {
        final SOURCE = '''
            if (x == 1) {
               y = x
            }
            if (x == 2) {
               y = x * 2
            }
            if (x == 3) {
               y = x * 3
            }
        '''
        assertSingleViolation(SOURCE, 2, 'if (x == 1) {', rule.errorMessage)
    }

    @Test
    void testDifferentLeftExpressionTypesDoNotCauseViolation() {
        final SOURCE = '''
        if (x == 1) {
            y = x
        } else if (x.value == 2) {
            y = x * 2
        } else if (x instanceof String) {
            y = x * 3
        } else {
            y = 0
        }'''

        assertNoViolations(SOURCE)
    }

    @Test
    void testMixedLeftExpressionTypesDoNotCauseViolation() {
        final SOURCE = '''
        if (x == 1) {
            y = x
        } else if (x == 2) {
            y = x * 2
        } else if (x.find { it == 3}) {
            y = x * 3
        } else {
            y = 0
        }'''

        assertNoViolations(SOURCE)
    }

    @Test
    void testUnsupportedExpressionTypes() {
        final SOURCE = '''
            if (!x && y) {
                doSomething()
            } else if (!x && z) {
                doSomethingElse()
            } else if (!x && i) {
                doAnotherThing()
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testMixedSupportedAndUnsupportedOperationTypes() {
        final SOURCE = '''
            if (x.is(y)) {
                doSomething()
            } else if (x == z) {
                doSomethingElse()
            } else if (!x && i) {
                doAnotherThing()
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testVariationsOnPropertyExpression() {
        final SOURCE = '''
            if (p.value instanceof Integer) {
                x = p.value * 2
            } else if (p.otherValue instanceof String) {
                x = p.otherValue * 2
            } else if (p.value instanceof Boolean) {
                x = !p.value
            }

             if (x.value instanceof Integer) {
                x = x.value * 2
            } else if (y.value instanceof String) {
                x = y.otherValue * 2
            } else if (z.value instanceof Boolean) {
                x = !z.value
            }
        '''

        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationAcrossClasses() {
        final SOURCE = '''
            class a {
                def method() {
                    if(x == 1) {
                        y = x
                    } else if (x == 2) {
                        y = x * 2
                    }
                }
            }
            class b {
                def otherMethod() {
                    if (x == 3) {
                        y = x * 3
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationAcrossMethodsInSameClass() {
        final SOURCE = '''
            class a {
                def method() {
                    if(x == 1) {
                        y = x
                    } else if (x == 2) {
                        y = x * 2
                    }
                }
                def otherMethod() {
                    if (x == 3) {
                        y = x * 3
                    }
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            if (x == 1) {
               y = x
            } else if (x == 2) {
               y = x * 2
            } else if (x == 3) {
               y = x * 3
            } else {
               y = 0
            }

            if (y instanceof Integer) {
               x = y + 1
            }
            if (y instanceof String) {
               x = y + '1'
            } else if (y instanceof Boolean) {
               x = !y
            } else {
               x = null
            }

            if (p.value instanceof Integer) {
                x = p.value * 2
            } else if (p.value instanceof String) {
                x = p.value + '1'
            } else if (p.value instanceof Boolean) {
                x = !p.value
            }
        '''
        assertViolations(SOURCE,
            [line:2, source: 'if (x == 1) {', message: rule.errorMessage],
            [line:12, source: 'if (y instanceof Integer) {', message: rule.errorMessage],
            [line:23, source: 'if (p.value instanceof Integer) {', message: rule.errorMessage])
    }

    @Override
    protected CouldBeSwitchStatementRule createRule() {
        new CouldBeSwitchStatementRule()
    }
}
