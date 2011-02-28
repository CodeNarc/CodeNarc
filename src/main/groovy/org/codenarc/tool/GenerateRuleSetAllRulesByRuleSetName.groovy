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
import org.codenarc.ruleset.RuleSets
import org.codenarc.ruleset.XmlFileRuleSet

/**
 * Java application (main() method) that generates the "SampleRuleSet-AllRulesByRuleSetName.groovy.template" file.
 * This file is a valid CodeNarc ruleset file that includes ALL rules distributed with CodeNarc, grouped by the
 * RuleSet name.
 *
 * @author Chris Mair
 * @version $Revision: $ - $Date:  $
 */
class GenerateRuleSetAllRulesByRuleSetName {

    protected static final RULESET_FILE = 'src/site/resources/SampleRuleSet-AllRulesByRuleSetName.groovy'
    private static final TEMPLATE_FILE = 'src/main/resources/templates/SampleRuleSet-AllRulesByRuleSetName.groovy.template'

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

        println("rulesByRuleSet=$rulesByRuleSet")

        def binding = [ruleSets:rulesByRuleSet]
        def ruleSetTemplateFile = new File(TEMPLATE_FILE)

        def engine = new SimpleTemplateEngine()
        def ruleSetText = engine.createTemplate(ruleSetTemplateFile).make(binding)

        def outputFile = new File(RULESET_FILE)
        outputFile.text = ruleSetText

        println "Finished writing $RULESET_FILE"
    }


}
