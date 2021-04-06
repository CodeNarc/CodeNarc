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
package org.codenarc.ruleset

import groovy.json.JsonSlurper
import org.codenarc.rule.Rule
import org.codenarc.ruleregistry.RuleRegistryHolder
import org.codenarc.util.PropertyUtil

/**
 * A <code>RuleSet</code> implementation that parses Rule definitions from Json read from a
 * <code>Reader</code>. Note that this class attempts to read and parse the Json from within
 * the constructor.
 *
 * @author Nicolas Vuillamy
  */
class JsonReaderRuleSet implements RuleSet {

    private final List rules = []

    /**
     * Construct a new instance on the specified Reader
     * @param reader - the Reader from which the XML will be read; must not be null
     */
    JsonReaderRuleSet(Reader reader) {
        assert reader
        def json = reader.text

        def ruleset = new JsonSlurper().parseText(json)
        loadRuleElements(ruleset)
        rules = rules.asImmutable()
    }

    /**
     * @return a List of Rule objects
     */
    @Override
    List getRules() {
        rules
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void loadRuleElements(Map<String,Map> ruleset) {
        ruleset.each { String ruleName, Map ruleParams ->
            // ruleName can be a CodeNarc class or just RuleName
            def ruleNameSplit = ruleName.tokenize('.')
            def ruleClassName = ruleNameSplit.size() > 2 ? ruleName : // ex: org.codenarc.rule.exception.CatchThrowableRule
                    RuleRegistryHolder.ruleRegistry?.getRuleClass(ruleName)?.name // ex: CatchThrowableRule
            assert ruleClassName, "No such rule named [$ruleName]"
            def ruleClass = getClass().classLoader.loadClass(ruleClassName)
            RuleSetUtil.assertClassImplementsRuleInterface(ruleClass)
            def rule = ruleClass.newInstance()
            rules << rule
            setRuleProperties(ruleParams, rule)
        }
    }

    private void setRuleProperties(Map ruleParams, Rule rule) {
        ruleParams.each { paramName, paramValue ->
            PropertyUtil.setPropertyFromString(rule, paramName, paramValue.toString())
        }
    }

}
