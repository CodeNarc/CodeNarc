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
import org.codenarc.util.io.ClassPathResource

/**
 * Implementation of RuleRegistry that loads the rules from the 'codenarc-base-rules.properties' properties file.
 *
 * @see org.codenarc.tool.GenerateCodeNarcRulesProperties
 *
 * @author Chris Mair
  */
class PropertiesFileRuleRegistry implements RuleRegistry {

    private static final LOG = Logger.getLogger(PropertiesFileRuleRegistry)
    private static final PROPERTIES_FILENAME = 'codenarc-base-rules.properties'
    public static final PROPERTIES_FILE = "src/main/resources/$PROPERTIES_FILENAME"

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
        return className ? PropertiesFileRuleRegistry.getClassLoader().loadClass(className) : null
    }

    private void loadRules() {
        def startTime = System.currentTimeMillis()
        def inputStream = ClassPathResource.getInputStream(PROPERTIES_FILENAME)
        properties = new Properties()
        properties.load(inputStream)
        def elapsedTime = System.currentTimeMillis() - startTime
        LOG.info("Loaded properties file in ${elapsedTime}ms; ${properties.size()} rules")
    }

}
