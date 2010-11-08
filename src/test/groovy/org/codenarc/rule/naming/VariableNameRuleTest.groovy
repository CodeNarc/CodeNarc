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
package org.codenarc.rule.naming

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.codenarc.util.GroovyVersion

/**
 * Tests for VariableNameRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class VariableNameRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'VariableName'
    }

    void testRegex_DefaultValue() {
        assert 'abc' ==~ rule.regex
        assert 'aXaX123' ==~ rule.regex

        assert !('abc_def' ==~ rule.regex)
        assert !('ABC123abc' ==~ rule.regex)
    }

    void testFinalRegex_DefaultValue() {
        assert 'ABC' ==~ rule.finalRegex
        assert 'A_B_C' ==~ rule.finalRegex

        assert !('abc_def' ==~ rule.finalRegex)
        assert !('ABC123abc' ==~ rule.finalRegex)
        assert !('ABCabc' ==~ rule.finalRegex)
        assert !('a_b_CDEF' ==~ rule.finalRegex)
    }

    void testRegexIsNull() {
        rule.regex = null
        shouldFailWithMessageContaining('regex') { applyRuleTo('def myMethod() { int count }') }
    }

    void testApplyTo_DoesNotMatchDefaultRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal deposit_amount
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'BigDecimal deposit_amount')
    }

    void testApplyTo_MatchesDefaultRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal depositAmount
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DoesNotMatchCustomRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int count = 23
                }
            }
        '''
        rule.regex = /z.*/
        assertSingleViolation(SOURCE, 4, 'int count = 23')
    }

    void testApplyTo_MatchesCustomRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    int zCount = 23
                }
            }
        '''
        rule.regex = /z.*/
        assertNoViolations(SOURCE)
    }

    void testApplyTo_NonFinal_IgnoreFinalInValue() {
        final SOURCE = '''
          class MyClass {
                def myMethod() {
                    String name = "final name"
                }
          }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_Final_MatchesDefaultFinalRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    final int COUNT = 23
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_Final_DoesNotMatchDefaultFinalRegex() {
        final SOURCE = '''
          class MyClass {
                def myMethod() {
                    final int count
                }
          }
        '''
        assertSingleViolation(SOURCE, 4, 'final int count')
    }

    void testApplyTo_Final_FinalRegexIsEmpty() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    final int count = 23
                }
            }
        '''
        rule.finalRegex = ''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_Final_DoesNotMatchCustomFinalRegex() {
        final SOURCE = '''
          class MyClass {
                def myMethod() {
                    final\tString COUNT = 23
                }
          }
        '''
        rule.finalRegex = /z.*/
        assertSingleViolation(SOURCE, 4, 'final\tString COUNT = 23')
    }

    void testApplyTo_Final_MatchesCustomFinalRegex() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    final int zCount = 23
                }
            }
        '''
        rule.finalRegex = /z.*/
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DoesNotMatchDefaultRegex_NoClassDefined() {
        final SOURCE = '''
            def myMethod() {
                int Count = 23
            }
        '''
        assertSingleViolation(SOURCE, 3, 'int Count = 23')
    }

    void testApplyTo_NoVariableDefinition() {
        final SOURCE = ' class MyClass { } '
        assertNoViolations(SOURCE)
    }

    void testApplyTo_DoesNotMatchDefaultRegex_ClosureDefinition() {
        final SOURCE = '''
            class MyClass {
                def closure = {
                    int Count = 23
                    Count
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'int Count = 23')
    }

    void testApplyTo_MultipleVariableNames_MatchesDefaultRegex() {
        final SOURCE = '''
            def myMethod() {
                def (pkg, name) = 123
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_MultipleVariableNames_OneDoesNotMatchDefaultRegex() {
        final SOURCE = '''
            def myMethod() {
                def (pkg, Count) = 123
            }
        '''
        // Not valid under Groovy 1.5.x
        if (!GroovyVersion.isGroovy1_5()) {
            assertSingleViolation(SOURCE, 3, 'def (pkg, Count) = 123', 'Count')
        }
    }

    void testApplyTo_MultipleVariableNames_Final_OneDoesNotMatchDefaultRegex() {
        final SOURCE = '''
            def myMethod() {
                final def (OK, bad, OK2) = 123
            }
        '''
        // Not valid under Groovy 1.5.x
        if (!GroovyVersion.isGroovy1_5()) {
            assertSingleViolation(SOURCE, 3, 'final def (OK, bad, OK2) = 123', 'bad')
        }
    }

    void testApplyTo_MultipleVariableNames_TwoDoNotMatchDefaultRegex() {
        final SOURCE = '''
            def myMethod() {
                def (Count, pkg, _MYVAR) = 123
            }
        '''
        // Not valid under Groovy 1.5.x
        if (!GroovyVersion.isGroovy1_5()) {
            assertTwoViolations(SOURCE,
                3, 'def (Count, pkg, _MYVAR) = 123', 'Count',
                3, 'def (Count, pkg, _MYVAR) = 123', '_MYVAR')
        }
    }

    void testApplyTo_IgnoreVariableNames_MatchesSingleName() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal deposit_amount
                }
            }
        '''
        rule.ignoreVariableNames = 'deposit_amount'
        assertNoViolations(SOURCE)
    }

    void testApplyTo_IgnoreVariableNames_MatchesNoNames() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    BigDecimal deposit_amount
                }
            }
        '''
        rule.ignoreVariableNames = 'Other'
        assertSingleViolation(SOURCE, 4, 'BigDecimal deposit_amount')
    }

    void testApplyTo_IgnoreVariableNames_MultipleNamesWithWildcards() {
        final SOURCE = '''
            class MyClass {
                def myMethod() {
                    String GOOD_NAME = 'good'
                    BigDecimal deposit_amount
                    def _amount = 100.25
                    def OTHER_name
                }
            }
        '''
        rule.ignoreVariableNames = 'OTHER?name,_*,GOOD_NAME'
        assertSingleViolation(SOURCE, 5, 'BigDecimal deposit_amount')
    }

    void testApplyTo_Enums() {
        final SOURCE = '''
            public enum AuthorizationLevel { 
                NONE, READ, WRITE
            }
        '''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        new VariableNameRule()
    }

}