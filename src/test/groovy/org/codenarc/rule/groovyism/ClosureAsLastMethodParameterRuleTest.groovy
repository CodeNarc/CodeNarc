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
 * Tests for ClosureAsLastMethodParameterRule
 *
 * @author Marcin Erdmann
 */
class ClosureAsLastMethodParameterRuleTest extends AbstractRuleTestCase<ClosureAsLastMethodParameterRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClosureAsLastMethodParameter'
    }

    @Test
    void testSimpleSuccessScenario() {
        final SOURCE = '''
            [1,2,3].each { println it }

            [1,2,3].inject([]) { acc, value -> println value }

            [1,2,3].inject([]) { acc, value -> println value };
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testWithComments() {
        final SOURCE = '''
            [1, 2, 3].each { /*
            sth // */ println it }
            [1, 2, 3].each { /*
            ) // */ println it }
            [1, 2, 3].each { /*
            // */ println it }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testWithLogicalStatementAtTheEndSuccessScenario() {
        final SOURCE = '''
            [1,2,3].any {
                it > 2
            } && [1,2,3].all { it < 4 }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testOneLineWithWhiteCharactersAtTheEndSuccessScenario() {
        final SOURCE = ' [1,2,3].each { println it }   '
        assertNoViolations(SOURCE)
    }

    @Test
    void testCallWithClosureThatIsNotTheLastMethodParameter() {
        final SOURCE = '''
            [1,2,3].someMethod({ println it }, 'second param')
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSimpleSingleViolation() {
        final SOURCE = '''
            [1,2,3].each({ println it })
        '''
        assertSingleViolation(SOURCE, 2, '[1,2,3].each({ println it })', "The last parameter to the 'each' method call is a closure an can appear outside the parenthesis")
    }

    @Test
    void testComplexSingleViolation() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }).someMethodCall()
        '''
        assertSingleViolation(SOURCE, 2,
            '''[1,2,3].each({'''
        )
    }

    @Test
    void testLastParenthesesOnLineFollowingEndOfClosure() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }
            ).someMethodCall()
        '''
        assertSingleViolation(SOURCE, 2,
            '''[1,2,3].each({'''
        )
    }

    @Test
    void testSimpleTwoViolations() {
        final SOURCE = '''
            [1,2,3].each({ println it })
            def value = 1
            [1,2,3].someMethod('first param', { println it })
        '''
        assertTwoViolations(SOURCE,
                2, '[1,2,3].each({ println it })',
                4, '[1,2,3].someMethod(\'first param\', { println it })')
    }

    @Test
    void testComplexTwoViolations() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }).someMethod('first param',
                { println it }
            )
        '''
        assertTwoViolations(SOURCE,
                2, '''[1,2,3].each({''',
                2, '''[1,2,3].each({'''
        )
    }

    @Test
    void testLinesWithStringsContainingDoubleSlash() {
        final SOURCE = '''
    @Test
            void testProcess() {
                shouldFail { process('xxx://wrsnetdev.usa.wachovia.net') }
                shouldFail(Exception) { process('http://')
                }
                shouldFail {
                    process('http://wrsnetdev.usa.wachovia.net:xxx') }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMethodCallWiderThanClosureParameter() {
        final SOURCE = '''
            doWithTransactionAndReallyLongName(
                'testing within a transaction',
                new BigDecimal("123456789012345")) {
                    process('12345') }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testMethodCallSurroundedByExtraParentheses() {
        final SOURCE = '''
            def filterFunds() {
               (funds.findAll { it.fundCode } )
            }
            def filterFunds_TwoExtraParentheses() {
               ((funds.findAll{it.fundCode}))
            }
            def extendFunds() {
               (funds.extend(3) { it.fundCode } )
            }
            def purgeFunds() {
               (funds.purge {
                    it.fundCode
                    }
               )
            }
            boolean isSkipParticipant(Participant participant) {
                if(((majorMinorStatusList.findAll{it -> it==(participant.majorStatus + participant.minorStatus).trim()})) ||
                        ((majorStatusList.findAll{it ->it==(participant.majorStatus).trim()})) ||
                        (participant.paymentCode == 1)||(participant.ssn=='')) {
                    return true
                }
                return false
            }

            // The only violation
            def clearFunds() {
               (println(funds.clear('clearing', { it.fundCode }) ))
            }
          '''
        assertSingleViolation(SOURCE, 28, "funds.clear('clearing', { it.fundCode })", "The last parameter to the 'clear' method call is a closure an can appear outside the parenthesis")
    }

    @Test
    void testNestedMethodCallSurroundedByExtraParentheses_KnownLimitation() {
        final SOURCE = '''
            def clearFunds() {
               (println((funds.clear('clearing', { it.fundCode })) ))
            }
          '''
        // Should actually fail with violation for inner method call -- clear()
        //assertSingleViolation(SOURCE, 3, "funds.clear('clearing', { it.fundCode })", "The last parameter to the 'clear' method call is a closure an can appear outside the parenthesis")
        assertNoViolations(SOURCE)
    }

    @Test
    void testMultiLineMethodCall_StartsWithParentheses() {
        final SOURCE = '''
            ((Node)o).children().inject( [:] ){ Map<String, String> m, Node childNode ->
                println m
            }
            (0..1).inject( [:] ){ Map<String, String> m, Node childNode ->
                println m
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testClosureParameterCastUsingAs_NoViolations() {
        final SOURCE = '''
            doStuff({ -> println 111 } as Runnable,
                 { -> println 222 } as Runnable)

            1 * service.calculateSmth(
                { Bean1 request1 -> request1.type == MAIN } as Bean1,
                { Bean2 request2 -> request2.type == MAIN } as Bean2) == 0.50
        '''
        assertNoViolations(SOURCE)
    }

    @Override
    protected ClosureAsLastMethodParameterRule createRule() {
        new ClosureAsLastMethodParameterRule()
    }
}
