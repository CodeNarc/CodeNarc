/*
 * Copyright 2009 the original author or authors.
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
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFail
import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for CompositeRuleSet
 *
 * @author Chris Mair
  */
class CompositeRuleSetTest extends AbstractTestCase {

    private static final RULE1 = [:] as Rule
    private static final RULE2 = [:] as Rule

    private compositeRuleSet

    @Test
    void testDefaultsToEmpyRuleSet() {
        assert compositeRuleSet.getRules() == []
    }

    @Test
    void testAddRuleSet_Null() {
        shouldFailWithMessageContaining('ruleSet') { compositeRuleSet.addRuleSet((RuleSet)null) }
    }

    @Test
    void testAddRuleSet_OneRuleSet() {
        def ruleSet = new ListRuleSet([RULE1])
        compositeRuleSet.addRuleSet(ruleSet)
        assert compositeRuleSet.getRules() == [RULE1]
    }

    @Test
    void testAddRuleSet_TwoRuleSets() {
        def ruleSet1 = new ListRuleSet([RULE1])
        def ruleSet2 = new ListRuleSet([RULE2])
        compositeRuleSet.addRuleSet(ruleSet1)
        compositeRuleSet.addRuleSet(ruleSet2)
        assert compositeRuleSet.getRules() == [RULE1, RULE2]
    }

    @Test
    void testAddRule_Null() {
        shouldFailWithMessageContaining('rule') { compositeRuleSet.addRule((Rule)null) }
    }

    @Test
    void testAddRule() {
        compositeRuleSet.addRule(RULE1)
        compositeRuleSet.addRule(RULE2)
        assert compositeRuleSet.getRules() == [RULE1, RULE2]
    }

    @Test
    void testInternalRulesListIsImmutable() {
        def rules = compositeRuleSet.rules
        shouldFail(UnsupportedOperationException) { rules.add(123) }
    }

    @Before
    void setUpCompositeRuleSetTest() {
        compositeRuleSet = new CompositeRuleSet()
    }
}
