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

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

import org.codenarc.rule.Rule
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.ruleregistry.RuleRegistryInitializer
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for RuleSetUtil
 *
 * @author Chris Mair
 * @author Nicolas Vuillamy
  */
class RuleSetUtilTest extends AbstractTestCase {

    private static final RULESET_XML_FILE = 'rulesets/RuleSet1.xml'
    private static final RULESET_JSON_FILE = 'rulesets/JsonRuleSet1.json'
    private static final RULESET_GROOVY_FILE = 'rulesets/GroovyRuleSet1.txt'
    private static final RULE_SCRIPT_FILE = 'rule/DoNothingRule.txt'
    private static final RULE_SCRIPT_FILE_URL = 'file:src/test/resources/rule/DoNothingRule.txt'
    private static final RULESET_AS_JSON = '''
            {
                "org.codenarc.rule.StubRule": { "name": "XXXX"}
            }
            '''.trim()

    @Test
    void testAssertClassImplementsRuleInterface_RuleClass() {
        RuleSetUtil.assertClassImplementsRuleInterface(CatchThrowableRule)
    }

    @Test
    void testAssertClassImplementsRuleInterface_NotARuleClass() {
        shouldFailWithMessageContaining('Rule interface') {
            RuleSetUtil.assertClassImplementsRuleInterface(this.class)
        }
    }

    @Test
    void testAssertClassImplementsRuleInterface_Null() {
        shouldFailWithMessageContaining('ruleClass') {
            RuleSetUtil.assertClassImplementsRuleInterface(null)
        }
    }

    @Test
    void testLoadRuleSetFile() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        assert RuleSetUtil.loadRuleSetFile(RULESET_GROOVY_FILE).class == GroovyDslRuleSet
        assert RuleSetUtil.loadRuleSetFile(RULESET_XML_FILE).class == XmlFileRuleSet
        assert RuleSetUtil.loadRuleSetFile(RULESET_JSON_FILE).class == JsonFileRuleSet
    }

    @Test
    void testLoadRuleSetString() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        assert RuleSetUtil.loadRuleSetFromString(RULESET_AS_JSON).class == JsonReaderRuleSet
    }

    @Test
    void testLoadRuleScriptFile() {
        def rule = RuleSetUtil.loadRuleScriptFile(RULE_SCRIPT_FILE)
        assert rule instanceof Rule
        assert rule.name == 'DoNothing'
    }

    @Test
    void testLoadRuleScriptFile_FileUrl() {
        def rule = RuleSetUtil.loadRuleScriptFile(RULE_SCRIPT_FILE_URL)
        assert rule instanceof Rule
        assert rule.name == 'DoNothing'
    }

    @Test
    void testLoadRuleScriptFile_useCurrentThreadContextClassLoader() {
        System.setProperty(RuleSetUtil.CLASS_LOADER_SYS_PROP, 'true')
        def rule = RuleSetUtil.loadRuleScriptFile(RULE_SCRIPT_FILE)
        assert rule instanceof Rule
        assert rule.name == 'DoNothing'
    }

    @Test
    void testLoadRuleScriptFile_NotARule() {
        shouldFailWithMessageContaining('Rule') { RuleSetUtil.loadRuleScriptFile('rule/NotARule.txt') }
    }

    @Test
    void testLoadRuleScriptFile_FileNotFound() {
        shouldFailWithMessageContaining('DoesNotExist.txt') { RuleSetUtil.loadRuleScriptFile('DoesNotExist.txt') }
    }
}
