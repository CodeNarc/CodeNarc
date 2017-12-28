/*
 * Copyright 2017 the original author or authors.
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

import org.codenarc.rule.GenericAbstractRuleTestCase
import org.junit.Test

/**
 * Tests for InvertedConditionRule
 *
 * @author Marcin Erdmann
 */
class InvertedConditionRuleTest extends GenericAbstractRuleTestCase<InvertedConditionRule> {

    @Test
    void testNoViolations() {
        assertNoViolations '''
            class ValidClass {
                void methodWithBinaryExpression() {
                    System.nanoTime() == 2
                    2 < System.nanoTime()
                }
            }
        '''
    }

    @Test
    void testSingleViolation() {
        final SOURCE = '''
            class InvalidClass {
                void methodWithBinaryExpression() {
                    2 == System.nanoTime()
                }
            }
        '''

        assertSingleViolation(SOURCE, 4, '2 == System.nanoTime()', '''2 is a constant expression on the left side of a compare equals operation''')
    }

    @Test
    void testMultipleViolations() {
        final SOURCE = '''
            class InvalidClass {
                void methodWithBinaryExpressions() {
                    null == System.nanoTime()
                    if ("foo" == "oof".reverse()) {}
                }
            }
        '''
        assertViolations(SOURCE,
            [
                lineNumber: 4,
                sourceLineText: 'null == System.nanoTime()',
                messageText: 'null is a constant expression on the left side of a compare equals operation'],
            [
                lineNumber: 5,
                sourceLineText: '"foo" == "oof".reverse()',
                messageText: 'foo is a constant expression on the left side of a compare equals operation'
            ]
        )
    }

    @Override
    protected InvertedConditionRule createRule() {
        new InvertedConditionRule()
    }

}
