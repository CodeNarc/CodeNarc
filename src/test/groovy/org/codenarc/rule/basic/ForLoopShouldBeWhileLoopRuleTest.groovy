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
import org.junit.Test

/**
 * Tests for ForLoopShouldBeWhileLoopRule
 *
 * @author 'Victor Savkin'
 */
class ForLoopShouldBeWhileLoopRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'ForLoopShouldBeWhileLoop'
    }

    @Test
    void testShouldNotAddViolationsForWhileLoops() {
        final SOURCE = '''
        	while(1 > 2)
               println "never happen"
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldNotAddViolationsForNewLoops() {
        final SOURCE = '''
        	for(i in [1,2])
               println i
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testForWithPropertyExpression() {
        final SOURCE = '''
        for(child in this.children) {
            
        } '''
        assertNoViolations SOURCE
    }
    @Test
    void testShouldNotAddViolationsIfForLoopHasInitExpr() {
        final SOURCE = '''
        	for(int i = 0; i<5;)
                println i++
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldNotAddViolationsIfForLoopHasUpdateExpr() {
        final SOURCE = '''
            int i = 0
        	for(; i < 5; i++)
                println i
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldAddViolationIfForLoopHasOnlyConditionExpr() {
        final SOURCE = '''
            int i = 0
        	for(; i < 5;)
                println i++
        '''
        assertSingleViolation SOURCE, 3, 'for(; i < 5;)', 'The for loop can be simplified to a while loop'
    }

    @Test
    void testForEachLoop() {
        final SOURCE = '''
            for (Plan p : plans) {
                println "Plan=$p"
            }
        '''
        assertNoViolations SOURCE
    }

    protected Rule createRule() {
        new ForLoopShouldBeWhileLoopRule()
    }
}
