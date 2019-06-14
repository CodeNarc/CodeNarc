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

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * Tests for EmptyWhileStatementRule
 *
 * @author Chris Mair
 */
class EmptyWhileStatementRuleTest extends AbstractRuleTestCase<EmptyWhileStatementRule> {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'EmptyWhileStatement'
    }

    @Test
    void testApplyTo_Violation() {
        final SOURCE = '''
            class MyClass {
                def myClosure = {
                    while (x==23) {
                    }
                    println 'ok'
                    while (!stopped) {
                    }
                }
            }
        '''
        assertTwoViolations(SOURCE, 4, 'while (x==23) {', 7, 'while (!stopped) {')
    }

    @Test
    void testApplyTo_Violation_WhileStatementContainsComment() {
        final SOURCE = '''
            while (isReady) {
                // TODO Should do something here
            }
        '''
        assertSingleViolation(SOURCE, 2, 'while (isReady) {')
    }

    @SuppressWarnings('UnnecessarySemicolon')
    @Test
    void testApplyTo_SingleEmptyStatement() {
        final SOURCE = '''
            def myMethod() {
                while (isReady)
                    ;
            }'''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''class MyClass {
                def myMethod() {
                    while (isReady) {
                        println "ready"
                    }
                }
            }'''
        assertNoViolations(SOURCE)
    }

    @Override
    protected EmptyWhileStatementRule createRule() {
        new EmptyWhileStatementRule()
    }

}
