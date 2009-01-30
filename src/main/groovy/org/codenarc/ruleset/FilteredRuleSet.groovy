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

/**
 * A <code>RuleSet</code> implementation that is a Decorator for another RuleSet, but provides
 * the ability to filter included and excluded rules within that RuleSet.
 * <p>
 * If a Rule matches both an include and an exclude, then the exclude takes precedence, i.e. the
 * Rule is NOT includes in the result from <code>getRules()</code>.
 *
 * @author Chris Mair
 * @version $Revision: 7 $ - $Date: 2009-01-21 21:52:00 -0500 (Wed, 21 Jan 2009) $
 */
class FilteredRuleSet implements RuleSet {
    private rules = []
    private includes = []
    private excludes = []

    /**
     * Construct a new instance on the specified RuleSet
     * @param ruleset - the RuleSet to be filtered (decorated); must not be null.
     */
    FilteredRuleSet(RuleSet ruleSet) {
        assert ruleSet != null
        rules.addAll(ruleSet.getRules())
    }

    /**
     * Add an include criteria.
     * @param include - the include specification, which is compared against the ids of all of the Rules
     *      within the underlying RuleSet. Only matching Rules are included in the result from <code>getRules()</code>.
     *      The <code>include</code> value must not be null or empty.
     */
    void addInclude(String include) {
        assert include
        includes << include
    }

    /**
     * Add an exclude criteria.
     * @param exclude - the exclude specification, which is compared against the ids of all of the Rules
     *      within the underlying RuleSet. Any matching Rules are excluded in the result from <code>getRules()</code>.
     *      The <code>exclude</code> value must not be null or empty.
     */
    void addExclude(String exclude) {
        assert exclude
        excludes << exclude
    }

    /**
     * Return the List of Rules that match the include(s) (if specified) AND DO NOT match any exlcude(s) specified.
     * @return the filtered List of Rule objects. The returned List is immutable.
     */
    List getRules() {
        def filteredRules = []
        rules.each { rule ->
            def matchesIncludes = includes.empty || includes.find { it == rule.id }
            if (matchesIncludes) {
                def matchesExcludes = !excludes.empty && excludes.find { it == rule.id }
                if (!matchesExcludes) {
                    filteredRules << rule
                }
            }
        }
        return filteredRules.asImmutable()
    }

}