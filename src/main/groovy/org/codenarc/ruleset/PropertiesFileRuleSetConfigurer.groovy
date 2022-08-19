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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codenarc.util.PropertyUtil
import org.codenarc.util.io.DefaultResourceFactory
import org.codenarc.util.io.ResourceFactory

/**
 * Reads the properties file named "codenarc.properties", if found on the classpath, and applies
 * the property values to matching Rules within a specified <code>RuleSet</code>. If the
 * properties file is not found on the classpath, then do nothing.
 * <p/>
 * The default name of the properties file ("codenarc.properties") can be overridden by setting
 * the "codenarc.properties.file" system property to the new filename. Note that the new filename
 * is still relative to the classpath, and may optionally contain (relative) path components (e.g.
 * "src/resources/my-codenarc.properties"), but it may be optionally prefixed by any of the valid
 * java.net.URL prefixes, such as "file:" (to load from a relative or absolute filesystem path).
 * <p/>
 * For each properties entry of the form <code>[rule-name].[property-name]=[property-value]</code>,
 * the named property for the rule within the RuleSet matching rule-name is set to the
 * specified property-value. Properties entries not of this form or specifying rule
 * names not within the specified RuleSet are ignored.
 *
 * @author Chris Mair
  */
class PropertiesFileRuleSetConfigurer implements RuleSetConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesFileRuleSetConfigurer)
    private static final String PROPERTIES_FILE_SYSPROP = 'codenarc.properties.file'

    private final ResourceFactory resourceFactory = new DefaultResourceFactory()
    protected String defaultPropertiesFilename = 'codenarc.properties'

    /**
     * Configure the rules within the RuleSet from the properties file (relative to the classpath).
     * The default properties filename is "codenarc.properties", but can be overridden by setting the
     * "codenarc.properties.filename" system property.
     * <p>
     * Each properties entry of the form <code>[rule-name].[property-name]=[property-value]</code>
     * is used to set the named property of the named rule. Other (non-matching)
     * property entries are ignored.
     * @param ruleSet - the RuleSet to configure; must not be null
     * @param propertiesFilename - optional filename of properties file; may be ay be optionally prefixed
     *              by any of the valid java.net.URL prefixes, such as "file:".
     */
    @Override
    void configure(RuleSet ruleSet, String propertiesFilename) {
        assert ruleSet

        def actualPropertiesFilename = getActualPropertiesFile(propertiesFilename)

        try {
            def inputStream = resourceFactory.getResource(actualPropertiesFilename).inputStream
            LOG.info("Reading RuleSet configuration from properties file [$actualPropertiesFilename].")
            inputStream.withStream { input ->
                def properties = new Properties()
                properties.load(input)
                applyProperties(properties, ruleSet)
            }
        }
        catch(IOException e) {
            LOG.info("RuleSet configuration properties file [$actualPropertiesFilename] not found.")
        }
    }

    private String getActualPropertiesFile(String propertiesFilename) {
        return propertiesFilename ?: (System.getProperty(PROPERTIES_FILE_SYSPROP) ?: defaultPropertiesFilename)
    }

    private void applyProperties(Properties properties, RuleSet ruleSet) {
        final PATTERN = ~/(\w*)\.(\w*)/
        properties.each { k, v ->
            def matcher = PATTERN.matcher(k)
            if (matcher.matches()) {
                def ruleName = matcher[0][1]
                def propertyName = matcher[0][2]
                def rule = findRule(ruleSet, ruleName)
                if (rule) {
                    setRuleProperty(rule, propertyName, v)
                }
                else {
                    LOG.warn("No such rule [$ruleName] for property [$k]. "  + MovedRules.getMovedOrRenamedMessageForRuleName(ruleName))
                }
            }
        }
    }

    private void setRuleProperty(Rule rule, String propertyName, String value) {
        if (propertyName == 'name') {
            LOG.warn("Overriding the rule 'name' property is not allowed; rule=" + rule.name)
        } else {
            PropertyUtil.setPropertyFromString(rule, propertyName, value)
        }
    }

    private Rule findRule(RuleSet ruleSet, String name) {
        ruleSet.rules.find { rule -> rule.name == name }
    }
}
