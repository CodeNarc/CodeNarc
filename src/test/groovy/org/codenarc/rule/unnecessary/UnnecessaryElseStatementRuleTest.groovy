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
package org.codenarc.rule.unnecessary

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for UnnecessaryElseStatementRule
 *
 * @author Victor Savkin
  */
class UnnecessaryElseStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryElseStatement'
    }

    @Test
    void testShouldNotAddViolationsForCodeWithoutIfStatement() {
        final SOURCE = '''
            while(true){
                println 'inside'
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldNotAddViolationsForIfStatementWithoutElse() {
        final SOURCE = '''
            if(value){
                return true
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldNotAddViolationsForIfStatementThatDoesNotHaveReturn() {
        final SOURCE = '''
            if(value){
                println true
            } else {
                println false
            }
        '''
        assertNoViolations SOURCE
    }

    @Test
    void testShouldNoViolationsForIfStatementWhereNotEveryBranchEndsWithReturn() {
        final SOURCE = '''
             if(value){
                if(value2){
                    return true
                }
             } else {
                 println false
             }
            '''
        assertNoViolations SOURCE
    }

    @Test
    void testIfElseIf_NoViolations() {
        final SOURCE = '''
            if (foo) {
                return bar
            } else if (baz) {
                return qux
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testIfElseIf() {
        final SOURCE = '''
            if (foo) {
                bar++
            } else if (baz) {
                return qux
            } else {
                fap
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNesting() {
        final SOURCE = '''
            if (foo) {
                bar++
            } else if (baz) {
                return qux
            } else {
                if (foo) {
                    return bar
                } else {
                    return quif
                }
            }
        '''
        assertSingleViolation(SOURCE, 9, '} else {')
    }

    @Test
    void testShouldAddViolationForIfStatementHavingOnlyReturnStatement() {
        final SOURCE = '''
            if(value)
                return true
            else
                println false
        '''
        assertSingleViolation SOURCE, 5, 'println false', 'When an if statement block ends with a return statement the else is unnecessary'
    }

    @Test
    void testShouldAddViolationForIfStatementHavingBlockWithReturnStatement() {
        final SOURCE = '''
            if(value){
                return true
            }else{
                println false
            }
        '''
        assertSingleViolation SOURCE, 4, '}else{', 'When an if statement block ends with a return statement the else is unnecessary'
    }

    @Test
    void testShouldAddViolationForIfStatementEndingWithReturnStatement() {
        final SOURCE = '''
            if(value){
                def a = 2 + 2
                return
            } else {
                println false
            }
        '''
        assertSingleViolation SOURCE, 5, '} else {', 'When an if statement block ends with a return statement the else is unnecessary'
    }

    @Test
    void testShouldAddViolationForIfStatementWithAllBranchesEndingWithReturnStatements() {
        final SOURCE = '''
            if(value){
                if(value2){
                    return true
                }
                while(false){
                }
                return false
            } else {
                println false
            }
        '''
        assertSingleViolation SOURCE, 9, '} else {', 'When an if statement block ends with a return statement the else is unnecessary'
    }

    @Test
    void testShouldAddViolationForIfStatementContainingUnconditionalReturnStatement() {
        final SOURCE = '''
            if(value){
                def a = value1
                return true
                def b = value2
            } else {
                println false
            }
        '''
        assertSingleViolation SOURCE, 6, '} else {', 'When an if statement block ends with a return statement the else is unnecessary'
    }

    @Test
    void testShouldAddTwoViolationForTwoEmbeddedIfStatementsEndingWithReturn() {
        final SOURCE = '''
            if(value){
                if(value2){
                    return true
                } else {
                    return false
                }
            } else {
                println false
            }
        '''
        assertTwoViolations SOURCE, 5, '} else {', 8, '} else {'
    }

    @Test
    void testShouldAddTwoViolationForTwoEmbeddedIfStatementsContainingUnconditionalReturn() {
        final SOURCE = '''
            if(value){
                def a = value1
                if(true){
                    return true
                } else {
                    return false
                }
                def b = value2
            } else {
                println false
            }
        '''
        assertTwoViolations SOURCE, 6, '} else {', 10, '} else {'
    }

    protected Rule createRule() {
        new UnnecessaryElseStatementRule()
    }
}
