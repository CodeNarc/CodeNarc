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
import org.apache.log4j.Logger
import org.codenarc.ruleset.RuleSets
import org.codenarc.ruleset.XmlFileRuleSet

/**
 * Java application (main() method) that generates the "StarterRuleSet-AllRulesByCategory.groovy.txt.template" file.
 * This file is a valid CodeNarc ruleset file that includes ALL rules distributed with CodeNarc, grouped by the
 * category.
 *
 * @author Chris Mair
  */
class GenerateRuleSetAllRulesByCategory {

    protected static final RULESET_FILE = 'src/site/resources/StarterRuleSet-AllRulesByCategory.groovy.txt'
    private static final TEMPLATE_FILE = 'src/main/resources/templates/StarterRuleSet-AllRulesByCategory.groovy.template'
    private static final LOG = Logger.getLogger(GenerateRuleSetAllRulesByCategory)

    protected static ruleSetFile = RULESET_FILE

    /**
     * Write out all current rules to the 'codenarc-base-rules.properties' properties file
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {

        def rulesByRuleSet = [:]
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            rulesByRuleSet[ruleSetPath] = GenerateUtil.sortRules(ruleSet.rules)
        }

        LOG.info("rulesByCategory=$rulesByRuleSet")

        Properties ruleExtraInformation = GenerateUtil.getRuleExtraInformation()

        def binding = [ruleSets:rulesByRuleSet, ruleExtraInformation:ruleExtraInformation]
        def ruleSetTemplateFile = new File(TEMPLATE_FILE)

        def engine = new SimpleTemplateEngine()
        def ruleSetText = engine.createTemplate(ruleSetTemplateFile).make(binding)

        def outputFile = new File(ruleSetFile)
        outputFile.text = ruleSetText

        LOG.info("Finished writing $ruleSetFile")
    }

}
