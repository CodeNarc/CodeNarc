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

import static org.codenarc.test.TestUtil.*

import org.codenarc.rule.formatting.SpaceAroundMapEntryColonRule
import org.codenarc.rule.groovyism.ExplicitCallToEqualsMethodRule
import org.codenarc.rule.logging.PrintlnRule
import org.codenarc.ruleregistry.RuleRegistryInitializer
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for JsonFileRuleSet
 *
 * @author Nicolas Vuillamy
  */
class JsonFileRuleSetTest extends AbstractTestCase {

    @Test
    void testNullPath() {
        shouldFailWithMessageContaining('path') { new JsonFileRuleSet(null) }
    }

    @Test
    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new JsonFileRuleSet('') }
    }

    @Test
    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new JsonFileRuleSet('DoesNotExist.json') }
        assertContainsAll(errorMessage, ['DoesNotExist.json', 'does not exist'])
    }

    @Test
    void testMultipleRulesWithProperties() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        final PATH = 'rulesets/JsonRuleSet1.json'
        def ruleSet = new JsonFileRuleSet(PATH)
        def rules = ruleSet.rules

        assert rules[0].class == ExplicitCallToEqualsMethodRule
        assert rules[1].class == PrintlnRule
        assert rules[2].class == SpaceAroundMapEntryColonRule
        assert rules*.enabled == [true, true, false]
        assert rules*.priority == [2, 2, 3]
    }

    @Test
    void testFileUrl() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        final PATH = 'file:src/test/resources/rulesets/JsonRuleSet1.json'
        def ruleSet = new JsonFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules*.class == [ExplicitCallToEqualsMethodRule, PrintlnRule, SpaceAroundMapEntryColonRule]
    }

    @Test
    void testRulesListIsImmutable() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        final PATH = 'rulesets/JsonRuleSet1.json'
        def ruleSet = new JsonFileRuleSet(PATH)
        def rules = ruleSet.rules
        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

}
