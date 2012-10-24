/*
 * Copyright 2010 the original author or authors.
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
import org.junit.Test

/**
 * Tests for DeadCodeRule
 *
 * @author Hamlet D'Arcy
 */
class DeadCodeRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DeadCode'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            def x = true
            def y = {
                if (x) { return y }
                println x
            }
            def y = {
                return 
            }
            return x
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testCodeAfterThrow() {
        final SOURCE = '''
            def x = {
                throw new Exception()
                println x
            }
            def m() {
                throw new Exception()
                println x
            }
'''
        assertTwoViolations(SOURCE,
                4, 'println x',
                8, 'println x')
    }

    @Test
    void testCodeAfterReturn() {
        final SOURCE = '''
            def x = {
                return
                println x
            }
            def m() {
                return
                println x
            }
'''
        assertTwoViolations(SOURCE,
                4, 'println x',
                8, 'println x')
    }

    @Test
    void testInIfStatement() {
        final SOURCE = '''
            def x = {
                if (x) {
                    return y
                    println x
                }
            } '''
        assertSingleViolation(SOURCE, 5, 'println x')
    }
    @Test
    void testIfStatementAllBranchesReturn() {
        final SOURCE = '''
            def x = {
               if (x)
                    return y
               else if (x)
                    return z
               else
                    throw y
                println x
            } '''
        assertSingleViolation(SOURCE, 9, 'println x')
    }

    @Test
    void testInWhile() {
        final SOURCE = '''
            while (true) {
                return
                println x
             } '''
        assertSingleViolation(SOURCE, 4, 'println x')
    }

    protected Rule createRule() {
        new DeadCodeRule()
    }
}
