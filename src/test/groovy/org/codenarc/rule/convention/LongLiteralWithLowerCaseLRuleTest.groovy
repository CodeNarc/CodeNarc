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
package org.codenarc.rule.convention

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule
import org.junit.Test

/**
 * Tests for LongLiteralWithLowerCaseLRule
 *
 * @author Hamlet D'Arcy
 */
class LongLiteralWithLowerCaseLRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LongLiteralWithLowerCaseL'
    }

    @Test
    void testSuccessScenario() {
        final SOURCE = '''
        	def a = 55
        	def b = 55L
        	def x = 5
        	def y = 5L
        	def z = 5.0f
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            def x = 1l
        '''
        assertSingleViolation(SOURCE, 2, 'def x = 1l', 'The literal 1l should be rewritten 1L')
    }

    @Test
    void testLongerNUmber() {
        final SOURCE = '''
            def x = 222l
        '''
        assertSingleViolation(SOURCE, 2, 'def x = 222l', 'The literal 222l should be rewritten 222L')
    }

    protected Rule createRule() {
        new LongLiteralWithLowerCaseLRule()
    }
}
