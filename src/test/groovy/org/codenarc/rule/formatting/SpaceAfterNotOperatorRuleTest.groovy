/*
 * Copyright 2020 the original author or authors.
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
import org.junit.Test

/**
 * Tests for SpaceAfterNotOperatorRule
 */
class SpaceAfterNotOperatorRuleTest extends AbstractRuleTestCase<SpaceAfterNotOperatorRule> {

    @Test
    void ruleProperties() {
        assert rule.priority == 3
        assert rule.name == 'SpaceAfterNotOperator'
    }

    @Test
    void noViolations() {
        assertNoViolations '''
            class Valid {
                boolean valid() {
                    !""
                }
            }
        '''
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class Invalid {
                boolean invalid() {
                    ! ""
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, '! ""', 'There is whitespace after the not operator.')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class Invalid {
                void invalid() {
                    ! aMethod(! "")
                }

                boolean aMethod(boolean argument) {
                }
            }
        '''

        assertViolations(SOURCE,
            [line: 4, source: '! aMethod(! "")', message: 'There is whitespace after the not operator.'],
            [line: 4, source: '! aMethod(! "")', message: 'There is whitespace after the not operator.']
        )
    }

    @Test
    void violationsInsideClosureFieldsAreNotDuplicated() {
        final SOURCE = '''
            class ClassWithClosureField {
                def scriptToFinish = {
                    ! $("#spinner").isDisplayed()
                }
            }
        '''

        assertSingleViolation(SOURCE, 4)
    }

    @Override
    protected SpaceAfterNotOperatorRule createRule() {
        new SpaceAfterNotOperatorRule()
    }
}
