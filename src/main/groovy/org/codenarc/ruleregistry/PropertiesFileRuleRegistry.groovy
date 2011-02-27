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
package org.codenarc.ruleregistry

import org.apache.log4j.Logger
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.RuleSets
import org.codenarc.ruleset.XmlFileRuleSet
import org.codenarc.util.io.ClassPathResource

/**
 * Implementation of RuleRegistry that loads the rules from the 'codenarc-base-rules.properties' properties file
 *
 * @author Chris Mair
 * @version $Revision: $ - $Date:  $
 */
class PropertiesFileRuleRegistry implements RuleRegistry {

    private static final LOG = Logger.getLogger(PropertiesFileRuleRegistry)
    private static final PROPERTIES_FILE = 'codenarc-base-rules.properties'
    private static final PROPERTIES_FILE_PATH = "src/main/resources/$PROPERTIES_FILE"

    private Properties properties

    PropertiesFileRuleRegistry() {
        loadRules()
    }

    /**
     * Return the Rule Class for the specified name or else null
     * @param ruleName - the rule name
     * @return the associated Rule Class or null if no Rule has been registered for the specified name
     */
    Class getRuleClass(String ruleName) {
        String className = properties[ruleName]
        return className ? Class.forName(className) : null
    }

    private void loadRules() {
        def startTime = System.currentTimeMillis()
        def inputStream = ClassPathResource.getInputStream(PROPERTIES_FILE)
        properties = new Properties()
        properties.load(inputStream)
        def elapsedTime = System.currentTimeMillis() - startTime
        LOG.info("Loaded properties file in ${elapsedTime}ms; ${properties.size()} rules")
    }

    //--------------------------------------------------------------------------
    // Development-Time Utilities
    //--------------------------------------------------------------------------

    /**
     * Write out all current rules to the 'codenarc-base-rules.properties' properties file
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {
        def allRuleSet = new CompositeRuleSet()
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            allRuleSet.addRuleSet(ruleSet)
        }
        def allRules = []
        allRules.addAll(allRuleSet.rules)
        def sortedRules = allRules.sort { rule -> rule.name }
        println("sortedRules=$sortedRules")
        def propertiesFile = new File(PROPERTIES_FILE_PATH)
        propertiesFile.withWriter { writer ->
            writer.println '# CodeNarc Rules (see PropertiesFileRuleRegistry)'
            sortedRules.each { rule ->
                writer.println "${rule.name} = ${rule.class.name}"
            }
        }
        println "Finished writing ${sortedRules.size()} rules to $PROPERTIES_FILE_PATH"
    }

}
