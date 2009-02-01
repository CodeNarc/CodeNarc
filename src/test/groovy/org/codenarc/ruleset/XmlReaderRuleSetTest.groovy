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
import org.codenarc.rule.imports.DuplicateImportRule
import org.codenarc.rule.exceptions.CatchThrowableRule
import org.codenarc.rule.basic.EmptyIfStatementRule
import org.codenarc.rule.TestPathRule
import org.codenarc.test.AbstractTest
import org.codenarc.rule.Rule

/**
 * Tests for XmlReaderRuleSet
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class XmlReaderRuleSetTest extends AbstractTest {
    private List rules

    void testNullReader() {
        shouldFailWithMessageContaining('reader') { new XmlReaderRuleSet(null) }
    }

    void testEmptyReader() {
        def reader = new StringReader('')
        shouldFail { new XmlReaderRuleSet(reader) }
    }

    void testNoRules() {
        def XML = '<ruleset></ruleset>'
        parseXmlRuleSet(XML)
        assert rules == []
    }

    void testOneRule() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule])
    }

    void testTwoRules() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'/>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, CatchThrowableRule])
    }

    void testTwoRulesWithProperties() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'>
                    <property name='name' value='XXXX'/>
                </rule>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'>
                    <property name='name' value='YYYY'/>
                    <property name='priority' value='1'/>
                </rule>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, CatchThrowableRule])
        assert rules*.name == ['XXXX', 'YYYY']
        assert rules*.priority == [0, 1]
    }

    void testNestedRuleSet() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.exceptions.CatchThrowableRule'>
                    <property name='priority' value='1'/>
                </rule>
                <ruleset-ref path='rulesets/RuleSet1.xml'/>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([CatchThrowableRule, TestPathRule])
        assert rules[0].priority == 1
    }

    void testDeeplyNestedRuleSet() {
        def XML = '''
            <ruleset>
                <ruleset-ref path='rulesets/RuleSet3.xml'/>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'/>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, DuplicateImportRule, CatchThrowableRule, TestPathRule, EmptyIfStatementRule])
        assert findRule('DuplicateImport').priority == 1
        assert findRule('EmptyIfStatement').priority == 1
        assert findRule('CatchThrowable').priority == 1
    }

    void testNestedRuleSet_Excludes() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'/>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <exclude-rule name='TestPath'/>
                    <exclude-rule name='EmptyIfStatement'/>
                </ruleset-ref>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([DuplicateImportRule, CatchThrowableRule])
    }

    void testNestedRuleSet_IncludesAndExcludes() {
        def XML = '''
            <ruleset>
                <ruleset-ref path='rulesets/RuleSet3.xml'>
                    <exclude-rule name='Stub'/>
                </ruleset-ref>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <include-rule name='TestPath'/>
                    <include-rule name='EmptyIfStatement'/>
                </ruleset-ref>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([DuplicateImportRule, TestPathRule, EmptyIfStatementRule])
    }

    void testNestedRuleSet_IncludesExcludesAndConfig() {
        def XML = '''
            <ruleset>
                <ruleset-ref path='rulesets/RuleSet3.xml'>
                    <include-rule name='Stub'/>
                </ruleset-ref>
                <rule class='org.codenarc.rule.imports.DuplicateImportRule'>
                    <property name='priority' value='1'/>
                </rule>
                <ruleset-ref path='rulesets/NestedRuleSet1.xml'>
                    <rule-config name='CatchThrowable'>
                        <property name='priority' value='3'/>
                    </rule-config>
                    <exclude-rule name='TestPath'/>
                </ruleset-ref>
            </ruleset>'''
        parseXmlRuleSet(XML)
        assertRuleClasses([StubRule, DuplicateImportRule, CatchThrowableRule, EmptyIfStatementRule])
        assert findRule('CatchThrowable').priority == 3
    }

    void testRulesListIsImmutable() {
        def XML = '''
            <ruleset>
                <rule class='org.codenarc.rule.StubRule'/>
            </ruleset>'''
        parseXmlRuleSet(XML)

        shouldFail(UnsupportedOperationException) { rules.clear() }
    }

    //--------------------------------------------------------------------------
    // Internal Helper Methods
    //--------------------------------------------------------------------------
    
    private void parseXmlRuleSet(String xml) {
        def reader = new StringReader(xml)
        def ruleSet = new XmlReaderRuleSet(reader)
        rules = ruleSet.rules
        log("rules=$rules")
    }

    private void assertRuleClasses(List classes) {
        assertEqualSets(rules*.class, classes)
    }

    private Rule findRule(String name) {
        return rules.find { it.name == name }
    }

}