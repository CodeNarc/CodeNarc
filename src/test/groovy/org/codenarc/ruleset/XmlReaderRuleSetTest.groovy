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
import org.codenarc.test.AbstractTest

/**
 * Tests for XmlReaderRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReaderRuleSetTest extends AbstractTest {

    void testNullReader() {
        shouldFailWithMessageContaining('reader') { new XmlReaderRuleSet(null) }
    }

    void testEmptyReader() {
        def reader = new StringReader('')
        shouldFail { new XmlReaderRuleSet(reader) }
    }

    void testNoRules() {
        def XML = '<ruleset></ruleset>'
        def reader = new StringReader(XML)
        def ruleSet = new XmlReaderRuleSet(reader)
        assert ruleSet.rules == []
    }

    void testOneRule() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>'''
        def reader = new StringReader(XML)
        def ruleSet = new XmlReaderRuleSet(reader)
        def rules = ruleSet.rules
        assert rules*.class == [StubRule]
    }

    void testTwoRules() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'/>
            </ruleset>'''
        def reader = new StringReader(XML)
        def ruleSet = new XmlReaderRuleSet(reader)
        def rules = ruleSet.rules
        log("rules=$rules")
        assert rules*.class == [StubRule, CatchThrowableRule]
    }

    void testTwoRulesWithProperties() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'>
                    <property name='id' value='XXXX'/>
                </rule>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'>
                    <property name='id' value='YYYY'/>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>'''
        def reader = new StringReader(XML)
        def ruleSet = new XmlReaderRuleSet(reader)
        def rules = ruleSet.rules
        log("rules=$rules")
        assert rules*.class == [StubRule, CatchThrowableRule]
        assert rules*.id == ['XXXX', 'YYYY']
        assert rules*.priority == [0, 1]
    }

    void testRulesListIsImmutable() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>'''
        def reader = new StringReader(XML)
        def ruleSet = new XmlReaderRuleSet(reader)
        def rules = ruleSet.rules

        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

}