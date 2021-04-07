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
    void test_NoViolations() {
        final SOURCE = '''
            List l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void test_Class_Constructor_Field_NoViolation() {
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
    void test_def_Variable_Violation() {
        final SOURCE = '''
            def l = [1, 2, 3, 4]
            l.flatten()
        '''
        assertSingleViolation SOURCE, 2, 'def l = [1, 2, 3, 4]', NoDefRule.MESSAGE
    }

    @Test
    void test_def_MethodReturnAndParameter_Violations() {
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
    void test_def_MultipleViolations() {
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
    void test_def_excludeRegex_NoViolations() {
        final SOURCE = '''
            def setup(){}
            def setupSpec(){}
            def cleanup(){}
            def cleanupSpec(){}
            def "should send"(){}
        '''
        rule.excludeRegex = /((setup|cleanup)(|Spec)|"[^"].*")/ //spock methods
        assertNoViolations(SOURCE)
    }

    @Test
    void test_def_excludeRegex_FieldAndMethodNames_NoViolations() {
        final SOURCE = '''
            class MyClass {
                def aaa = 123
                def bbb() { }
                String ccc = 'xxx'
            }
            '''
        rule.excludeRegex = /aaa|bbb|ccc/
        assertNoViolations(SOURCE)
    }

    @Test
    void test_def_WithinComments_No() {
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
    void test_def_ConstructorParameter_Violation() {
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
    void test_def_Field_Violation() {
        final SOURCE = '''
            class MyClass {
                def name
                def count = 99;
            }
            '''
        assertViolations(SOURCE,
                [line:3, source:'def name', message:NoDefRule.MESSAGE_DEF_FIELD],
                [line:4, source:'def count = 99;', message:NoDefRule.MESSAGE_DEF_FIELD]
        )
    }

    @Test
    void test_def_MultipleAssignment_Violation() {
        final SOURCE = '''
            def (init, condition, update) = forStatement.collectionExpression.expressions
        '''
        assertSingleViolation SOURCE, 2, 'def (init, condition, update)', NoDefRule.MESSAGE
    }

    @Test
    void test_def_MultipleAssignment_SomeNamesExcluded_Violation() {
        final SOURCE = '''
            def (init, condition, update) = forStatement.collectionExpression.expressions
        '''
        rule.excludeRegex = /(abc|condition|other|update)/
        assertSingleViolation SOURCE, 2, 'def (init, condition, update)', NoDefRule.MESSAGE
    }

    @Test
    void test_def_MultipleAssignment_AllNamesExcluded_NoViolations() {
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
