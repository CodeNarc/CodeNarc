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
import org.codenarc.rule.TestPathRule
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.test.AbstractTest

/**
 * Tests for XmlFileRuleSet
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class XmlFileRuleSetTest extends AbstractTest {

    void testNullPath() {
        shouldFailWithMessageContaining('path') { new XmlFileRuleSet(null) }
    }

    void testEmptyPath() {
        shouldFailWithMessageContaining('path') { new XmlFileRuleSet('') }
    }

    void testFileDoesNotExist() {
        def errorMessage = shouldFail { new XmlFileRuleSet('DoesNotExist.xml') }
        assertContainsAll(errorMessage, ['DoesNotExist.xml', 'does not exist'])
    }

    void testOneRule() {
        final PATH = 'rulesets/RuleSet1.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules*.class == [TestPathRule]
    }

    void testTwoRulesWithProperties() {
        final PATH = 'rulesets/RuleSet2.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        assert rules*.class == [StubRule, CatchThrowableRule]
        assert rules*.id == ['XXXX', 'YYYY']
        assert rules*.priority == [0, 1]
    }

    void testRulesListIsImmutable() {
        final PATH = 'rulesets/RuleSet1.xml'
        def ruleSet = new XmlFileRuleSet(PATH)
        def rules = ruleSet.rules
        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

}