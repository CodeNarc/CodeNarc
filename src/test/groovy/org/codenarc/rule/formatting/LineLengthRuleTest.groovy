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
package org.codenarc.rule.formatting

import org.codenarc.rule.AbstractRuleTestCase
import org.codenarc.rule.Rule

/**
 * Tests for LineLengthRule
 *
 * @author Hamlet D'Arcy
 * @author <a href="mailto:geli.crick@osoco.es">Geli Crick</a>
  */
class LineLengthRuleTest extends AbstractRuleTestCase {

    void testRuleProperties() {
        assert rule.priority == 2
        assert rule.name == 'LineLength'
    }

    void testSuccessScenario() {
        final SOURCE = '''
        	class Person {
                def longMethodButNotQuiteLongEnough1234567890123456789012345() {
                }
            }
        '''
        assertNoViolations(SOURCE)
    }

    void testSingleViolation() {
        final SOURCE = '''
        	class Person {
                def longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456() {
                }
            }
        '''
        assertSingleViolation(SOURCE, 3, 'def longMethod123456789012345678900123456789012345678901234567890123456789012345678901234567890123456',
                'The line exceeds 120 characters. The line is 121 characters.')
    }

    void testComments() {
        final SOURCE = '''
        	class Person {
                // this is a really long comment 001234567890123456789012345678907890123456789012345678901234567890123456
            }
        '''
        assertSingleViolation(SOURCE, 3, '// this is a really long comment 001234567890123456789012345678907890123456789012345678901234567890123456')
    }

    protected Rule createRule() {
        new LineLengthRule()
    }
}