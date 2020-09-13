/*
 * Copyright 2020 the original author or authors.
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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A <code>RuleSet</code> implementation that parses a string and redirects to
 * Xml or Json ruleset parser
 *
 * @author Nicolas Vuillamy
 */
class StringRuleSet implements RuleSet {

    private static final Logger LOG = LoggerFactory.getLogger(StringRuleSet)
    private final String ruleSetString
    private final List rules

    StringRuleSet(String ruleSetString) {
        assert ruleSetString
        this.ruleSetString = ruleSetString
        LOG.debug("Loading ruleset from [$ruleSetString]")

        rules = ruleSetString.trim().startsWith('{') ? new JsonReaderRuleSet(new StringReader(ruleSetString)).getRules() :
                ruleSetString.trim().startsWith('<') ? new XmlReaderRuleSet(new StringReader(ruleSetString)).getRules() :
                        null
        assert rules, "Unable to parse ruleset from string $ruleSetString"
     }

    /**
     * @return a List of Rule objects
     */
    @Override
    List getRules() {
        rules
    }

}
