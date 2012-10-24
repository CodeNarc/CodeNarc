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
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for BrokenOddnessCheckRule
 *
 * @author 'Hamlet D'Arcy'
 */
class BrokenOddnessCheckRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'BrokenOddnessCheck'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            if (x & 1 == 1) { }             // OK
            if (x % 2 != 0) { }             // OK
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testProperty() {
        final SOURCE = '''
            if (x % 2 == 1) { }             // violation
        '''
        assertSingleViolation(SOURCE, 2, '(x % 2 == 1)', "The code uses '(x % 2 == 1)' to check for oddness, which does not work for negative numbers. Use (x & 1 == 1) or (x % 2 != 0) instead")
    }

    @Test
    void testPropertyReverse() {
        final SOURCE = '''
            if (1 == x % 2) { }             // violation
        '''
        assertSingleViolation(SOURCE, 2, '(1 == x % 2)', "The code uses '(1 == x % 2)' to check for oddness, which does not work for negative numbers. Use (x & 1 == 1) or (x % 2 != 0) instead")
    }

    @Test
    void testMethod() {
        final SOURCE = '''
            if (method() % 2 == 1) { }      // violation
        '''
        assertSingleViolation(SOURCE, 2, '(method() % 2 == 1)', "The code uses '(this.method() % 2 == 1)' to check for oddness, which does not work for negative numbers. Use (this.method() & 1 == 1) or (this.method() % 2 != 0) instead")
    }

    protected Rule createRule() {
        new BrokenOddnessCheckRule()
    }
}
