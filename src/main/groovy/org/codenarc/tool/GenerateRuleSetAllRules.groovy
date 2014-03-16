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

/**
 * Java application (main() method) that generates the "StarterRuleSet-AllRules.groovy.txt.template" file.
 * This file is a valid CodeNarc ruleset file that includes ALL rules distributed with CodeNarc.
 *
 * @author Chris Mair
  */
class GenerateRuleSetAllRules {

    protected static final RULESET_FILE = 'src/site/resources/StarterRuleSet-AllRules.groovy.txt'
    private static final TEMPLATE_FILE = 'src/main/resources/templates/StarterRuleSet-AllRules.groovy.template'
    private static final LOG = Logger.getLogger(GenerateRuleSetAllRules)

    protected static ruleSetFile = RULESET_FILE

    /**
     * Write out all current rules to the 'codenarc-base-rules.properties' properties file
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {
        def sortedRules = GenerateUtil.createSortedListOfAllRules()
        LOG.debug("sortedRules=$sortedRules")

        Properties ruleExtraInformation = GenerateUtil.getRuleExtraInformation()

        def binding = [rules:sortedRules, ruleExtraInformation:ruleExtraInformation]
        def ruleSetTemplateFile = new File(TEMPLATE_FILE)

        def engine = new SimpleTemplateEngine()
        def ruleSetText = engine.createTemplate(ruleSetTemplateFile).make(binding)

        def outputFile = new File(ruleSetFile)
        outputFile.text = ruleSetText

        LOG.info("Finished writing ${sortedRules.size()} rules to $ruleSetFile")
    }

}
