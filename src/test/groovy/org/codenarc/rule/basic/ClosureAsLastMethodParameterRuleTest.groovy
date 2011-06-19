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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for ClosureAsLastMethodParameterRule
 *
 * @author Marcin Erdmann
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class ClosureAsLastMethodParameterRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClosureAsLastMethodParameter'
    }

    void testSimpleSuccessScenario() {
        final SOURCE = '''
        	[1,2,3].each { println it }

        	[1,2,3].inject([]) { acc, value -> println value }

        	[1,2,3].inject([]) { acc, value -> println value };
        '''
        assertNoViolations(SOURCE)
    }

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

    void testWithLogicalStatementAtTheEndSuccessScenario() {
        final SOURCE = '''
        	[1,2,3].any {
                it > 2
            } && [1,2,3].all { it < 4 }
        '''
        assertNoViolations(SOURCE)
    }

    void testOneLineWithWhiteCharactersAtTheEndSuccessScenario() {
        final SOURCE = ' [1,2,3].each { println it }   '
        assertNoViolations(SOURCE)
    }

    void testCallWithClosureThatIsNotTheLastMethodParameter() {
        final SOURCE = '''
        	[1,2,3].someMethod({ println it }, 'second param')
        '''
        assertNoViolations(SOURCE)
    }

    void testSimpleSingleViolation() {
        final SOURCE = '''
            [1,2,3].each({ println it })
        '''
        assertSingleViolation(SOURCE, 2, '[1,2,3].each({ println it })', "The last parameter to the 'each' method call is a closure an can appear outside the parenthesis")
    }

    void testComplexSingleViolation() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }).someMethodCall()
        '''
        assertSingleViolation(SOURCE, 2,
            '''[1,2,3].each({
                println it
            })'''
        )
    }

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

    void testComplexTwoViolations() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }).someMethod('first param',
                { println it }
            )
        '''
        assertTwoViolations(SOURCE,
                2,
            '''[1,2,3].each({
                println it
            }).someMethod('first param',
                { println it }
            )''',
                2,
            '''[1,2,3].each({
                println it
            })'''
        )
    }

    void testLinesWithStringsContainingDoubleSlash() {
        final SOURCE = '''
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

    void testMethodCallWiderThanClosureParameter() {
        final SOURCE = '''
            doWithTransactionAndReallyLongName(
                'testing within a transaction',
                new BigDecimal("123456789012345")) {
                    process('12345') }
        '''
        assertNoViolations(SOURCE)
    }

    void testMethodCallSurroundedByExtraParentheses() {
        final SOURCE = '''
            def filterFunds() {
               (funds.findAll { it.fundCode } )
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
               println(funds.clear('clearing', { it.fundCode }) )
            }
          '''
        assertSingleViolation(SOURCE, 25, "funds.clear('clearing', { it.fundCode })", "The last parameter to the 'clear' method call is a closure an can appear outside the parenthesis")
    }

    protected Rule createRule() {
        new ClosureAsLastMethodParameterRule()
    }
}
