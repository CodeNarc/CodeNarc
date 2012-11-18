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

import org.codenarc.rule.StubRule

import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.test.AbstractTestCase
import org.junit.Test

import static org.codenarc.test.TestUtil.*
import org.codenarc.rule.FakePathRule

/**
 * Tests for XmlFileRuleSet
 *
 * @author Chris Mair
  */
class XmlFileRuleSetTest extends AbstractTestCase {

    @Test
    void testNullPath() {
        shouldFailWithMessageContaining('path') { new XmlFileRuleSet(null) }
    }

    @Test
    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new XmlFileRuleSet('') }
    }

    @Test
    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new XmlFileRuleSet('DoesNotExist.xml') }
        assertContainsAll(errorMessage, ['DoesNotExist.xml', 'does not exist'])
    }

    @Test
    void testOneRule() {
        final PATH = 'rulesets/RuleSet1.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules*.class == [FakePathRule]
    }

    @Test
    void testMultipleRulesWithProperties() {
        final PATH = 'rulesets/RuleSet2.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules[0].class == StubRule
        assert rules[1].class == CatchThrowableRule
        assert rules[2].class.name == 'DoNothingRule'   // Rule script, so class is dynamic
        assert rules*.name == ['XXXX', 'YYYY', 'DoNothing']
        assert rules*.priority == [0, 1, 2]
    }

    @Test
    void testFileUrl() {
        final PATH = 'file:src/test/resources/rulesets/RuleSet1.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules*.class == [FakePathRule]
    }

    @Test
    void testRulesListIsImmutable() {
        final PATH = 'rulesets/RuleSet1.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

}
