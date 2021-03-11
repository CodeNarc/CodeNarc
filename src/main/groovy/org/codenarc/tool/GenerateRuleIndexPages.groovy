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

import groovy.text.SimpleTemplateEngine
import org.codenarc.rule.Rule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codenarc.ruleset.RuleSets
import org.codenarc.ruleset.XmlFileRuleSet

/**
 * Java application (main() method) that generates the "codenarc-rule-index.md.template" file.
 * which is the markdown file for the Rule Index page of the project web site.
 *
 * @author Chris Mair
  */
class GenerateRuleIndexPages {

    protected static final String RULE_INDEX_BY_CATEGORY_FILE = 'docs/codenarc-rule-index.md'
    protected static final String RULE_INDEX_BY_NAME_FILE = 'docs/codenarc-rule-index-by-name.md'
    private static final String BY_CATEGORY_TEMPLATE_FILE = 'src/main/resources/templates/codenarc-rule-index.md.template'
    private static final String BY_NAME_TEMPLATE_FILE = 'src/main/resources/templates/codenarc-rule-index-by-name.md.template'
    private static final Logger LOG = LoggerFactory.getLogger(GenerateRuleIndexPages)

    protected static String ruleIndexByCategoryFile = RULE_INDEX_BY_CATEGORY_FILE
    protected static String ruleIndexByNameFile = RULE_INDEX_BY_NAME_FILE

    /**
     * Write out rule index by rule category to the 'codenarc-rule-index.apt' APT file,
     * and the rule index by name to the 'codenarc-rule-index-by-name.apt' APT file.
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {
        def rulesByRuleSet = [:]
        def numberOfRules = 0
        def ruleToRuleSetMap = new TreeMap({ a, b -> a.name <=> b.name })
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            def ruleSetName = ruleSetPath - 'rulesets/' - '.xml'
            rulesByRuleSet[ruleSetName] = GenerateUtil.sortRules(ruleSet.rules)
            ruleSet.rules.each { rule ->
                ruleToRuleSetMap[rule] = ruleSetName
            }
            numberOfRules += rulesByRuleSet[ruleSetName].size()
        }

        Properties ruleExtraInformation = GenerateUtil.getRuleExtraInformation()

        generateIndexByCategoryFile(rulesByRuleSet, numberOfRules, ruleExtraInformation)
        generateIndexByNameFile(ruleToRuleSetMap, numberOfRules, ruleExtraInformation)

        LOG.info("Finished writing $ruleIndexByCategoryFile")
    }

    private static void generateIndexByCategoryFile(Map<String, Rule> rulesByRuleSet, int numberOfRules, Properties ruleExtraInformation) {
        def engine = new SimpleTemplateEngine()
        def binding = [ruleSets: rulesByRuleSet, numberOfRules: numberOfRules, ruleExtraInformation: ruleExtraInformation]
        def byCategoryIndexTemplateFile = new File(BY_CATEGORY_TEMPLATE_FILE)

        def ruleSetIndexText = engine.createTemplate(byCategoryIndexTemplateFile).make(binding)

        def byCategoryOutputFile = new File(ruleIndexByCategoryFile)
        byCategoryOutputFile.text = ruleSetIndexText
    }

    private static void generateIndexByNameFile(Map<Rule, String> ruleToRuleSetMap, int numberOfRules, Properties ruleExtraInformation) {
        def engine = new SimpleTemplateEngine()
        def binding = [ruleToRuleSetMap: ruleToRuleSetMap, numberOfRules: numberOfRules, ruleExtraInformation: ruleExtraInformation]
        def byNameIndexTemplateFile = new File(BY_NAME_TEMPLATE_FILE)

        def ruleSetIndexFileText = engine.createTemplate(byNameIndexTemplateFile).make(binding)

        def byNameOutputFile = new File(ruleIndexByNameFile)
        byNameOutputFile.text = ruleSetIndexFileText
    }

}
