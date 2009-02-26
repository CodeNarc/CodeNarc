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

import org.apache.log4j.Logger
import org.codenarc.util.PropertyUtil

/**
 * Reads the properties file named "codenarc.properties", if found on the classpath, and applies
 * the property values to matching Rules within a specified <code>RuleSet</code>. If the
 * properties file is not found on the classpath, then do nothing.
 * <p/>
 * For each properties entry of the form <code>[rule-name].[property-name]=[property-value]</code>,
 * the named property for the rule within the RuleSet matching rule-name is set to the
 * specified propery-value. Properties entries not of this form or specifying rule
 * names not within the specified RuleSet are ignored.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class PropertiesFileRuleSetConfigurer {
    static final LOG = Logger.getLogger(PropertiesFileRuleSetConfigurer)
    protected propertiesFilename = 'codenarc.properties'

    /**
     * Configure the rules within the RuleSet from the properties file.
     * Each properties entry of the form <code>[rule-name].[property-name]=[property-value]</code>
     * is used to set the named property of the named rule. Other (non-matching)
     * property entries are ignored.
     * @param ruleSet - the RuleSet to configure; must not be null
     */
    void configure(RuleSet ruleSet) {
        assert ruleSet
        def inputStream = getClass().classLoader.getResourceAsStream(propertiesFilename)
        if (inputStream) {
            LOG.info("Reading RuleSet configuration from properties file [$propertiesFilename].")
            inputStream.withStream { input ->
                def properties = new Properties()
                properties.load(input)
                applyProperties(properties, ruleSet)
            }
        }
        else {
            LOG.info("RuleSet configuration properties file [$propertiesFilename] not found.")
        }
    }

    private applyProperties(Properties properties, RuleSet ruleSet) {
        final PATTERN = ~/(\w*)\.(\w*)/
        properties.each { k, v ->
            def matcher = PATTERN.matcher(k)
            if (matcher.matches()) {
                def ruleName = matcher[0][1]
                def propertyName = matcher[0][2]
                def rule = findRule(ruleSet, ruleName)
                if (rule) {
                    PropertyUtil.setPropertyFromString(rule, propertyName, v)
                }
            }
        }
    }

    private findRule(RuleSet ruleSet, String name) {
        ruleSet.rules.find { rule -> rule.name == name }
    }
}