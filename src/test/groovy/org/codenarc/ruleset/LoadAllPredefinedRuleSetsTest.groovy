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

import org.codenarc.test.AbstractTestCase
import org.codenarc.test.RuleSets

/**
 * Load all predefined RuleSet files using XmlFileRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class LoadAllPredefinedRuleSetsTest extends AbstractTestCase {

    private static final BASE_MESSAGES_BUNDLE = "codenarc-base-messages"
    private messages

    void testLoadAllPredefinedRuleSets() {
        RuleSets.ALL_RULESET_FILES.each { ruleSetPath ->
            def ruleSet = new XmlFileRuleSet(ruleSetPath)
            def rules = ruleSet.rules
            log("[$ruleSetPath] rules=$rules")
            assert rules

            rules.each { rule -> assertDescriptionInMessagesBundle(rule.name) }
        }
    }

    void setUp() {
        super.setUp()
        messages = ResourceBundle.getBundle(BASE_MESSAGES_BUNDLE)
    }

    private void assertDescriptionInMessagesBundle(String ruleName) {
        assert messages.getString(ruleName + '.description')
        assert messages.getString(ruleName + '.description.html')
    }
}