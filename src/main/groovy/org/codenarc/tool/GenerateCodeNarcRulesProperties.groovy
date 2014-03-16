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

import org.apache.log4j.Logger
import org.codenarc.ruleregistry.PropertiesFileRuleRegistry

/**
 * Java application (main() method) that generates the 'codenarc-base-rules.properties' properties file.
 * This properties file contains <ruleName> = <ruleClass> properties for all CodeNarc rules.
 *
 * @see org.codenarc.ruleregistry.PropertiesFileRuleRegistry
 *
 * @author Chris Mair
  */
class GenerateCodeNarcRulesProperties {

    protected static final PROPERTIES_FILE = PropertiesFileRuleRegistry.PROPERTIES_FILE
    private static final LOG = Logger.getLogger(GenerateCodeNarcRulesProperties)

    protected static propertiesFile = PROPERTIES_FILE

    /**
     * Write out all current rules to the 'codenarc-base-rules.properties' properties file
     * @param args - command-line args (not used)
     */
    static void main(String[] args) {
        def sortedRules = GenerateUtil.createSortedListOfAllRules()
        LOG.debug("sortedRules=$sortedRules")
        def propertiesFile = new File(propertiesFile)
        propertiesFile.withWriter { writer ->
            writer.println '# CodeNarc Rules (see PropertiesFileRuleRegistry): ' + new Date()
            sortedRules.each { rule ->
                writer.println "${rule.name} = ${rule.class.name}"
            }
        }
        LOG.info("Finished writing ${sortedRules.size()} rules to $propertiesFile")
    }

}
