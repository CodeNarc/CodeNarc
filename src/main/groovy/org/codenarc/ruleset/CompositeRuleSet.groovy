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

/**
 * A <code>RuleSet</code> implementation that aggregates a set of RuleSets and Rules.
 *
 * @author Chris Mair
  */
class CompositeRuleSet implements RuleSet {

    private final rules = []

    /**
     * Add a single Rule to this RuleSet
     * @param rule - the Rule to add
     */
    void addRule(Rule rule) {
        assert rule != null
        rules << rule
    }

    /**
     * Add all of the Rules within the specified RuleSet to this RuleSet
     * @param ruleSet - the RuleSet whose Rules are to be included
     */
    void addRuleSet(RuleSet ruleSet) {
        assert ruleSet != null
        rules.addAll(ruleSet.getRules())
    }

    /**
     * @return a List of Rule objects. The returned List is immutable.
     */
    List getRules() {
        rules.asImmutable()
    }

}
