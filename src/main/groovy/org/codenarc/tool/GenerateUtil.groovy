/*
 * Copyright 2011 the original author or authors.
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
 package org.codenarc.tool

import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.RuleSets
import org.codenarc.ruleset.XmlFileRuleSet
import org.codenarc.util.io.ClassPathResource

/**
 * Contains static utility methods related to the Generate* tools.
 *
 * @author Chris Mair
  */
class GenerateUtil {

    private static final RULE_EXTRA_INFO_FILE = 'codenarc-rule-extrainfo.properties'
    private static Properties ruleExtraInformation

    static Properties getRuleExtraInformation() {
        if (ruleExtraInformation) {
            return ruleExtraInformation
        }
        ruleExtraInformation = new Properties()
        ruleExtraInformation.load(ClassPathResource.getInputStream(RULE_EXTRA_INFO_FILE))
        ruleExtraInformation
    }

    static List createSortedListOfAllRules() {
        def allRuleSet = new CompositeRuleSet()
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            allRuleSet.addRuleSet(ruleSet)
        }
        sortRules(allRuleSet.rules)
    }

    static List sortRules(List rules) {
        def allRules = []
        allRules.addAll(rules)
        allRules.sort { rule -> rule.name }
    }

}
