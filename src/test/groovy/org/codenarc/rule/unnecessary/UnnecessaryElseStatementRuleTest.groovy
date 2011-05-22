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

/**
 * Tests for UnnecessaryElseStatementRule
 *
 * @author Victor Savkin
 * @version $Revision: 329 $ - $Date: 2010-04-29 04:20:25 +0200 (Thu, 29 Apr 2010) $
 */
class UnnecessaryElseStatementRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 5
        assert rule.name == 'UnnecessaryElseStatement'
    }

    void testShouldNotAddViolationsForCodeWithoutIfStatement() {
        final SOURCE = '''
            while(true){
                println 'inside'
            }
        '''
        assertNoViolations SOURCE
    }

    void testShouldNotAddViolationsForIfStatementWithoutElse() {
        final SOURCE = '''
            if(value){
                return true
            }
        '''
        assertNoViolations SOURCE
    }

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

    void testShouldAddViolationForIfStatementHavingOnlyReturnStatement() {
        final SOURCE = '''
            if(value)
                return true
            else
                println false
        '''
        assertSingleViolation SOURCE, 2, null, 'When an if statement block ends with a return statement the else is unnecessary'
    }

    void testShouldAddViolationForIfStatementHavingBlockWithReturnStatement() {
        final SOURCE = '''
            if(value){
                return true
            }else{
                println false
            }
        '''
        assertSingleViolation SOURCE, 2, null, 'When an if statement block ends with a return statement the else is unnecessary'
    }

    void testShouldAddViolationForIfStatementEndingWithReturnStatement() {
        final SOURCE = '''
            if(value){
                def a = 2 + 2
                return
            } else {
                println false
            }
        '''
        assertSingleViolation SOURCE, 2, null, 'When an if statement block ends with a return statement the else is unnecessary'
    }

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
        assertSingleViolation SOURCE, 2, null, 'When an if statement block ends with a return statement the else is unnecessary'
    }

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
        assertSingleViolation SOURCE, 2, null, 'When an if statement block ends with a return statement the else is unnecessary'
    }

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
        assertTwoViolations SOURCE, 2, null, 3, null
    }

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
        assertTwoViolations SOURCE, 2, null, 4, null
    }

    protected Rule createRule() {
        new UnnecessaryElseStatementRule()
    }
}
