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
 * Tests for DuplicateCaseStatementRule
 *
 * @author Hamlet D'Arcy
 */
class DuplicateCaseStatementRuleTest extends AbstractRuleTestCase {

    @Test
    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'DuplicateCaseStatement'
    }

    @Test
    void testApplyTo_NoViolations() {
        final SOURCE = '''
            def value = 2
            switch( value ) {
              case 1:
                break
              case 2:
                break
            }
        '''
        assertNoViolations(SOURCE)
    }

    @Test
    void testApplyTo_SingleViolationInteger() {
        final SOURCE = '''
            switch( 0 ) {
                case 1:
                  break
                case 2:
                  break
                case 2:
                  break
            }
        '''
        assertSingleViolation(SOURCE, 7, 'case 2:')
    }

    @Test
    void testApplyTo_SingleViolationString() {
        final SOURCE = '''
            switch( "test" ) {
                case "$a":
                  break
                case "$a":
                  break
                case "ab":
                  break
                case "ab":
                  break
                case "abc":
                  break
            }
        '''
        assertSingleViolation(SOURCE, 9, 'case "ab":')
    }

    protected Rule createRule() {
        new DuplicateCaseStatementRule()
    }

}
