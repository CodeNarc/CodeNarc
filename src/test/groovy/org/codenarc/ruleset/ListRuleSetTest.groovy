/*
 * Copyright 2008 the original author or authors.
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
package org.codenarc.ruleset

import org.codenarc.rule.Rule
import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for ListRuleSet
 *
 * @author Chris Mair
  */
class ListRuleSetTest extends AbstractTestCase {

    private static final RULE = [:] as Rule

    @Test
    void testWithRules() {
        def ruleSet = new ListRuleSet([RULE])
        assert ruleSet.getRules() == [RULE]
    }

    @Test
    void testRulesListIsImmutable() {
        def list = [RULE]
        def ruleSet = new ListRuleSet(list)
        list.clear()
        def r = ruleSet.getRules()
        assert r == [RULE]
        shouldFail(UnsupportedOperationException) { r.clear() }
    }

    @Test
    void testWithNull() {
        shouldFailWithMessageContaining('rules') { new ListRuleSet(null) }
    }

    @Test
    void testWithNonRules() {
        shouldFail { new ListRuleSet([RULE, 23]) }
    }
}
