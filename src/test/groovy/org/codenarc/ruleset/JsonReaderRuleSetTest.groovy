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
import org.codenarc.rule.StubRule
import org.codenarc.rule.exceptions.CatchRuntimeExceptionRule
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.ruleregistry.RuleRegistryInitializer
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for JsonReaderRuleSetTest
 *
 * @author Nicolas Vuillamy
  */
class JsonReaderRuleSetTest extends AbstractTestCase {

    private List rules

    @Test
    void testNullReader() {
        shouldFailWithMessageContaining('reader') { new JsonReaderRuleSet(null) }
    }

    @Test
    void testEmptyReader() {
        def reader = new StringReader('')
        shouldFail { new JsonReaderRuleSet(reader) }
    }

    @Test
    void testNoRules() {
        final JSON = '{}'
        parseJsonRuleSet(JSON)
        assert rules == []
    }

    @Test
    void testOneRule() {
        final JSON = '''
            {
                "org.codenarc.rule.StubRule": {}
            }
            '''
        parseJsonRuleSet(JSON)
        assertRuleClasses([StubRule])
    }

    @Test
    void testTwoRules() {
        final JSON = '''
            {
                "org.codenarc.rule.StubRule": {},
                "org.codenarc.rule.exceptions.CatchThrowableRule": {}
            }
            '''
        parseJsonRuleSet(JSON)
        assertRuleClasses([StubRule, CatchThrowableRule])
    }

    @Test
    void testTwoRulesWithProperties() {
        final JSON = '''
            {
                "org.codenarc.rule.StubRule": { "name": "XXXX", "enabled": false },
                "org.codenarc.rule.exceptions.CatchThrowableRule": { "name": "YYYY", "priority": 1 }
            }
            '''
        parseJsonRuleSet(JSON)
        assertRuleClasses([StubRule, CatchThrowableRule])
        assert rules*.name == ['XXXX', 'YYYY']
        assert rules*.priority == [0, 1]
        assert rules*.enabled == [false, true]
    }

    @Test
    void testTwoRulesWithPropertiesBaseRuleName() {
        new RuleRegistryInitializer().initializeRuleRegistry()
        final JSON = '''
            {
                "CatchRuntimeException": { "name": "XXXX", "enabled": false },
                "CatchThrowable": { "name": "YYYY", "priority": 1 }
            }
            '''
        parseJsonRuleSet(JSON)
        assertRuleClasses([CatchRuntimeExceptionRule, CatchThrowableRule])
        assert rules*.name == ['XXXX', 'YYYY']
        assert rules*.priority == [2, 1]
        assert rules*.enabled == [false, true]
    }

    @Test
    void testRuleClassNotFound() {
        final JSON = '''
            {
                "org.codenarc.rule.DoesNotExist": {}
            }
            '''
        shouldFail(ClassNotFoundException) { parseJsonRuleSet(JSON) }
    }

    @Test
    void testRuleClassNotARule() {
        final JSON = '''
            {
                "java.lang.Object": {}
            }
            '''
        shouldFailWithMessageContaining('java.lang.Object') { parseJsonRuleSet(JSON) }
    }

    @Test
    void testNestedRuleSet_ConfigRulePropertyDoesNotExist() {
        final JSON = '''
            {
                "org.codenarc.rule.exceptions.CatchThrowableRule": { "DoesNotExist": "123456789" }
            }
            '''
        shouldFailWithMessageContaining('DoesNotExist') { parseJsonRuleSet(JSON) }
    }

    @Test
    void testRulesListIsImmutable() {
        final JSON = '''
            {
                "org.codenarc.rule.StubRule": {}
            }
            '''
        parseJsonRuleSet(JSON)

        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

    @Test
    void testRuleNameNotFound() {
        final JSON = '''
            {
                "DoesNotExist": {}
            }
            '''
        shouldFailWithMessageContaining('DoesNotExist') { parseJsonRuleSet(JSON) }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void parseJsonRuleSet(String json) {
        def reader = new StringReader(json)
        def ruleSet = new JsonReaderRuleSet(reader)
        rules = ruleSet.rules
        log("rules=$rules")
    }

    private void assertRuleClasses(List classes) {
        assertEqualSets(rules*.class, classes)
    }

}
