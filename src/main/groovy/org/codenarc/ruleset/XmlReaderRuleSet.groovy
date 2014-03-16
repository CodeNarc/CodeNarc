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

import groovy.xml.Namespace
import org.codenarc.util.PropertyUtil
import org.codenarc.util.io.ClassPathResource

import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

/**
 * A <code>RuleSet</code> implementation that parses Rule definitions from XML read from a
 * <code>Reader</code>. Note that this class attempts to read and parse the XML from within
 * the constructor.
 *
 * @author Chris Mair
  */
@SuppressWarnings('DuplicateLiteral')
class XmlReaderRuleSet implements RuleSet {

    // W3C_XML_SCHEMA_NS_URI constant is not defined in older versions of javax.xml.XMLConstants 
    private static final XML_SCHEMA_URI = 'http://www.w3.org/2001/XMLSchema'

    private static final NS = new Namespace('http://codenarc.org/ruleset/1.0')
    private static final RULESET_SCHEMA_FILE = 'ruleset-schema.xsd'
    private final List rules = []

    /**
     * Construct a new instance on the specified Reader
     * @param reader - the Reader from which the XML will be read; must not be null
     */
    XmlReaderRuleSet(Reader reader) {
        assert reader
        def xml = reader.text
        validateXml(xml)

        def ruleset = new XmlParser().parseText(xml)
        loadRuleSetRefElements(ruleset)
        loadRuleElements(ruleset)
        loadRuleScriptElements(ruleset)
        rules = rules.asImmutable()
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        rules
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void loadRuleSetRefElements(ruleset) {
        ruleset[NS.'ruleset-ref'].each { ruleSetRefNode ->
            def ruleSetPath = ruleSetRefNode.attribute('path')
            def refRuleSet = RuleSetUtil.loadRuleSetFile(ruleSetPath)
            def allRules = refRuleSet.rules
            def filteredRuleSet = new FilteredRuleSet(refRuleSet)
            ruleSetRefNode[NS.'include'].each { includeNode ->
                def includeRuleName = includeNode.attribute('name')
                filteredRuleSet.addInclude(includeRuleName)
            }
            ruleSetRefNode[NS.'exclude'].each { excludeNode ->
                def excludeRuleName = excludeNode.attribute('name')
                filteredRuleSet.addExclude(excludeRuleName)
            }
            ruleSetRefNode[NS.'rule-config'].each { configNode ->
                def configRuleName = configNode.attribute('name')
                def rule = allRules.find { it.name == configRuleName }
                assert rule, "Rule named [$configRuleName] referenced within <rule-config> was not found. " +
                    MovedRules.getMovedOrRenamedMessageForRuleName(configRuleName)
                configNode[NS.property].each { p ->
                    def name = p.attribute('name')
                    def value = p.attribute('value')
                    PropertyUtil.setPropertyFromString(rule, name, value)
                }
            }
            rules.addAll(filteredRuleSet.rules)
        }
    }

    private void loadRuleElements(ruleset) {
        ruleset[NS.rule].each { ruleNode ->
            def ruleClassName = ruleNode.attribute('class')
            def ruleClass = getClass().classLoader.loadClass(ruleClassName.toString())
            RuleSetUtil.assertClassImplementsRuleInterface(ruleClass)
            def rule = ruleClass.newInstance()
            rules << rule
            setRuleProperties(ruleNode, rule)
        }
    }

    private void loadRuleScriptElements(ruleset) {
        ruleset[NS.'rule-script'].each { ruleScriptNode ->
            def ruleScriptPath = ruleScriptNode.attribute('path')
            def rule = RuleSetUtil.loadRuleScriptFile(ruleScriptPath)
            rules << rule
            setRuleProperties(ruleScriptNode, rule)
        }
    }

    private setRuleProperties(ruleNode, rule) {
        ruleNode[NS.property].each { p ->
            def name = p.attribute('name')
            def value = p.attribute('value')
            PropertyUtil.setPropertyFromString(rule, name, value)
        }
    }

    private void validateXml(String xml) {
        def factory = SchemaFactory.newInstance(XML_SCHEMA_URI)
        def schema = factory.newSchema(new StreamSource(getSchemaXmlInputStream()))
        def validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(xml)))
    }

    private InputStream getSchemaXmlInputStream() {
        ClassPathResource.getInputStream(RULESET_SCHEMA_FILE)
    }
}
