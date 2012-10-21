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
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.test.AbstractTestCase

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for RuleSetUtil
 *
 * @author Chris Mair
  */
class RuleSetUtilTest extends AbstractTestCase {
    private static final RULESET_XML_FILE = 'rulesets/RuleSet1.xml'
    private static final RULESET_GROOVY_FILE = 'rulesets/GroovyRuleSet1.txt'
    private static final RULE_SCRIPT_FILE = 'rule/DoNothingRule.txt'
    private static final RULE_SCRIPT_FILE_URL = 'file:src/test/resources/rule/DoNothingRule.txt'

    void testAssertClassImplementsRuleInterface_RuleClass() {
        RuleSetUtil.assertClassImplementsRuleInterface(CatchThrowableRule)
    }

    void testAssertClassImplementsRuleInterface_NotARuleClass() {
        shouldFailWithMessageContaining('Rule interface') {
            RuleSetUtil.assertClassImplementsRuleInterface(this.class)
        }
    }

    void testAssertClassImplementsRuleInterface_Null() {
        shouldFailWithMessageContaining('ruleClass') {
            RuleSetUtil.assertClassImplementsRuleInterface(null)
        }
    }

    void testLoadRuleSetFile() {
        assert RuleSetUtil.loadRuleSetFile(RULESET_GROOVY_FILE).class == GroovyDslRuleSet
        assert RuleSetUtil.loadRuleSetFile(RULESET_XML_FILE).class == XmlFileRuleSet
    }

    void testLoadRuleScriptFile() {
        def rule = RuleSetUtil.loadRuleScriptFile(RULE_SCRIPT_FILE)
        assert rule instanceof Rule
        assert rule.name == 'DoNothing'
    }

    void testLoadRuleScriptFile_FileUrl() {
        def rule = RuleSetUtil.loadRuleScriptFile(RULE_SCRIPT_FILE_URL)
        assert rule instanceof Rule
        assert rule.name == 'DoNothing'
    }

    void testLoadRuleScriptFile_NotARule() {
        shouldFailWithMessageContaining('Rule') { RuleSetUtil.loadRuleScriptFile('rule/NotARule.txt') }
    }

    void testLoadRuleScriptFile_FileNotFound() {
        shouldFailWithMessageContaining('DoesNotExist.txt') { RuleSetUtil.loadRuleScriptFile('DoesNotExist.txt') }
    }
}
