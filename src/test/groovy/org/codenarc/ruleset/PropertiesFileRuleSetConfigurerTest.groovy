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
import org.codenarc.rule.StubRule
import org.codenarc.test.AbstractTestCase
import org.junit.After
import org.junit.Test

/**
 * Tests for PropertiesFileRuleSetConfigurer
 *
 * @author Chris Mair
  */
class PropertiesFileRuleSetConfigurerTest extends AbstractTestCase {

    private static final String RULE1_NAME = 'rule1'
    private static final String RULE2_NAME = 'rule2'

    private static final String RULE1_MESSAGE = 'abc'
    private static final String RULE2_MESSAGE = 'violation'             // overridden in "codenarc.properties"
    private static final String RULE2_MESSAGE_OVERRIDE = 'override'     // overridden in "override-codenarc.properties"

    private StubRule rule1 = new StubRule(name:RULE1_NAME, priority:1, violationMessage:'abc')
    private StubRule rule2 = new StubRule(name:RULE2_NAME, priority:2, violationMessage:'def')
    private RuleSet ruleSet = new ListRuleSet([rule1, rule2])
    private PropertiesFileRuleSetConfigurer configurer = new PropertiesFileRuleSetConfigurer()

    @Test
    void testConfigure() {
        configurer.configure(ruleSet)
        log(ruleSet.rules)
        assertRuleSetContainsRule(RULE1_NAME, 3, RULE1_MESSAGE)
        assertRuleSetContainsRule(RULE2_NAME, 2, RULE2_MESSAGE)
    }

    @Test
    void testConfigure_OverridePropertiesFilenameThroughSystemProperty() {
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, 'override-codenarc.properties')
        configurer.configure(ruleSet)

        assertRuleSetContainsRule(RULE1_NAME, 2, RULE1_MESSAGE)
        assertRuleSetContainsRule(RULE2_NAME, 2, RULE2_MESSAGE_OVERRIDE)
    }

    @Test
    void testConfigure_OverridePropertiesFilenameThroughSystemProperty_FileUrl() {
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, 'file:src/test/resources/override-codenarc.properties')
        configurer.configure(ruleSet)

        assertRuleSetContainsRule(RULE1_NAME, 2, RULE1_MESSAGE)
        assertRuleSetContainsRule(RULE2_NAME, 2, RULE2_MESSAGE_OVERRIDE)
    }

    @Test
    void testConfigure_PropertiesFileDoesNotExist() {
        configurer.defaultPropertiesFilename = 'DoesNotExist.properties'
        configurer.configure(ruleSet)

        assertRuleSetContainsRule(RULE1_NAME, 1, RULE1_MESSAGE)
        assertRuleSetContainsRule(RULE2_NAME, 2, 'def')
    }

    @Test
    void testConfigure_EmptyRuleSet() {
        ruleSet = new ListRuleSet([])
        configurer.configure(ruleSet)
        assert ruleSet.rules.isEmpty()
    }

    @Test
    void testConfigure_NullRuleSet() {
        shouldFailWithMessageContaining('ruleSet') { configurer.configure(null) }
    }

    @After
    void after() {
        System.clearProperty(CODENARC_PROPERTIES_FILE_PROP)
    }

    private void assertRuleSetContainsRule(String ruleName, int priority, String message) {
        assert ruleSet.rules.find { Rule rule -> rule.name == ruleName && rule.priority == priority && rule.violationMessage == message }
    }

}
