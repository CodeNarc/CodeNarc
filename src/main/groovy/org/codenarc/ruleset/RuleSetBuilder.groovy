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
import org.codenarc.ruleregistry.RuleRegistryHolder

/**
 * A Builder for RuleSets. Create a RuleSet by calling the <code>ruleset</code>
 * method, passing in a <code>Closure</code> defining the contents of the RuleSet.
 * The <code>Closure</code> can contain any combination of the following (as well as
 * arbitrary Groovy code):
 * <ul>
 *   <li><code>ruleset</code> - to load a RuleSet file. The path specifies either a
 *          Groovy file or an XML file.</li>
 *   <li><code>rule</code> - to load a single Rule (specify either the rule name or rule class)</li>
 *   <li><code>description</code> - description of the RuleSet (optional)</li>
 * </ul>
 *
 * @author Chris Mair
  */
class RuleSetBuilder {

    private final topLevelDelegate = new TopLevelDelegate()

    void ruleset(Closure closure) {
        closure.delegate = topLevelDelegate
        closure.call()
    }

    RuleSet getRuleSet() {
        topLevelDelegate.ruleSet
    }
}

class TopLevelDelegate {

    private final allRuleSet = new CompositeRuleSet()

    void ruleset(String path) {
        def ruleSet = RuleSetUtil.loadRuleSetFile(path)
        allRuleSet.addRuleSet(ruleSet)
    }

    void ruleset(String path, Closure closure) {
        def ruleSet = RuleSetUtil.loadRuleSetFile(path)
        def ruleSetConfigurer = new RuleSetDelegate(ruleSet)
        closure.delegate = ruleSetConfigurer
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.call()
        allRuleSet.addRuleSet(ruleSetConfigurer.ruleSet)
    }

    void rule(Class ruleClass) {
        RuleSetUtil.assertClassImplementsRuleInterface(ruleClass)
        Rule rule = ruleClass.newInstance()
        allRuleSet.addRule(rule)
    }

    void rule(Class ruleClass, Map properties) {
        RuleSetUtil.assertClassImplementsRuleInterface(ruleClass)  // TODO refactor
        Rule rule = ruleClass.newInstance()
        properties.each { key, value -> rule[key] = value }
        allRuleSet.addRule(rule)
    }

    void rule(Class ruleClass, Closure closure) {
        RuleSetUtil.assertClassImplementsRuleInterface(ruleClass)
        Rule rule = ruleClass.newInstance()
        closure.delegate = rule
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        allRuleSet.addRule(rule)
    }

    void rule(String path) {
        def rule = RuleSetUtil.loadRuleScriptFile(path)
        allRuleSet.addRule(rule)
    }

    void rule(String path, Closure closure) {
        def rule = RuleSetUtil.loadRuleScriptFile(path)
        closure.delegate = rule
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        allRuleSet.addRule(rule)
    }

    def propertyMissing(String name) {
        def ruleClass = RuleRegistryHolder.ruleRegistry?.getRuleClass(name)
        assert ruleClass, "No such rule named [$name]. " + MovedRules.getMovedOrRenamedMessageForRuleName(name)
        rule(ruleClass)
    }

    def methodMissing(String name, args) {
        def ruleClass = RuleRegistryHolder.ruleRegistry?.getRuleClass(name)
        assert ruleClass, "No such rule named [$name]. " + MovedRules.getMovedOrRenamedMessageForRuleName(name)
        if (args.size() > 0) {
            rule(ruleClass, args[0])
        }
        else {
            rule(ruleClass)
        }
    }

    @SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
    void description(String description) {
        // Do nothing
    }

    protected RuleSet getRuleSet() {
        allRuleSet
    }
}

class RuleSetDelegate {
    RuleSet ruleSet

    RuleSetDelegate(RuleSet ruleSet) {
        this.ruleSet = new FilteredRuleSet(ruleSet)
    }

    void exclude(String excludeNames) {
        ruleSet.addExclude(excludeNames)
    }

    void include(String includeNames) {
        ruleSet.addInclude(includeNames)
    }

    def methodMissing(String name, args) {
        def rule = findRule(name)
        assert rule, "No such rule named [$name]. " + MovedRules.getMovedOrRenamedMessageForRuleName(name)

        def arg = args[0]
        if (arg instanceof Closure) {
            arg.delegate = rule
            arg.setResolveStrategy(Closure.DELEGATE_FIRST)
            arg.call()
        }
        else {
            // Assume it is a Map
            arg.each { key, value -> rule[key] = value }
        }
    }

    private Rule findRule(String name) {
        ruleSet.rules.find { rule -> rule.name == name }
    }
}
