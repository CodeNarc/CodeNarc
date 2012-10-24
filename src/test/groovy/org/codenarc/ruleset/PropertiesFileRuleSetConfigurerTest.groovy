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

import org.codenarc.rule.StubRule
import org.codenarc.test.AbstractTestCase
import org.junit.Before
import org.junit.Test

import static org.codenarc.test.TestUtil.shouldFailWithMessageContaining

/**
 * Tests for PropertiesFileRuleSetConfigurer
 *
 * @author Chris Mair
  */
class PropertiesFileRuleSetConfigurerTest extends AbstractTestCase {

    private ruleSet
    private rule1 = new StubRule(name:'rule1', priority:1, violationMessage:'abc')
    private rule2 = new StubRule(name:'rule2', priority:2, violationMessage:'def')
    private configurer

    @Test
    void testConfigure() {
        configurer.configure(ruleSet)
        assert ruleMap() == [rule1:[3, 'abc'], rule99:[2, 'violation']], ruleMap()
    }

    @Test
    void testConfigure_OverridePropertiesFilenameThroughSystemProperty() {
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, 'override-codenarc.properties')
        configurer.configure(ruleSet)
        assert ruleMap() == [rule1:[2, 'abc'], rule99:[2, 'override']], ruleMap()
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, '')
    }

    @Test
    void testConfigure_OverridePropertiesFilenameThroughSystemProperty_FileUrl() {
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, 'file:src/test/resources/override-codenarc.properties')
        configurer.configure(ruleSet)
        assert ruleMap() == [rule1:[2, 'abc'], rule99:[2, 'override']], ruleMap()
        System.setProperty(CODENARC_PROPERTIES_FILE_PROP, '')
    }

    @Test
    void testConfigure_PropertiesFileDoesNotExist() {
        configurer.defaultPropertiesFilename = 'DoesNotExist.properties'
        configurer.configure(ruleSet)
        assert ruleMap() == [rule1:[1, 'abc'], rule2:[2, 'def']]
    }

    @Test
    void testConfigure_EmptyRuleSet() {
        ruleSet = new ListRuleSet([]) 
        configurer.configure(ruleSet)
        assert ruleMap() == [:], ruleMap()
    }

    @Test
    void testConfigure_NullRuleSet() {
        shouldFailWithMessageContaining('ruleSet') { configurer.configure(null) }
    }

    @Before
    void setUpPropertiesFileRuleSetConfigurerTest() {
        configurer = new PropertiesFileRuleSetConfigurer()
        ruleSet = new ListRuleSet([rule1, rule2])
    }

    private ruleMap() {
        def map = [:]
        ruleSet.rules.each { rule -> map[rule.name] = [rule.priority, rule.violationMessage] }
         map
    }
}
