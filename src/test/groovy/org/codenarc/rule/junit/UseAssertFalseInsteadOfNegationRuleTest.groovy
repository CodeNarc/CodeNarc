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
 * Tests for UseAssertFalseInsteadOfNegationRule
 *
 * @author 'Hamlet D'Arcy'
  */
class UseAssertFalseInsteadOfNegationRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'UseAssertFalseInsteadOfNegation'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    assertFalse(condition)
                    assertTrue(condition)
                    assertTrue(!condition, condition)
                    assertFalse(!condition, condition)
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testUsingThisReference() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    assertTrue(!condition)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'assertTrue(!condition)', 'assertTrue(!condition) can be simplified to assertFalse(condition)')
    }

    @Test
    void testUsingStaticReference() {
        final SOURCE = '''
            class MyTest extends GroovyTestCase {
                public void testMethod() {
                    Assert.assertTrue(!condition)
                }
            }
        '''
        assertSingleViolation(SOURCE, 4, 'Assert.assertTrue(!condition)', 'Assert.assertTrue(!condition) can be simplified to Assert.assertFalse(condition)')
    }

    protected Rule createRule() {
        new UseAssertFalseInsteadOfNegationRule()
    }
}
