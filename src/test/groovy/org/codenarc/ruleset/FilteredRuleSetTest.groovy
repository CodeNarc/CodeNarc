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

import org.codenarc.rule.StubRule
import org.codenarc.ruleset.FilteredRuleSet
import org.codenarc.ruleset.ListRuleSet
import org.codenarc.test.AbstractTest

/**
 * Tests for FilteredRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class FilteredRuleSetTest extends AbstractTest {
    static final RULE1 = new StubRule(name:'Rule1')
    static final RULE2 = new StubRule(name:'Rule2')
    static final RULE3 = new StubRule(name:'Rule3')
    private innerRuleSet
    private filteredRuleSet

    void testConstructor_NullRuleSet() {
        shouldFailWithMessageContaining('ruleSet') { new FilteredRuleSet(null) }
    }

    void testGetRules() {
        assert filteredRuleSet.getRules() == [RULE1, RULE2, RULE3]
    }

    void testAddInclude_Null() {
        shouldFailWithMessageContaining('include') { filteredRuleSet.addInclude(null) }
    }

    void testAddInclude_Empty() {
        shouldFailWithMessageContaining('include') { filteredRuleSet.addInclude('') }
    }

    void testAddExclude_Null() {
        shouldFailWithMessageContaining('exclude') { filteredRuleSet.addExclude(null) }
    }

    void testAddExclude_Empty() {
        shouldFailWithMessageContaining('exclude') { filteredRuleSet.addExclude('') }
    }

    void testOneInclude() {
        filteredRuleSet.addInclude('Rule2')
        assert filteredRuleSet.getRules() == [RULE2]
    }

    void testTwoIncludes() {
        filteredRuleSet.addInclude('Rule1')
        filteredRuleSet.addInclude('Rule3')
        assert filteredRuleSet.getRules() == [RULE1, RULE3]
    }

    void testIncludes_Wildcards() {
        filteredRuleSet.addInclude('Ru?e1')
        filteredRuleSet.addInclude('*3')
        assert filteredRuleSet.getRules() == [RULE1, RULE3]
    }

    void testOneExclude() {
        filteredRuleSet.addExclude('Rule2')
        assert filteredRuleSet.getRules() == [RULE1, RULE3]
    }

    void testTwoExclude() {
        filteredRuleSet.addExclude('Rule2')
        filteredRuleSet.addExclude('Rule3')
        assert filteredRuleSet.getRules() == [RULE1]
    }

    void testExcludes_Wildcards() {
        filteredRuleSet.addExclude('*2')
        filteredRuleSet.addExclude('R?l?3')
        assert filteredRuleSet.getRules() == [RULE1]
    }

    void testBothIncludesAndExcludes() {
        filteredRuleSet.addInclude('Rule1')
        filteredRuleSet.addInclude('Rule2')
        filteredRuleSet.addExclude('Rule2')     // Exclude takes precedence
        filteredRuleSet.addExclude('Rule3')
        assert filteredRuleSet.getRules() == [RULE1]
    }

    void testBothIncludesAndExcludes_Wildcards() {
        filteredRuleSet.addInclude('R?le1')
        filteredRuleSet.addInclude('Rule2')
        filteredRuleSet.addExclude('*2')     // Exclude takes precedence
        filteredRuleSet.addExclude('??le3')
        assert filteredRuleSet.getRules() == [RULE1]
    }

    void testInternalRulesListIsImmutable() {
        def rules = filteredRuleSet.rules
        shouldFail(UnsupportedOperationException) { rules.add(123) }
    }

    void setUp() {
        super.setUp()
        innerRuleSet = new ListRuleSet([RULE1, RULE2, RULE3])
        filteredRuleSet = new FilteredRuleSet(innerRuleSet)
    }
}