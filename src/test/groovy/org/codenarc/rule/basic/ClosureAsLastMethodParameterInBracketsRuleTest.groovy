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
 * Tests for ClosureAsLastMethodParameterInBracketsRule
 *
 * @author Marcin Erdmann
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class ClosureAsLastMethodParameterInBracketsRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ClosureAsLastMethodParameterInBrackets'
    }

    void testSimpleSuccessScenario() {
        final SOURCE = '''
        	[1,2,3].each { println it }

        	[1,2,3].inject([]) { acc, value -> println value }

        	[1,2,3].inject([]) { acc, value -> println value };
        '''
        assertNoViolations(SOURCE)
    }

    void testWithCommentAtTheEndSuccessScenario() {
        final SOURCE = '''
        	[1,2,3].each { println it } //some comment
            [1,2,3].each { println it } /*some comment*/
            [1,2,3].each { println it } // strange comment /*some comment*/
            [1,2,3].each {
                println it
            } // multiline with comment
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

    void testSimpleSingleViolationWithComment() {
        final SOURCE = '''
            [1,2,3].each({
                println it
            }) //some comment'
        '''
        assertSingleViolation(SOURCE, 2,
            '''[1,2,3].each({
                println it
            })'''
        )
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

    protected Rule createRule() {
        new ClosureAsLastMethodParameterInBracketsRule()
    }
}
