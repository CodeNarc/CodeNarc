/*
 * Copyright 2014 the original author or authors.
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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.junit.Test

/**
 * @author Dominik Przybysz
 * @author Chris Mair
 */
class NoDefRuleTest extends AbstractRuleTestCase<NoDefRule> {

    @Test
    void testNoViolations() {
        final SOURCE = '''
            List l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            def l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertSingleViolation SOURCE, 2, 'def l = [1, 2, 3, 4]', NoDefRule.MESSAGE
    }

    @Test
    void testViolationsForReturnAndParameter() {
        final SOURCE = '''
            def hello(def l){
                int k = 3
            }
            '''
        assertTwoViolations(SOURCE,
                2, 'def hello(def l){', NoDefRule.MESSAGE_DEF_RETURN,
                2, 'def hello(def l){', NoDefRule.MESSAGE_DEF_PARAMETER)
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            def test(int l){
                int k = 3
                def i = 5
            }
        '''
        assertTwoViolations(SOURCE,
                2, 'def test(int l){', NoDefRule.MESSAGE_DEF_RETURN,
                4, 'def i = 5', NoDefRule.MESSAGE)
    }

    @Test
    void testExcludes_NoViolation() {
        rule.excludeRegex = /((setup|cleanup)(|Spec)|"[^"].*")\(\)/ //spock methods
        final SOURCE = '''
            def setup(){}
            def setupSpec(){}
            def cleanup(){}
            def cleanupSpec(){}
            def "should send"(){}
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationForComments() {
        final SOURCE_WITH_VIOLATION = '''
            // def cmt = [:]
            def l = [1, 2]
            '''
        assertSingleViolation SOURCE_WITH_VIOLATION, 3, 'def l = [1, 2]', NoDefRule.MESSAGE

        final SOURCE_WITHOUT_VIOLATION = '''
            List l = [1, 2, 3, 4]
            // def cmt = [:]
            l.flatten()
            '''
        assertNoViolations(SOURCE_WITHOUT_VIOLATION)
    }

    @Test
    void testConstructor_NoViolation() {
        final SOURCE = '''
            class User {
                String name

                User(String name) {
                    this.name = name
                }
            }
            '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDefOnConstructorParameter_Violation() {
        final SOURCE = '''
            class User {
                String name

                User(def name) {
                    this.name = name
                }
            }
            '''
        assertSingleViolation SOURCE, 5, 'User(def name) {', NoDefRule.MESSAGE_DEF_PARAMETER
    }

    @Test
    void testDefMultipleAssignment_Violation() {
        final SOURCE = '''
            def (init, condition, update) = forStatement.collectionExpression.expressions
        '''
        assertSingleViolation SOURCE, 2, 'def (init, condition, update)', NoDefRule.MESSAGE
    }

    @Test
    void testDefMultipleAssignment_SomeNamesExcluded_Violation() {
        final SOURCE = '''
            def (init, condition, update) = forStatement.collectionExpression.expressions
        '''
        rule.excludeRegex = /(abc|condition|other|update)/
        assertSingleViolation SOURCE, 2, 'def (init, condition, update)', NoDefRule.MESSAGE
    }

    @Test
    void testDefMultipleAssignment_AllNamesExcluded_NoViolations() {
        final SOURCE = '''
            def (init, condition, update) = forStatement.collectionExpression.expressions
        '''
        rule.excludeRegex = /(update|condition|other|init)/
        assertNoViolations(SOURCE)
    }

    @Override
    protected NoDefRule createRule() {
        new NoDefRule()
    }
}
