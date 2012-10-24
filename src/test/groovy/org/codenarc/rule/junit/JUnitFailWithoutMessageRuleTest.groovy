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
package org.codenarc.rule.junit

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for JUnitFailWithoutMessageRule
 *
 * @author Hamlet D'Arcy
  */
class JUnitFailWithoutMessageRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'JUnitFailWithoutMessage'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
    @Test
                void testMethod() {
                    fail('msg')
                    Assert.fail('msg')
                    this.fail('msg')
                    foo.fail()
                }
             }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testFailCalls() {
        final SOURCE = '''
        	 class MyTestCase extends TestCase {
                void testMethod() {
                    fail()
                    Assert.fail()
                }
             }
        '''
        assertTwoViolations(SOURCE,
                4, 'fail()',
                5, 'Assert.fail()')   
    }

    protected Rule createRule() {
        new JUnitFailWithoutMessageRule()
    }
}
