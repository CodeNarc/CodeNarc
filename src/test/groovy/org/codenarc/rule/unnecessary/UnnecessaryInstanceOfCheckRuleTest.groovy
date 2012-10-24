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
 * Tests for UnnecessaryInstanceOfCheckRule
 *
 * @author Hamlet D'Arcy
  */
class UnnecessaryInstanceOfCheckRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 3
        assert rule.name == 'UnnecessaryInstanceOfCheck'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
            if (!(variable instanceof String)) { /* */ }
            def x = !(variable instanceof String)
            def y = variable instanceof String
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testDeclaration() {
        final SOURCE = '''
            def x = !variable instanceof String
        '''
        assertSingleViolation(SOURCE, 2, '!variable instanceof String', "The result of '!(variable)' will never be a String")
    }

    @Test
    void testDeclaration2() {
        final SOURCE = '''
            def x = !variable instanceof Boolean
        '''
        assertSingleViolation(SOURCE, 2, '!variable instanceof Boolean', "The result of '!(variable)' will always be a Boolean")
    }

    @Test
    void testIfStatement() {
        final SOURCE = '''
            if (!var instanceof Integer) { /* */ }
        '''
        assertSingleViolation(SOURCE, 2, '!var instanceof Integer', "The result of '!(var)' will never be a Integer")
    }

    @Test
    void testIfStatement2() {
        final SOURCE = '''
            if (!var instanceof Boolean) { /* */ }
        '''
        assertSingleViolation(SOURCE, 2, '!var instanceof Boolean', "The result of '!(var)' will always be a Boolean")
    }

    protected Rule createRule() {
        new UnnecessaryInstanceOfCheckRule()
    }
}
