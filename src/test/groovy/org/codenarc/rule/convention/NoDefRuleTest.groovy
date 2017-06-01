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
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * @author Dominik Przybysz
 */
class NoDefRuleTest extends AbstractRuleTestCase {

    @Test
    void testSuccessScenario() {
        final SOURCE = '''\
            List l = [1, 2, 3, 4]
            l.flatten()
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''\
            def l = [1, 2, 3, 4]
            l.flatten()
        '''.stripMargin()
        assertSingleViolation SOURCE, 1, 'def l = [1, 2, 3, 4]', NoDefRule.MESSAGE
    }

    @Test
    void testTwoViolationForReturnAndParameter() {
        final SOURCE = '''\
                def hello(def l){
                    int k = 3
                }
            '''.stripMargin()
        assertTwoViolations(SOURCE,
                1, 'def hello(def l){', NoDefRule.MESSAGE_DEF_RETURN,
                1, 'def hello(def l){', NoDefRule.MESSAGE_DEF_PARAMETER)
    }

    @Test
    void testTwoViolation() {
        final SOURCE = '''\
            def test(int l){
                int k = 3
                def i = 5
            }
        '''.stripMargin()
        assertTwoViolations(SOURCE,
                1, 'def test(int l){', NoDefRule.MESSAGE_DEF_RETURN,
                3, 'def i = 5', NoDefRule.MESSAGE)
    }

    @Test
    void testExcludesNoViolation() {
        rule.excludeRegex = /((setup|cleanup)(|Spec)|"[^"].*")\(\)/ //spock methods
        final SOURCE = '''\
            def setup(){}
            def setupSpec(){}
            def cleanup(){}
            def cleanupSpec(){}
            def "should send"(){}
        '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testNoViolationForComments() {
        final SOURCE_WITH_VIOLATION = '''\
                // def cmt = [:]
                def l = [1, 2]
            '''.stripMargin()
        assertSingleViolation SOURCE_WITH_VIOLATION, 2, 'def l = [1, 2]', NoDefRule.MESSAGE

        final SOURCE_WITHOUT_VIOLATION = '''\
                    List l = [1, 2, 3, 4]
                    // def cmt = [:]
                    l.flatten()
                '''.stripMargin()
        assertNoViolations(SOURCE_WITHOUT_VIOLATION)
    }

    @Test
    void testConstructorSuccessScenario() {
        final SOURCE = '''\
                        class User {
                            String name
                            
                            User(String name) {
                                this.name = name
                            }
                        }
                    '''.stripMargin()
        assertNoViolations(SOURCE)
    }

    @Test
    void testConstructorSingleViolation() {
        final SOURCE = '''\
                            class User {
                                String name
                                
                                User(def name) {
                                    this.name = name
                                }
                            }
                        '''.stripMargin()
        assertSingleViolation SOURCE, 4, 'User(def name) {', NoDefRule.MESSAGE_DEF_PARAMETER
    }

    @Override
    protected Rule createRule() {
        new NoDefRule()
    }
}
