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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTest
import org.codenarc.rule.Rule

/**
 * Tests for EmptyForStatementRule
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class EmptyForStatementRuleTest extends AbstractRuleTest {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyForStatement'
    }

    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    for (int i=0; i < 23; i++) {
                    }
                    println 'ok'
                    for (int j=0; j < 10; j++) {
                    }
                }
            }
        '''
        assertTwoViolations(SOURCE, 4, 'for (int i=0; i < 23; i++) {', 7, 'for (int j=0; j < 10; j++) {')
    }

    void testApplyTo_Violation_ForStatementContainsComment() {
        final SOURCE = '''
            for (int j=0; j < 10; j++) {
                // TODO Should do something here
            }
        '''
        assertSingleViolation(SOURCE, 2, 'for (int j=0; j < 10; j++) {')
    }

    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    for (int j=0; j < 10; j++) {
                        println "ready"
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    void testApplyTo_SingleEmptyStatement() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    for (int j=0; j < 10; j++)
                        ;
                }
            }'''
        assertNoViolations(SOURCE)
    }

    protected Rule createRule() {
        return new EmptyForStatementRule()
    }

}