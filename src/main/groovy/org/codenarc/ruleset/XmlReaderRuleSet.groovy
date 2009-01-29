/*
 * Copyright 2008 the original author or authors.
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

import org.codenarc.util.PropertyUtil
import org.apache.log4j.Logger

/**
 * A <code>RuleSet</code> implementation that parses Rule definitions from XML read from a
 * <code>Reader</code>. Note that this class attempts to read and parse the XML from within
 * the constructor.
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReaderRuleSet implements RuleSet {
    static final LOG = Logger.getLogger(XmlReaderRuleSet)
    private List rules = []

    /**
     * Construct a new instance on the specified Reader
     * @param reader - the Reader from which the XML will be read; must not be null
     */
    XmlReaderRuleSet(Reader reader) {
        assert reader
        def xml = reader.text
        def ruleset = new XmlParser().parseText(xml)
        ruleset.rule.each { ruleNode ->
            def ruleClassName = ruleNode['@class']
            def rule = Class.forName(ruleClassName.toString()).newInstance()
            rules << rule
            ruleNode.property.each { p ->
                def name = p.attribute('name')
                def value = p.attribute('value')
                PropertyUtil.setPropertyFromString(rule, name, value)
            }
        }
        ruleset.'ruleset-ref'.each { ruleSetRefNode ->
            def ruleSetPath = ruleSetRefNode['@path']
            LOG.debug("Loading ruleset from [$ruleSetPath]")
            loadRuleSetFromFile(ruleSetPath)
        }
        rules = rules.asImmutable()
    }

    private void loadRuleSetFromFile(String path) {
        def ruleSet = new XmlFileRuleSet(path)
        rules.addAll(ruleSet.rules)
    }

    /**
     * @return a List of Rule objects
     */
    List getRules() {
        return rules
    }
}