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
import org.codenarc.test.AbstractTestCase
import org.junit.Test

/**
 * Tests for JsonReaderRuleSetTest
 *
 * @author Nicolas Vuillamy
  */
class StringRuleSetTest extends AbstractTestCase {

    private static final NAMESPACE = '''
        xmlns="http://codenarc.org/ruleset/1.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://codenarc.org/ruleset/1.0 http://codenarc.org/ruleset-schema.xsd"
        xsi:noNamespaceSchemaLocation="http://codenarc.org/ruleset-schema.xsd" '''
    private List rules

    @Test
    void testNullString() {
        shouldFailWithMessageContaining('ruleSetString') { new StringRuleSet(null) }
    }

    @Test
    void testEmptyString() {
        shouldFail { new StringRuleSet('') }
    }

    @Test
    void testParseXml() {
        final XML = """
            <ruleset $NAMESPACE>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>"""
        parseStringRuleSet(XML)
        assertRuleClasses([StubRule])
    }

    @Test
    void testParseJson() {
        final JSON = '''
            {
                "org.codenarc.rule.StubRule": {}
            }
            '''
        parseStringRuleSet(JSON)
        assertRuleClasses([StubRule])
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------

    private void parseStringRuleSet(String ruleSetsString) {
        def ruleSet = new StringRuleSet(ruleSetsString)
        rules = ruleSet.rules
        log("rules=$rules")
    }

    private void assertRuleClasses(List classes) {
        assertEqualSets(rules*.class, classes)
    }

}
