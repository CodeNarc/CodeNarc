/*
 * Copyright 2026 the original author or authors.
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
import org.junit.jupiter.api.Test

/**
 * Tests for SpockUseVerifyEachRule
 *
 * @author Leonard Brünings
 */
class SpockUseVerifyEachRuleTest extends AbstractRuleTestCase<SpockUseVerifyEachRule> {

    private static final String VIOLATION_MESSAGE_EVERY = "Replace 'every' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_EACH = "Replace 'each' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_EACH_WITH_INDEX = "Replace 'eachWithIndex' with Spock's 'verifyEach' for better per-item failure diagnostics"
    private static final String VIOLATION_MESSAGE_FOR_EACH = "Replace 'forEach' with Spock's 'verifyEach' for better per-item failure diagnostics"

    @Test
    void ruleProperties_AreValid() {
        assert rule.priority == 2
        assert rule.name == 'SpockUseVerifyEach'
        assert rule.checkAllBlocks == true
    }

    @Test
    void every_InThenAndExpect_TwoViolations() {
        final SOURCE = '''
            class MySpec extends spock.lang.Specification {
                def "test every"() {
                    given:
                    def list = []

                    expect:
                    list.every { it.field == value }

                    when:
                    "nothing"

                    then:
                    list.every { it.field == value }
                }
            }
        '''.stripIndent()
        assertViolations(SOURCE,
            [line: 8, source: 'list.every { it.field == value }', message: VIOLATION_MESSAGE_EVERY],
            [line: 14, source: 'list.every { it.field == value }', message: VIOLATION_MESSAGE_EVERY],
        )
    }

    @Override
    protected SpockUseVerifyEachRule createRule() {
        new SpockUseVerifyEachRule()
    }
}
