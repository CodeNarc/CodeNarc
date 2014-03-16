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

/**
 * A <code>RuleSet</code> implementation that returns a static List of Rules passed into its constructor.
 *
 * @author Chris Mair
  */
class ListRuleSet implements RuleSet {

    private final rules

    /**
     * Construct a new instance from the specified List of rules.
     * @param rules - the List of List of <code>Rule</code> objects; must not be null, but may be empty.
     */
    ListRuleSet(List rules) {
        assert rules != null
        assert rules.every { it instanceof Rule }
        def copy = []
        copy.addAll(rules)
        this.rules = Collections.unmodifiableList(copy)
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        rules
    }
}
